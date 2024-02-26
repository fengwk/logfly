package fun.fengwk.logfly.core.transfer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class LinkedBlockingQueueTransfer implements Transfer {

    private final LinkedBlockingQueue<LogLine> logLineQueue;

    public LinkedBlockingQueueTransfer(int bufferSize) {
        this.logLineQueue = new LinkedBlockingQueue<>(bufferSize);
    }

    @Override
    public BlockingQueue<LogLine> getBlockingQueue() {
        return this.logLineQueue;
    }

}
