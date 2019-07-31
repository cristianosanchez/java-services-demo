package acme.pubsub;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class SubscriberExample {

    // use the default project id
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

    private static final BlockingQueue<PubsubMessage> localQueue = new LinkedBlockingDeque<>();

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            String data = message.getData().toStringUtf8();
            log.info("Received message id '{}' and data '{}'", message.getMessageId(), data);
            int n = extractMessageNumber(data);
            if (n % 3 == 0) {
                consumer.nack();
                log.warn("Not acking message id '{}' and data '{}'", message.getMessageId(), data);
            } else {
                localQueue.offer(message);
                consumer.ack();
                log.info("Acking message id '{}' and data '{}'", message.getMessageId(), data);
            }
        }
    }

    /** Receive messages over a subscription. */
    public static void main(String... args) throws Exception {
        // set subscriber id, eg. my-sub
        String subscriptionId = "bookings-log";
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;
        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber =
                    Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            // Continue to listen to messages
            while (true) {
                PubsubMessage message = localQueue.take();
                String data = message.getData().toStringUtf8();
                log.info("Processing message id '{}' and data '{}'", message.getMessageId(), data);
            }
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

    private static int extractMessageNumber(String data) {
        int idx = data.lastIndexOf("message-");
        if (idx < 0) {
            return 0;
        }
        try {
            return Integer.parseInt(data.substring(idx));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
