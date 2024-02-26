package fun.fengwk.logfly.reporter;

import fun.fengwk.logfly.core.transfer.LogLine;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.Map;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
@Getter
public class TargetLogLine extends LogLine {

    private final Map<String, Object> target;

    public TargetLogLine(File logFile, String line, Map<String, Object> target) {
        super(logFile, line);
        this.target = target;
    }

}
