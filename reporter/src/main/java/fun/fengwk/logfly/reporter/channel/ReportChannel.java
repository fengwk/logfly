package fun.fengwk.logfly.reporter.channel;

/**
 * @author fengwk
 */
public interface ReportChannel extends AutoCloseable {

    /**
     * 上报日志行
     *
     * @param target 上报的目标内容
     */
    void report(String target);

}
