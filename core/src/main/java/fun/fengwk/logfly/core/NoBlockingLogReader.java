package fun.fengwk.logfly.core;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class NoBlockingLogReader implements AutoCloseable {

    private static final int BUFFER_SIZE = 1024 * 8;
    private final LineReader reader;
    private final Charset charset;
    private final RandomAccessFile randomAccessFile;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private int position = 0;
    private ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
    private int prevByte = 0; // 0-normal, 1-\r, 2-\n

    public NoBlockingLogReader(LineReader reader, File file, Charset charset) throws FileNotFoundException {
        this.reader = Objects.requireNonNull(reader, "Reader must not be null");
        this.charset = Objects.requireNonNull(charset, "Charset must not be null");
        this.randomAccessFile = new RandomAccessFile(
            Objects.requireNonNull(file, "File must not be null"), "r");
    }

    public void readLine() throws IOException {
        FileChannel channel = this.randomAccessFile.getChannel();
        channel.position(this.position);

        int readBytes = 0;
        int curReadBytes;
        while ((curReadBytes = channel.read(buffer)) != -1) {
            this.buffer.flip();
            while (this.buffer.hasRemaining()) {
                byte b = this.buffer.get();
                if (b == '\r') {
                    // case1: \r
                    this.lineBuffer.write(b);
                    this.prevByte = 1;
                } else {
                    if (b == '\n') {
                        // case2: \n
                        if (this.prevByte == 1) {
                            // windows \r\n
                            log.debug("Read windows newline \\r\\n");
                            readLineWithoutBackSlashR();
                        } else {
                            // linux \n
                            log.debug("Read linux newline \\n");
                            readFullLine();
                        }
                        this.prevByte = 2;
                    } else {
                        // case3: not \r or \n
                        if (this.prevByte == 1) {
                            log.debug("Read mac newline \\r");
                            // mac \r
                            readLineWithoutBackSlashR();
                        }
                        this.lineBuffer.write(b);
                        this.prevByte = 0;
                    }
                }
            }
            this.buffer.clear();
            readBytes += curReadBytes;
        }

        this.position += readBytes;
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
        // 读取剩余的内容
        if (this.prevByte == 1 && this.lineBuffer.size() > 1) {
            log.debug("Read remaining with out \\r");
            readLineWithoutBackSlashR();
        } else if (this.lineBuffer.size() > 0) {
            log.debug("Read remaining");
            readFullLine();
        }
        // 如果最后一个字符是 \r 或者 \n 则补充一个空行
        if (this.prevByte > 0) {
            log.debug("Read last blank newline");
            reader.readLine("");
        }
        log.debug("NoBlockingLogReader closed");
    }

    private void readLineWithoutBackSlashR() {
        byte[] bytes = this.lineBuffer.toByteArray();
        byte[] lineBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 0, lineBytes, 0, lineBytes.length);
        String line = new String(lineBytes, this.charset);
        this.reader.readLine(line);
        this.lineBuffer.reset();
    }

    private void readFullLine() {
        String line = this.lineBuffer.toString(this.charset);
        this.reader.readLine(line);
        this.lineBuffer.reset();
    }

}
