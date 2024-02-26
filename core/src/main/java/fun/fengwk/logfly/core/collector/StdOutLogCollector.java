package fun.fengwk.logfly.core.collector;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author fengwk
 */
@Slf4j
public class StdOutLogCollector extends AbstractLogCollector {

    protected StdOutLogCollector(File logFile) {
        super(logFile);
    }

    @Override
    public synchronized void readLine(String line) {
        System.out.println(line);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
