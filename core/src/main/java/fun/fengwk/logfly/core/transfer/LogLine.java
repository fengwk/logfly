package fun.fengwk.logfly.core.transfer;

import lombok.Data;

import java.io.File;

/**
 * @author fengwk
 */
@Data
public class LogLine {

    private final File logFile;
    private final String line;

}
