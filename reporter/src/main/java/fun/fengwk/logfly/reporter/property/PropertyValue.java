package fun.fengwk.logfly.reporter.property;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author fengwk
 */
public class PropertyValue {

    private final Object value;

    public PropertyValue(Object value) {
        this.value = value;
    }

    public static PropertyValue parse(String value, PropertyType type) {
        switch (type) {
            case STRING:
                return new PropertyValue(value);
            case NUMBER:
                try {
                    return new PropertyValue(Integer.parseInt(value));
                } catch (NumberFormatException ig1) {
                    try {
                        return new PropertyValue(Long.parseLong(value));
                    } catch (NumberFormatException ig2) {
                        try {
                            return new PropertyValue(new BigInteger(value));
                        } catch (NumberFormatException ig3) {
                            return new PropertyValue(new BigDecimal(value));
                        }
                    }
                }
            case BOOLEAN:
                return new PropertyValue(Boolean.parseBoolean(value));
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public Object getValue() {
        return value;
    }

}
