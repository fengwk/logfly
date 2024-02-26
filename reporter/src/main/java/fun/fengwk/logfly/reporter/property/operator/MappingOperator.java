package fun.fengwk.logfly.reporter.property.operator;

import lombok.Data;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
public class MappingOperator {

    private final Extractor extractor;
    private final String output;

    public MappingOperator(Extractor extractor, String output) {
        this.extractor = Objects.requireNonNull(extractor, "Extractor must not be null");
        this.output = Objects.requireNonNull(output, "Output must not be null");
    }

    public Object map(Object source) throws OgnlException {
        Map<String, Object> context = extractor.extract(source);
        return Ognl.getValue(output, context, source);
    }


    public static void main(String[] args) {

    }

}
