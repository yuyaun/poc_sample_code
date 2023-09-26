package com.spdemo.service.jetstream;

import io.nats.client.Message;
import io.nats.client.impl.Headers;

import java.util.function.Consumer;

public interface JetStreamService {


    /**
     * publish nats message
      */
    void publish(String subject, Headers headers, String payload) throws Exception;
    void publish(String subject, Headers headers, Object payload) throws Exception;

    /**
     * consumer message
     * 需另行啟動 Scheduled 來收 message
     */
    void pullSubscribe(String subject, String durableName, Integer BATCH_SIZE, Consumer<Message> consumer) throws Exception;


    /**
     * consumer message
     */
    void pushSubscribe(String subject, Consumer<Message> consumer) throws Exception;

}
