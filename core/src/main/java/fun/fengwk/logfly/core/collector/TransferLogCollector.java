package fun.fengwk.logfly.core.collector;

import fun.fengwk.logfly.core.transfer.LogLine;
import fun.fengwk.logfly.core.transfer.Transfer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

/**
 * 异步日志收集器
 *
 * @author fengwk
 */
@Slf4j
public class TransferLogCollector extends AbstractLogCollector {

    private final Transfer transfer;

    protected TransferLogCollector(File logFile, Transfer transfer) {
        super(logFile);
        this.transfer = Objects.requireNonNull(transfer, "Transfer must not be null");
    }

    @Override
    public void readLine(String line) {
        try {
            this.transfer.getBlockingQueue().put(new LogLine(getLogFile(), line));
        } catch (InterruptedException e) {
            log.warn("Read line interrupted, line: {}", line);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

}
