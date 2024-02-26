package fun.fengwk.logfly.core;

import fun.fengwk.logfly.core.collector.AbstractLogCollector;
import fun.fengwk.logfly.core.collector.LogCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author fengwk
 */
@Slf4j
public class StringCollector extends AbstractLogCollector {

    private final StringBuilder sb = new StringBuilder();

    protected StringCollector(File logFile) {
        super(logFile);
    }

    @Override
    public void readLine(String line) {
        log.debug("Read line: '{}'", line);
        sb.append(line);
    }

    @Override
    public void close() {

    }

    public String getString() {
        return sb.toString();
    }

}
