package mq.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PulsarClientRetryQueueAndDeathQueueTest {

    public static String serviceUrl = "pulsar://192.168.56.10:6650";
    public static String topicName = "my-origin-topic-by-java";

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
            String message = "origin-message-by-java-" + System.currentTimeMillis();
            MessageId send = producer.send(message.getBytes());
            System.out.println("send=" + send);

            consumer = newConsumer(pulsarClient);
            System.out.println("consumer=" + consumer);

            System.in.read();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
        MessageListener<byte[]> myMessageListener = new MessageListener<byte[]>() {
            @Override
            public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
                try {
                    MessageId messageId = msg.getMessageId();
                    String messageDataString = new String(msg.getData());
                    Map<String, String> properties = msg.getProperties();

                    System.out.println("messageId:" + messageId);
                    System.out.println("messageDataString:" + messageDataString);
                    System.out.println("properties:" + properties);

                    consumer.reconsumeLater(msg, 3, TimeUnit.SECONDS);

                    // 主动不确认消息，对原始的消息来触发其发送到重试队列，对重试的消息在达到重试次数后触发其发送到死信队列
//                     consumer.acknowledge(msg);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                }
            }
        };

        DeadLetterPolicy deadLetterPolicy = DeadLetterPolicy.builder()
                .retryLetterTopic("my-retry-topic-by-java")
                .maxRedeliverCount(3)
                .deadLetterTopic("my-death-topic-by-java")
                .initialSubscriptionName("my-death-subscription-by-java")
                .build();

        Consumer<byte[]> consumer = pulsarClient.newConsumer()
                .topic(topicName)
                .subscriptionName("my-origin-subscription-by-java")
                .messageListener(myMessageListener)
                .enableRetry(true)
                .deadLetterPolicy(deadLetterPolicy)
                .subscriptionType(SubscriptionType.Failover)
                .subscribe();
        return consumer;
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
