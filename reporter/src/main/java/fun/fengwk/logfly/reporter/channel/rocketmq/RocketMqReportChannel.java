package fun.fengwk.logfly.reporter.channel.rocketmq;

import fun.fengwk.logfly.reporter.channel.ReportChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMqReportChannel implements ReportChannel {

    private final MQProducer producer;
    private final String topic;

    public RocketMqReportChannel(String nameServer, String producerGroup, String topic) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.start();
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void report(String target) {
        Message message = new Message(topic, target.getBytes(StandardCharsets.UTF_8));
        try {
            SendResult sendResult = producer.send(message);
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("Report target failed, logLine: {}", target);
            }
        } catch (InterruptedException ex) {
            log.error("Report target interrupted, logLine: {}", target, ex);
            Thread.currentThread().interrupt();
        } catch (MQClientException | RemotingException | MQBrokerException ex) {
            log.error("Report target error, logLine: {}", target, ex);
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.shutdown();
        }
    }

}