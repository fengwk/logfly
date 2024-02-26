package fun.fengwk.logfly.reporter;

import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.logfly.core.transfer.LogLine;
import fun.fengwk.logfly.core.transfer.Transfer;
import fun.fengwk.logfly.reporter.channel.ReportChannel;
import fun.fengwk.logfly.reporter.parser.LogLineParser;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author fengwk
 */
public class Reporter implements Runnable, AutoCloseable {

    private final ReportChannel channel;
    private final Transfer transfer;
    private final LogLineParser parser;
    private volatile List<Thread> reporterThreads;
    private volatile CountDownLatch cdl;

    public Reporter(ReportChannel channel, Transfer transfer, LogLineParser parser) {
        this.channel = Objects.requireNonNull(
            channel, "Report channel must not be null");
        this.transfer = Objects.requireNonNull(
            transfer, "Transfer must not be null");
        this.parser = Objects.requireNonNull(
            parser, "Log line parser must not be null");
    }

    /**
     * 启动上报
     */
    public synchronized void start() {
        if (reporterThreads == null) {
            int n = Runtime.getRuntime().availableProcessors();
            this.reporterThreads = new ArrayList<>(n);
            this.cdl = new CountDownLatch(n);
            while (n > 0) {
                Thread reportThread = new Thread(this);
                reporterThreads.add(reportThread);
                reportThread.start();
                n--;
            }
        }
    }

    /**
     * 停止上报
     */
    public synchronized void stop() {
        if (reporterThreads != null) {
            for (Thread reporterThread : reporterThreads) {
                reporterThread.interrupt();
            }
            boolean interrupted = Thread.currentThread().isInterrupted();
            for (;;) {
                try {
                    cdl.wait();
                    break;
                } catch (InterruptedException ignore) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            this.reporterThreads = null;
            this.cdl = null;
        }
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        BlockingQueue<LogLine> queue = transfer.getBlockingQueue();
        while (!currentThread.isInterrupted()) {
            // 从队列中获取日志行
            LogLine logLine;
            try {
                logLine = queue.take();
            } catch (InterruptedException e) {
                currentThread.interrupt();
                break;
            }

            // 过滤空日志行
            if (StringUtils.isEmpty(logLine.getLine())) {
                continue;
            }

            // 解析日志行
            Map<String, Object> logLineObj = new LinkedHashMap<>();
            logLineObj.put("fileName", logLine.getLogFile().getName());
            logLineObj.put("line", logLine.getLine());




            Map<String, Object> target = parser.parse(logLine.getLine());
            TargetLogLine targetLogLine = new TargetLogLine(logLine.getLogFile(), logLine.getLine(), target);

            // 上报日志行
            channel.report(targetLogLine);
        }

        // 通知完成关闭
        cdl.countDown();
    }

    @Override
    public void close() {
        stop();
    }

}
