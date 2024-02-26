package fun.fengwk.logfly.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.fengwk.logfly.core.collector.AbstractLogCollector;
import fun.fengwk.logfly.core.collector.LogCollector;

/**
 * @author fengwk
 */
public class LineNumbersCollector extends AbstractLogCollector {

    private static final Pattern PATTERN = Pattern.compile("^\\d+");

    private final List<Integer> lineNumbers = new ArrayList<>();

    protected LineNumbersCollector(File logFile) {
        super(logFile);
    }

    @Override
    public void readLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            String lineNumber = matcher.group();
            lineNumbers.add(Integer.parseInt(lineNumber));
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }

}
