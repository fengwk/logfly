package fun.fengwk.logfly.core.collector;

import fun.fengwk.logfly.core.LineReader;

import java.io.File;

/**
 * 日志收集器
 *
 * @author fengwk
 */
public interface LogCollector extends LineReader, AutoCloseable {

    /**
     * 获取正在收集的日志文件
     *
     * @return 正在收集的日志文件
     */
    File getLogFile();

}
