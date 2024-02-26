package fun.fengwk.logfly.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class CLI {

    private static final Map<String, LogCollectorFactory> FACTORIES;

    static {
        Map<String, LogCollectorFactory> factories = new HashMap<>();
//        factories.put("stdout", properties -> new StdOutLogCollector());
        FACTORIES = factories;
    }

    public static void main(String[] args) {

//        LogCollector logCollector = FACTORIES.get(collector).create(properties);
    }

}
