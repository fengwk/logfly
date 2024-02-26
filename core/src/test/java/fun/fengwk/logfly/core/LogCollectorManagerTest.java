package fun.fengwk.logfly.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fengwk
 */
@Slf4j
public class LogCollectorManagerTest {

    @Test
    public void test_WithoutNewline() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        StringCollector stringCollector = new StringCollector(tmpFile);
        logCollectorManager.collect(stringCollector, StandardCharsets.UTF_8);

        assertTrue(tmpFile.createNewFile());

        String s1 = UUID.randomUUID().toString();
        String s2 = UUID.randomUUID().toString();
        String s3 = UUID.randomUUID().toString();
        String s4 = UUID.randomUUID().toString();
        String s5 = UUID.randomUUID().toString();

        appendText(tmpFile, s1);
        appendText(tmpFile, s2);
        appendText(tmpFile, s3);
        appendText(tmpFile, s4);
        appendText(tmpFile, s5);

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile.delete());

        assertEquals(s1 + s2 + s3 + s4 + s5, stringCollector.getString());
    }

    @Test
    public void test_SpecialSeq() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        StringCollector stringCollector = new StringCollector(tmpFile);
        logCollectorManager.collect(stringCollector, StandardCharsets.UTF_8);

        assertTrue(tmpFile.createNewFile());

        String s1 = "\r\n";
        String s2 = "\n";
        String s3 = "\r";
        String s4 = UUID.randomUUID().toString();
        String s5 = "\r";

        appendText(tmpFile, s1);
        appendText(tmpFile, s2);
        appendText(tmpFile, s3);
        appendText(tmpFile, s4);
        appendText(tmpFile, s5);

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile.delete());

        assertEquals(s4, stringCollector.getString());
    }

    @Test
    public void test_SpecialSeq2() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        InvokeCounterCollector invokeCountCollector = new InvokeCounterCollector(tmpFile);
        logCollectorManager.collect(invokeCountCollector, StandardCharsets.UTF_8);

        assertTrue(tmpFile.createNewFile());

        String s1 = "\r\n";
        String s2 = "\n";
        String s3 = "\r";
        String s4 = UUID.randomUUID().toString();
        String s5 = "\r";

        appendText(tmpFile, s1);
        appendText(tmpFile, s2);
        appendText(tmpFile, s3);
        appendText(tmpFile, s4);
        appendText(tmpFile, s5);

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile.delete());

        assertEquals(5, invokeCountCollector.getCount());
    }

    @Test
    public void test_SpecialSeq3() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        InvokeCounterCollector invokeCountCollector = new InvokeCounterCollector(tmpFile);
        logCollectorManager.collect(invokeCountCollector, StandardCharsets.UTF_8);

        assertTrue(tmpFile.createNewFile());

        String s1 = "\r\n";
        String s2 = "\n";
        String s3 = "\r";
        String s4 = UUID.randomUUID().toString();
        String s5 = "asd";

        appendText(tmpFile, s1);
        appendText(tmpFile, s2);
        appendText(tmpFile, s3);
        appendText(tmpFile, s4);
        appendText(tmpFile, s5);

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile.delete());

        assertEquals(4, invokeCountCollector.getCount());
    }

    @Test
    public void test_CollectThenWrite() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        LineNumbersCollector lineNumbersCollector = new LineNumbersCollector(tmpFile);
        logCollectorManager.collect(lineNumbersCollector, StandardCharsets.UTF_8);

        // 该线程将不断向tmpFile中写入数据
        CountDownLatch cdl = new CountDownLatch(1);
        List<BiConsumer<String, File>> appendFunctions = Arrays.asList(
            this::appendWindowsLine,
            this::appendLinuxLine,
            this::appendMacLine
        );
        int limit = 10000;
        new Thread(() -> {
            try {
                if (tmpFile.createNewFile()) {
                    for (int i = 0; i < limit; i++) {
                        appendFunctions.get(i % appendFunctions.size())
                            .accept(String.valueOf(i + 1), tmpFile);
                    }
                } else {
                    log.error("Create temp file failed: {}", tmpFile);
                }
            } catch (IOException ex) {
                log.error("Create temp file error", ex);
            }
            cdl.countDown();
        }).start();

        cdl.await();

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile.delete());

        for (int i = 0; i < limit; i++) {
            assertEquals(i + 1, (int) lineNumbersCollector.getLineNumbers().get(i));
        }
    }

    @Test
    public void test_MultiFile() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile1 = new File(tmpDir, UUID.randomUUID().toString());
        File tmpFile2 = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        LineNumbersCollector lineNumbersCollector1 = new LineNumbersCollector(tmpFile1);
        LineNumbersCollector lineNumbersCollector2 = new LineNumbersCollector(tmpFile2);
        logCollectorManager.collect(lineNumbersCollector1, StandardCharsets.UTF_8);
        logCollectorManager.collect(lineNumbersCollector2, StandardCharsets.UTF_8);

        // 该线程将不断向tmpFile中写入数据
        CountDownLatch cdl = new CountDownLatch(2);
        List<BiConsumer<String, File>> appendFunctions = Arrays.asList(
            this::appendWindowsLine,
            this::appendLinuxLine,
            this::appendMacLine
        );
        // t1
        int limit1 = 10000;
        new Thread(() -> {
            try {
                if (tmpFile1.createNewFile()) {
                    for (int i = 0; i < limit1; i++) {
                        appendFunctions.get(i % appendFunctions.size())
                            .accept( (i + 1) + "-t1", tmpFile1);
                    }
                } else {
                    log.error("Create temp file failed: {}", tmpFile1);
                }
            } catch (IOException ex) {
                log.error("Create temp file error", ex);
            }
            cdl.countDown();
        }).start();
        // t2
        int limit2 = 20000;
        new Thread(() -> {
            try {
                if (tmpFile2.createNewFile()) {
                    for (int i = 0; i < limit2; i++) {
                        appendFunctions.get(i % appendFunctions.size())
                            .accept( (i + 1) + "-t2", tmpFile2);
                    }
                } else {
                    log.error("Create temp file failed: {}", tmpFile1);
                }
            } catch (IOException ex) {
                log.error("Create temp file error", ex);
            }
            cdl.countDown();
        }).start();

        cdl.await();

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile1.delete());
        assertTrue(tmpFile2.delete());

        for (int i = 0; i < limit1; i++) {
            assertEquals(i + 1, (int) lineNumbersCollector1.getLineNumbers().get(i));
        }
        for (int i = 0; i < limit2; i++) {
            assertEquals(i + 1, (int) lineNumbersCollector2.getLineNumbers().get(i));
        }
    }

    @Test
    public void test_SwitchFile() throws IOException, InterruptedException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile1 = new File(tmpDir, UUID.randomUUID().toString());
        File tmpFile2 = new File(tmpDir, UUID.randomUUID().toString());

        LogCollectorManager logCollectorManager = new LogCollectorManager();
        LineNumbersCollector lineNumbersCollector1 = new LineNumbersCollector(tmpFile1);
        LineNumbersCollector lineNumbersCollector2 = new LineNumbersCollector(tmpFile2);
        logCollectorManager.collect(lineNumbersCollector1, StandardCharsets.UTF_8);
        logCollectorManager.collect(lineNumbersCollector2, StandardCharsets.UTF_8);

        // 该线程将不断向tmpFile中写入数据
        CountDownLatch cdl = new CountDownLatch(2);
        List<BiConsumer<String, File>> appendFunctions = Arrays.asList(
            this::appendWindowsLine,
            this::appendLinuxLine,
            this::appendMacLine
        );
        // t1
        int limit1 = 1000;
        new Thread(() -> {
            try {
                if (tmpFile1.createNewFile()) {
                    for (int i = 0; i < limit1 / 2; i++) {
                        appendFunctions.get(i % appendFunctions.size())
                            .accept( (i + 1) + "-t1", tmpFile1);
                    }
                    if (tmpFile1.delete()) {
                        if (tmpFile1.createNewFile()) {
                            for (int i = limit1 / 2; i < limit1; i++) {
                                appendFunctions.get(i % appendFunctions.size())
                                    .accept( (i + 1) + "-t1-recreate", tmpFile1);
                            }
                        } else {
                            log.error("Recreate temp file failed: {}", tmpFile1);
                        }
                    } else {
                        log.error("Delete temp file failed: {}", tmpFile1);
                    }
                } else {
                    log.error("Create temp file failed: {}", tmpFile1);
                }
            } catch (IOException ex) {
                log.error("Create temp file error", ex);
            }
            cdl.countDown();
        }).start();
        // t2
        int limit2 = 20000;
        new Thread(() -> {
            try {
                if (tmpFile2.createNewFile()) {
                    for (int i = 0; i < limit2; i++) {
                        appendFunctions.get(i % appendFunctions.size())
                            .accept( (i + 1) + "-t2", tmpFile2);
                    }
                } else {
                    log.error("Create temp file failed: {}", tmpFile1);
                }
            } catch (IOException ex) {
                log.error("Create temp file error", ex);
            }
            cdl.countDown();
        }).start();

        cdl.await();

        sleep(500L); // 确保读取完成

        logCollectorManager.close();
        assertTrue(tmpFile1.delete());
        assertTrue(tmpFile2.delete());

        for (int i = 0; i < limit1; i++) {
            assertEquals(i + 1, (int) lineNumbersCollector1.getLineNumbers().get(i));
        }
        for (int i = 0; i < limit2; i++) {
            assertEquals(i + 1, (int) lineNumbersCollector2.getLineNumbers().get(i));
        }
    }

    private void appendWindowsLine(String prefix, File file) {
        appendLine(prefix, file, "\r\n");
    }

    private void appendLinuxLine(String prefix, File file) {
        appendLine(prefix, file, "\n");
    }

    private void appendMacLine(String prefix, File file) {
        appendLine(prefix, file, "\r");
    }

    private void appendLine(String prefix, File file, String newline) {
        String line = prefix + ":" + UUID.randomUUID() + newline;
        appendText(file, line);
    }

    private void appendText(File file, String text) {
        try {
            Files.writeString(file.toPath(), text, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            log.error("Append text to file failed", ex);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
