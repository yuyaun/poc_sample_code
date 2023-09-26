package com.spdemo.service.jetstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@DependsOn("jetStreamStreamManager")
@Slf4j
public class JetStreamConnectionManager implements JetStreamService {

    private String natsServer;
    private String streamName;

    public JetStreamConnectionManager(@Value("${nats.server.ip}") String natsServer, @Value("${nats.jetstream.streamName}") String streamName) {
        this.natsServer = natsServer;
        this.streamName = streamName;
    }

    public static final ConnectionListener DEFAULT_CONNECTION_LISTENER = (conn, type) -> System.out.println("Status change " + type);

    public static final ErrorListener DEFAULT_ERROR_LISTENER = new ErrorListener() {
        public void exceptionOccurred(Connection conn, Exception exp) {
            System.out.println("Exception " + exp.getMessage());
        }

        public void errorOccurred(Connection conn, String type) {
            System.out.println("Error " + type);
        }

        public void slowConsumerDetected(Connection conn, Consumer consumer) {
            System.out.println("Slow consumer");
        }
    };

    public static Options createConnectionOptions(String server, boolean allowReconnect, ErrorListener el, ConnectionListener cl) throws Exception {

        if (el == null) {
            el = new ErrorListener() {
            };
        }

        Options.Builder builder = new Options.Builder()
                .server(server)
                .connectionTimeout(Duration.ofSeconds(5))   // connection timeout
                .pingInterval(Duration.ofSeconds(10))
                .reconnectWait(Duration.ofSeconds(1))  // reconnect
                .connectionListener(cl)
                .errorListener(el);

        if (!allowReconnect) {
            builder = builder.noReconnect();
        } else {
            builder = builder.maxReconnects(-1);
        }

        return builder.build();
    }

//    // jetstream Config
//    private String streamName = "ERP_PO";
//    private Collection<String> subjects = Arrays.asList(
//            "ERP.PO.Upload.Incoming", // PO 待處理
//            "ERP.PO.Result.Updated"   // ERP 已更新收到 PO 結果
//    );


    List<Connection> connectionsForPushSubscribe = new ArrayList();
    Map<String, Connection> connectionsForPullSubscribe = new ConcurrentHashMap<>();
    List<Connection> connectionsForPublish = new ArrayList();

    @PreDestroy
    public void destory() {
        for (Connection nc : connectionsForPushSubscribe) {
            try {
                nc.close();
            } catch (Exception e) {
            }
        }
        for (Connection nc : connectionsForPullSubscribe.values()) {
            try {
                nc.close();
            } catch (Exception e) {
            }
        }
        for (Connection nc : connectionsForPublish) {
            try {
                nc.close();
            } catch (Exception e) {
            }
        }
    }

    @PostConstruct
    public void init() {
        try {

            // 建立 Nats Connection

            Connection nc = Nats.connect(createConnectionOptions(natsServer, true, DEFAULT_ERROR_LISTENER, DEFAULT_CONNECTION_LISTENER));
            connectionsForPublish.add(nc);


        } catch (Throwable ex) {
//            log.error("manage jetstream error !", ex);
            throw new IllegalStateException("manage nats jetstream error !", ex);
        }
    }



    @Override
    public void publish(String subject, Headers headers, Object event) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(event);
        publish(subject, headers, data);
    }

    @Override
    public void publish(String subject, Headers headers, String payload) throws Exception {
        if (connectionsForPublish.size() == 0)
            throw new IllegalStateException("nats connection is empty !");

        Connection nc = connectionsForPublish.get(0);
        JetStream js = nc.jetStream();

        Message msg = NatsMessage.builder()
                .subject(subject)
                .headers(headers)
                .data(payload, StandardCharsets.UTF_8)
                .build();

        PublishAck pa = js.publish(msg);
        log.info(String.format("Published message %s on subject %s, stream %s, seqno %d.\n",
                payload, subject, pa.getStream(), pa.getSeqno()));
    }

    @Override
    public void pullSubscribe(String subject, String durableName, Integer BATCH_SIZE, java.util.function.Consumer<Message> consumer) throws Exception {
        // 一個 Subscribe 一個 connection
        if(!this.connectionsForPullSubscribe.containsKey(subject)) {
            Connection nc = Nats.connect(createConnectionOptions(natsServer, true, DEFAULT_ERROR_LISTENER, DEFAULT_CONNECTION_LISTENER));
            connectionsForPullSubscribe.put(subject, nc);
        }
        Connection nc = connectionsForPullSubscribe.get(subject);
        JetStream js = nc.jetStream();

        ConsumerConfiguration cc = ConsumerConfiguration.builder()
                .ackWait(Duration.ofMillis(2500))
                .build();
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .stream(streamName)
//                .durable(subject + "_default_durable") // required
                .durable(durableName.replaceAll("-", "_").replaceAll("\\.", "_")) // required
                .configuration(cc)
                .build();

        JetStreamSubscription sub = js.subscribe(subject, pullOptions);
        nc.flush(Duration.ofSeconds(1));

        int count = 0;
        while (count < BATCH_SIZE) {
            sub.pull(BATCH_SIZE);
            Message m = sub.nextMessage(Duration.ofSeconds(1)); // first message
            while (m != null) {
                if (m.isJetStream()) {
                    // process message
                    count++;
                    consumer.accept(m);
                    m.ack();
                }
                m = sub.nextMessage(Duration.ofMillis(100)); // other messages should already be on the client
            }
        }

    }

    @Override
    public void pushSubscribe(String subject, java.util.function.Consumer<Message> consumer) throws Exception {

        // 一個 Subscribe 一個 connection
        Connection nc = Nats.connect(createConnectionOptions(natsServer, true, DEFAULT_ERROR_LISTENER, DEFAULT_CONNECTION_LISTENER));
        connectionsForPushSubscribe.add(nc);

        JetStream js = nc.jetStream();

        Dispatcher dispatcher = nc.createDispatcher();

        MessageHandler handler = msg -> {

                log.debug("\nMessage Received:");

                if (msg.hasHeaders()) {
                    log.debug("  Headers:");
                    for (String key : msg.getHeaders().keySet()) {
                        for (String value : msg.getHeaders().get(key)) {
                            log.debug("    %s: %s\n", key, value);
                        }
                    }
                }

                consumer.accept(msg);

                // This example here's no auto-ack.
                // The default Consumer Configuration AckPolicy is Explicit
                // so we need to ack the message or it'll be redelivered.
                msg.ack();

            };

        // Build our subscription options.
        // * A push subscription means the server will "push" us messages.
        // * Durable means the server will remember where we are if we use that name.
        // * Durable can by null or empty, the builder treats them the same.
        // * The stream name is not technically required. If it is not provided, the
        //   code building the subscription will look it up by making a request to the server.
        //   If you know the stream name, you might as well supply it and save a trip to the server.
        PushSubscribeOptions so = PushSubscribeOptions.builder()
                .stream(streamName)
                .durable(subject.replaceAll("-", "_").replaceAll("\\.", "_") + "_default_durable")
                .build();

        // Subscribe using the handler
        js.subscribe(subject, dispatcher, handler, false, so);  // auto ack -> false
    }
}
