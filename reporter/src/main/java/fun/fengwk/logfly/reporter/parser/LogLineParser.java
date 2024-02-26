package fun.fengwk.logfly.reporter.parser;

import java.util.Map;

/**
 * @author fengwk
 */
public interface LogLineParser {

    /**
     * 解析日志行
     *
     * @param line 日志行
     * @return 解析后产出的数据行
     */
    Map<String, Object> parse(String line);

}
