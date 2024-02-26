package fun.fengwk.logfly.core.transfer;

import java.util.concurrent.BlockingQueue;

/**
 * @author fengwk
 */
public interface Transfer {

    /**
     * 获取包含收集到日志记录的阻塞队列
     *
     * @return 包含收集到日志记录的阻塞队列
     */
    BlockingQueue<LogLine> getBlockingQueue();

}
