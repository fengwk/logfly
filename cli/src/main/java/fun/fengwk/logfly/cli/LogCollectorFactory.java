package fun.fengwk.logfly.cli;

import java.util.Properties;

import fun.fengwk.logfly.core.LogCollector;

/**
 * @author fengwk
 */
@FunctionalInterface
public interface LogCollectorFactory {

    LogCollector create(Properties properties);

}
