package fun.fengwk.logfly.core;

import fun.fengwk.convention4j.common.fs.FileWatcherListener;
import fun.fengwk.logfly.core.collector.LogCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * 日志收集监听器
 *
 * @author fengwk
 */
@Slf4j
public class LogCollectorListener implements FileWatcherListener, AutoCloseable {

    private final LogCollector logCollector;
    private final Charset charset;
    private NoBlockingLogReader reader;
    private boolean closed;

    public LogCollectorListener(LogCollector logCollector, Charset charset) {
        this.logCollector = Objects.requireNonNull(logCollector, "Log collector must not be null");
        this.charset = Objects.requireNonNull(charset, "Charset must not be null");
    }

    @Override
    public synchronized void onEntryCreate() {
        File logFile = this.logCollector.getLogFile();
        if (this.closed) {
            log.debug("Log file has been closed: {}", logFile);
            return;
        }
        try {
            this.reader = new NoBlockingLogReader(this.logCollector, logFile, charset);
            log.debug("NoBlockingLogReader created: {}", logFile);
        } catch (FileNotFoundException ex) {
            log.error("Log file not found: {}", logFile, ex);
        }
        readLine();
    }

    @Override
    public synchronized void onEntryDelete() {
        File logFile = this.logCollector.getLogFile();
        if (this.closed) {
            log.debug("Log file has been closed: {}", logFile);
            return;
        }
        if (this.reader != null) {
            try {
                this.reader.close();
                log.debug("NoBlockingLogReader closed: {}", logFile);
            } catch (IOException e) {
                log.error("Log reader close failed: {}", logFile, e);
            }
        }
    }

    @Override
    public synchronized void onEntryModify() {
        File logFile = this.logCollector.getLogFile();
        if (this.closed) {
            log.debug("Log file has been closed: {}", logFile);
            return;
        }
        log.debug("Log file modified: {}", logFile);
        readLine();
    }

    private void readLine() {
        File logFile = this.logCollector.getLogFile();
        try {
            this.reader.readLine();
            log.debug("Invoked read line: {}", logFile);
        } catch (IOException ex) {
            log.error("Log reader read line failed: {}", logFile, ex);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
            this.closed = true;
            log.debug("NoBlockingLogReader closed: {}", this.logCollector.getLogFile());
        }
    }

    public File getLogFile() {
        return this.logCollector.getLogFile();
    }

}
