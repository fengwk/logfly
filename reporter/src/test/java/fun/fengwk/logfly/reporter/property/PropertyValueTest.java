package fun.fengwk.logfly.reporter.property;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author fengwk
 */
public class PropertyValueTest {

    @Test
    public void test() {
        check(1, "1", PropertyType.NUMBER);
        check(11111111111111L, "11111111111111", PropertyType.NUMBER);
        check(new BigInteger("22222222222222222222222"), "22222222222222222222222", PropertyType.NUMBER);
        check(new BigDecimal("123.123"), "123.123", PropertyType.NUMBER);
        check("xxxxxxxxxxxxx", "xxxxxxxxxxxxx", PropertyType.STRING);
        check("", "", PropertyType.STRING);
        check(true, "true", PropertyType.BOOLEAN);
        check(false, "false", PropertyType.BOOLEAN);
    }

    private void check(Object expected, String value, PropertyType propertyType) {
        assertEquals(expected, PropertyValue.parse(value, propertyType).getValue());
    }

}
