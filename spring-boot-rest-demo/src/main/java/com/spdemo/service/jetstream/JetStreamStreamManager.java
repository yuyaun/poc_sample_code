package com.spdemo.service.jetstream;

import io.nats.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class JetStreamStreamManager {

    private String natsServer;

    // jetstream Config
    private String streamName;
    private final List<String> subjects;

    public JetStreamStreamManager(@Value("${nats.server.ip}") String natsServer, @Value("${nats.jetstream.streamName}") String streamName,
                                  @Value("${nats.jetstream.subjects}") String subjects) {
        this.natsServer = natsServer;
        this.streamName = streamName;
        this.subjects = Arrays.asList(StringUtils.tokenizeToStringArray(subjects, ","));
    }

    public static final ConnectionListener DEFAULT_CONNECTION_LISTENER = (conn, type) -> log.info("Status change "+ type);

    public static final ErrorListener DEFAULT_ERROR_LISTENER = new ErrorListener() {
        public void exceptionOccurred(Connection conn, Exception exp) {
            System.out.println("Exception " + exp.getMessage());
        }

        public void errorOccurred(Connection conn, String type) {
            log.info("Error " + type);
        }

        public void slowConsumerDetected(Connection conn, Consumer consumer) {
            log.info("Slow consumer");
        }
    };

    public static Options createConnectionOptions(String server, boolean allowReconnect, ErrorListener el, ConnectionListener cl) throws Exception {

        if (el == null) { el = new ErrorListener() {}; }

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


    @PostConstruct
    public void init() {

        // 建立 JetStream
        try (Connection nc = Nats.connect(createConnectionOptions(natsServer, true, DEFAULT_ERROR_LISTENER, DEFAULT_CONNECTION_LISTENER))) {

            // Create a JetStreamManagement context.
//            JetStreamManagement jsm = nc.jetStreamManagement();

            NatsJsUtils.createStreamOrUpdateSubjects(nc, streamName, subjects);


        } catch (Throwable ex) {
//            log.error("manage jetstream error !", ex);
            throw new IllegalStateException("manage nats jetstream error !", ex);
        }
    }



    public static String printStreamInfo(Object o) {
        String s = o.toString();
        List<String> subObjectNames = Arrays.asList("StreamConfiguration", "StreamState", "ClusterInfo", "Mirror", "subjects", "sources");
        for (String sub : subObjectNames) {
            boolean noIndent = sub.startsWith("!");
            String sb = noIndent ? sub.substring(1) : sub;
            String rx1 = ", " + sb;
            String repl1 = (noIndent ? ",\n": ",\n    ") + sb;
            s = s.replace(rx1, repl1);
        }

        return s;
    }

}
