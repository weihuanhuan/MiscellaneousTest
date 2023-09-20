package mq.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PulsarClientTest {

    public static String serviceUrl = "pulsar://192.168.56.10:6650";
    public static String topicName = "my-topic-by-java";

    public static void main(String[] args) {
        PulsarClient pulsarClient = null;
        Producer<byte[]> producer = null;
        Consumer<byte[]> consumer = null;

        try {
            pulsarClient = getPulsarClient();
            System.out.println("pulsarClient=" + pulsarClient);

            producer = newProducer(pulsarClient);
            System.out.println("producer=" + producer);

            // You can then send messages to the broker and topic you specified:
            String message = "message-by-java-" + System.currentTimeMillis();
            MessageId send = producer.send(message.getBytes());
            System.out.println("send=" + send);

            consumer = newConsumer(pulsarClient);
            System.out.println("consumer=" + consumer);

            receiveMessages(consumer);
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        } finally {
            silentClose(consumer);
            silentClose(producer);
            silentClose(pulsarClient);
        }
    }

    public static PulsarClient getPulsarClient() throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();
        return client;
    }

    public static Producer<byte[]> newProducer(PulsarClient pulsarClient) throws PulsarClientException {
        Producer<byte[]> producer = pulsarClient.newProducer()
                .topic(topicName)
                .create();
        return producer;
    }

    public static Consumer<byte[]> newConsumer(PulsarClient pulsarClient) throws PulsarClientException {
//        MessageListener<byte[]> myMessageListener = new MessageListener<byte[]>() {
//            @Override
//            public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
//                try {
//                    System.out.println("Message received: " + new String(msg.getData()));
//                    consumer.acknowledge(msg);
//                } catch (Exception e) {
//                    consumer.negativeAcknowledge(msg);
//                }
//            }
//        };

        Consumer<byte[]> consumer = pulsarClient.newConsumer()
                .topic(topicName)
                .subscriptionName("my-subscription-by-java")
//                .messageListener(myMessageListener)
                .subscribe();
        return consumer;
    }

    public static void receiveMessages(Consumer<byte[]> consumer) throws PulsarClientException {
        boolean hasReachedEndOfTopic = consumer.hasReachedEndOfTopic();

        while (!hasReachedEndOfTopic) {
            // Wait for a message
            Message<byte[]> receive = consumer.receive(5, TimeUnit.SECONDS);
            if (receive == null) {
                //TODO 当收不到消息时，hasReachedEndOfTopic 依旧不会为 true ，所以不能用它来结束对消息的等待。另外其什么情况下为 true 呢？
                hasReachedEndOfTopic = consumer.hasReachedEndOfTopic();
                System.out.println("receive=" + receive);
                System.out.println("hasReachedEndOfTopic=" + hasReachedEndOfTopic);
                break;
            }

            try {
                // Do something with the message
                System.out.println("Message received: " + new String(receive.getData()));

                // Acknowledge the message
                consumer.acknowledge(receive);
            } catch (Exception e) {
                System.out.println("Message exception: " + e.getMessage());

                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(receive);
            }
        }
    }

    public static void silentClose(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
