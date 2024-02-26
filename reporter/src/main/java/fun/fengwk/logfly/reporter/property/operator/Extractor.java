package fun.fengwk.logfly.reporter.property.operator;

import java.util.Map;

/**
 * @author fengwk
 */
public interface Extractor {

    Map<String, Object> extract(Object source);

}
