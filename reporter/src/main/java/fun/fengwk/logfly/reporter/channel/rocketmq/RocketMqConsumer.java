package fun.fengwk.logfly.reporter.channel.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author fengwk
 */
public class RocketMqConsumer implements AutoCloseable {

    private final MQPushConsumer consumer;

    public RocketMqConsumer(String nameServer, String consumerGroup, String topic) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.subscribe(topic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> consume(msgs, context));
        this.consumer = consumer;
        consumer.start();
    }

    private ConsumeConcurrentlyStatus consume(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        msgs.parallelStream().forEach(msg -> {

        });
        for (MessageExt msg : msgs) {
            String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void doReport() {

    }

    @Override
    public void close() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }

}
