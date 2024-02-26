package fun.fengwk.logfly.core.collector;

import java.io.File;

/**
 * @author fengwk
 */
public abstract class AbstractLogCollector implements LogCollector {

    private final File logFile;

    protected AbstractLogCollector(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public File getLogFile() {
        return this.logFile;
    }

}
