package fun.fengwk.logfly.core;

import fun.fengwk.logfly.core.collector.AbstractLogCollector;
import fun.fengwk.logfly.core.collector.LogCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author fengwk
 */
@Slf4j
public class InvokeCounterCollector extends AbstractLogCollector {

    private int count;

    protected InvokeCounterCollector(File logFile) {
        super(logFile);
    }

    @Override
    public void readLine(String line) {
        count++;
    }

    @Override
    public void close() {

    }

    public int getCount() {
        return count;
    }

}
