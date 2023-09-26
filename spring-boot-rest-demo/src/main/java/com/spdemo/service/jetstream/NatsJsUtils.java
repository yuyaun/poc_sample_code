package com.spdemo.service.jetstream;

import io.nats.client.*;
import io.nats.client.api.*;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
class NatsJsUtils {

    // ----------------------------------------------------------------------------------------------------
    // STREAM INFO / CREATE / UPDATE
    // ----------------------------------------------------------------------------------------------------
    public static StreamInfo getStreamInfoOrNullWhenNotExist(JetStreamManagement jsm, String streamName) throws IOException, JetStreamApiException {
        try {
            return jsm.getStreamInfo(streamName);
        }
        catch (JetStreamApiException jsae) {
            if (jsae.getErrorCode() == 404) {
                return null;
            }
            throw jsae;
        }
    }

    public static boolean streamExists(JetStreamManagement jsm, String streamName) throws IOException, JetStreamApiException {
        return getStreamInfoOrNullWhenNotExist(jsm, streamName) != null;
    }

    public static boolean streamExists(Connection nc, String streamName) throws IOException, JetStreamApiException {
        return getStreamInfoOrNullWhenNotExist(nc.jetStreamManagement(), streamName) != null;
    }

    public static void exitIfStreamExists(JetStreamManagement jsm, String streamName) throws IOException, JetStreamApiException {
        if (streamExists(jsm, streamName)) {
            log.debug("\nThe example cannot run since the stream '" + streamName + "' already exists.\n" +
                    "It depends on the stream being in a new state. You can either:\n" +
                    "  1) Change the stream name in the example.\n  2) Delete the stream.\n  3) Restart the server if the stream is a memory stream.");
            System.exit(-1);
        }
    }

    public static void exitIfStreamNotExists(Connection nc, String streamName) throws IOException, JetStreamApiException {
        if (!streamExists(nc, streamName)) {
            log.debug("\nThe example cannot run since the stream '" + streamName + "' does not exist.\n" +
                    "It depends on the stream existing and having data.");
            System.exit(-1);
        }
    }

    public static StreamInfo createStream(JetStreamManagement jsm, String streamName, StorageType storageType, List<String> subjects) throws IOException, JetStreamApiException {
        // Create a stream, here will use a file storage type, and one subject,
        // the passed subject.

//Old
//        StreamConfiguration sc = StreamConfiguration.builder()
//                .name(streamName)
//                .storageType(storageType)
//                .subjects(subjects)
//                .build();

        // New
        StreamConfiguration sc = StreamConfiguration.builder()
            .name(streamName)
            .subjects(subjects)
            .retentionPolicy(RetentionPolicy.Limits)
            // .maxConsumers(...)
            .maxBytes(-1)
            .maxAge(0)
            .maxMsgSize(-1)
            .maxMessages(-1)
            .storageType(StorageType.File)
            // .replicas(...)
            // .noAck(...)
            // .template(...)
            // .discardPolicy(...)
            .build();

        // Add or use an existing stream.
        StreamInfo si = jsm.addStream(sc);
        log.info("Created stream '%s' with subject(s) %s\n",
                streamName, si.getConfiguration().getSubjects());

        return si;
    }

    public static StreamInfo createStream(JetStreamManagement jsm, String streamName, List<String> subjects)
            throws IOException, JetStreamApiException {
        return createStream(jsm, streamName, StorageType.Memory, subjects);
    }

    public static StreamInfo createStream(Connection nc, String stream, List<String> subjects) throws IOException, JetStreamApiException {
        return createStream(nc.jetStreamManagement(), stream, StorageType.Memory, subjects);
    }

    public static StreamInfo createStreamExitWhenExists(Connection nc, String streamName, List<String> subjects) throws IOException, JetStreamApiException {
        return createStreamExitWhenExists(nc.jetStreamManagement(), streamName, subjects);
    }

    public static StreamInfo createStreamExitWhenExists(JetStreamManagement jsm, String streamName, List<String> subjects) throws IOException, JetStreamApiException {
        exitIfStreamExists(jsm, streamName);
        return createStream(jsm, streamName, StorageType.Memory, subjects);
    }

    public static StreamInfo createStreamOrUpdateSubjects(JetStreamManagement jsm, String streamName, StorageType storageType, List<String> subjects)
            throws IOException, JetStreamApiException {

        StreamInfo si = getStreamInfoOrNullWhenNotExist(jsm, streamName);
        if (si == null) {
            return createStream(jsm, streamName, storageType, subjects);
        }

        // check to see if the configuration has all the subject we want
        StreamConfiguration sc = si.getConfiguration();
        boolean needToUpdate = false;
        for (String sub : subjects) {
            if (!sc.getSubjects().contains(sub)) {
                needToUpdate = true;
                sc.getSubjects().add(sub);
            }
        }

        if (needToUpdate) {
            sc = StreamConfiguration.builder(sc).subjects(sc.getSubjects()).build();
            si = jsm.updateStream(sc);
            log.debug("Existing stream '%s' was updated, has subject(s) %s\n",
                    streamName, si.getConfiguration().getSubjects());
        }
        else
        {
            log.debug("Existing stream '%s' already contained subject(s) %s\n",
                    streamName, si.getConfiguration().getSubjects());
        }

        return si;
    }

    public static StreamInfo createStreamOrUpdateSubjects(JetStreamManagement jsm, String streamName, List<String> subjects)
            throws IOException, JetStreamApiException {
        return createStreamOrUpdateSubjects(jsm, streamName, StorageType.Memory, subjects);
    }

    public static StreamInfo createStreamOrUpdateSubjects(Connection nc, String stream, List<String> subjects) throws IOException, JetStreamApiException {
        return createStreamOrUpdateSubjects(nc.jetStreamManagement(), stream, StorageType.Memory, subjects);
    }

    // ----------------------------------------------------------------------------------------------------
    // READ MESSAGES
    // ----------------------------------------------------------------------------------------------------
    public static List<Message> readMessagesAck(JetStreamSubscription sub) throws InterruptedException {
        return readMessagesAck(sub, true, Duration.ofSeconds(1));
    }

    public static List<Message> readMessagesAck(JetStreamSubscription sub, boolean verbose) throws InterruptedException {
        return readMessagesAck(sub, verbose, Duration.ofSeconds(1));
    }

    public static List<Message> readMessagesAck(JetStreamSubscription sub, Duration nextMessageTimeout) throws InterruptedException {
        return readMessagesAck(sub, true, nextMessageTimeout);
    }

    public static List<Message> readMessagesAck(JetStreamSubscription sub, boolean verbose, Duration nextMessageTimeout) throws InterruptedException {
        if (verbose) {
            log.debug("Read/Ack ->");
        }
        List<Message> messages = new ArrayList<>();
        Message msg = sub.nextMessage(nextMessageTimeout);
        while (msg != null) {
            messages.add(msg);
            msg.ack();
            if (verbose) {
                log.debug(" " + new String(msg.getData()));
            }
            msg = sub.nextMessage(nextMessageTimeout);
        }

        if (verbose) {
            log.debug(messages.size() == 0 ? " No messages available <-" : " <- ");
        }

        return messages;
    }

    // ----------------------------------------------------------------------------------------------------
    // PRINT
    // ----------------------------------------------------------------------------------------------------
    public static void printStreamInfo(StreamInfo si) {
        printObject(si, "StreamConfiguration", "StreamState", "ClusterInfo", "Mirror", "subjects", "sources");
    }

    public static void printStreamInfoList(List<StreamInfo> list) {
        printObject(list, "!StreamInfo", "StreamConfiguration", "StreamState");
    }

    public static void printConsumerInfo(ConsumerInfo ci) {
        printObject(ci, "ConsumerConfiguration", "Delivered", "AckFloor");
    }

    public static void printConsumerInfoList(List<ConsumerInfo> list) {
        printObject(list, "!ConsumerInfo", "ConsumerConfiguration", "Delivered", "AckFloor");
    }

    public static void printObject(Object o, String... subObjectNames) {
        String s = o.toString();
        for (String sub : subObjectNames) {
            boolean noIndent = sub.startsWith("!");
            String sb = noIndent ? sub.substring(1) : sub;
            String rx1 = ", " + sb;
            String repl1 = (noIndent ? ",\n": ",\n    ") + sb;
            s = s.replace(rx1, repl1);
        }

        log.debug(s);
    }

    // ----------------------------------------------------------------------------------------------------
    // REPORT
    // ----------------------------------------------------------------------------------------------------
    public static void report(List<Message> list) {
        log.debug("Fetch ->");
        for (Message m : list) {
            log.debug(" " + new String(m.getData()));
        }
        log.debug(" <- ");
    }

    public static List<Message> report(Iterator<Message> list) {
        List<Message> messages = new ArrayList<>();
        log.debug("Fetch ->");
        while (list.hasNext()) {
            Message m = list.next();
            messages.add(m);
            log.debug(" " + new String(m.getData()));
        }
        log.debug(" <- ");
        return messages;
    }

    // ----------------------------------------------------------------------------------------------------
    // MISC
    // ----------------------------------------------------------------------------------------------------
    public static int countJs(List<Message> messages) {
        int count = 0;
        for (Message m : messages) {
            count++;
        }
        return count;
    }

    public static int count408s(List<Message> messages) {
        int count = 0;
        for (Message m : messages) {
            if (m.isStatusMessage() && m.getStatus().getCode() == 408) {
                count++;
            }
        }
        return count;
    }
}
