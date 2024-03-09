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

public class PulsarClientOnlyDeathQueueTest {

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
            String message = "origin-only-death-message-by-java-" + System.currentTimeMillis();
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

                    // 不对消息进行确认，使其触发 ack 超时，并最终进入死信队列
//                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                }
            }
        };

        DeadLetterPolicy deadLetterPolicy = DeadLetterPolicy.builder()
                //【Exception in thread "main" java.lang.IllegalArgumentException: MaxRedeliverCount must be > 0.】
                //【	at org.apache.pulsar.client.impl.ConsumerBuilderImpl.deadLetterPolicy(ConsumerBuilderImpl.java:442)】
                .maxRedeliverCount(3)
                .deadLetterTopic("my-only-death-topic-by-java")
                .initialSubscriptionName("my-only-death-subscription-by-java")
                .build();

        Consumer<byte[]> consumer = pulsarClient.newConsumer()
                .topic(topicName)
                .messageListener(myMessageListener)
                .deadLetterPolicy(deadLetterPolicy)
                // 顶一个主题的的同名订阅类型，不可以进行修改，所以我们创建一个新的订阅名称
                //【Caused by: org.apache.pulsar.client.api.PulsarClientException$ConsumerBusyException: {"errorMsg":"Subscription is of different type","reqId":4382186774778827181, "remote":"/192.168.56.10:6650", "local":"/192.168.56.1:1964"}】
                //【at org.apache.pulsar.client.impl.ConsumerBuilderImpl.subscribe(ConsumerBuilderImpl.java:103)】
//                .subscriptionName("my-origin-subscription-by-java")
                // 在不使用重试队列时，如果订阅类型不为共享的，那么未确认的消息只会不断的重新投递，并不会进行死信队列中
                // 这里的重投是重新投递的原始信息，而不是像重试队列那样，产生新的信息进行重新投递新的信息过来，
//                .subscriptionName("my-origin-exclusive-subscription-by-java")
//                .subscriptionType(SubscriptionType.Exclusive)
                .subscriptionName("my-origin-shared-subscription-by-java")
                .subscriptionType(SubscriptionType.Shared)
                // 设置消费者的应答超时时间， 用于触发超时后将消息发送到死信队列
                .ackTimeout(3, TimeUnit.SECONDS)
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
