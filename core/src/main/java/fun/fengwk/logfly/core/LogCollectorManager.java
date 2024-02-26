package fun.fengwk.logfly.core;

import fun.fengwk.convention4j.common.fs.FileWatcherManager;
import fun.fengwk.logfly.core.collector.LogCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志收集管理器
 *
 * @author fengwk
 */
@Slf4j
public class LogCollectorManager implements AutoCloseable {

    private final List<LogCollectorListener> listeners = new ArrayList<>();

    public synchronized void collect(LogCollector logCollector, Charset charset) throws IOException {
        LogCollectorListener listener = new LogCollectorListener(logCollector, charset);
        this.listeners.add(listener);
        File logFile = logCollector.getLogFile();
        FileWatcherManager.getInstance().watch(logFile.toPath(), listener);
        log.debug("LogCollectorListener created: {}", logFile);
    }


    @Override
    public synchronized void close() throws IOException {
        for (LogCollectorListener listener : this.listeners) {
            File logFile = listener.getLogFile();
            FileWatcherManager.getInstance().unwatch(logFile.toPath());
            listener.close();
            log.debug("LogCollectorListener closed: {}", logFile);
        }
    }

}
