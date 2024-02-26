package fun.fengwk.logfly.reporter.property;

import fun.fengwk.convention4j.common.json.JsonUtils;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author fengwk
 */
public class PropertyTest {

    @Test
    public void test() {
        Property p1 = Property.compile("a.b.c");
        Property p2 = Property.compile("a.b1[2].c");
        Property p3 = Property.compile("a.b1[].c");
        Property p4 = Property.compile("a.b2.c[0]");
        Property p5 = Property.compile("a.b2.c[]");
        Map<String, Object> target = new LinkedHashMap<>();
        p1.setTarget(target, PropertyValue.parse("1", PropertyType.NUMBER));
        p2.setTarget(target, PropertyValue.parse("1", PropertyType.NUMBER));
        p3.setTarget(target, PropertyValue.parse("1", PropertyType.NUMBER));
        p4.setTarget(target, PropertyValue.parse("1", PropertyType.NUMBER));
        p5.setTarget(target, PropertyValue.parse("1", PropertyType.NUMBER));
        assertEquals("{\"a\":{\"b\":{\"c\":1},\"b1\":[null,null,{\"c\":1},{\"c\":1}],\"b2\":{\"c\":[1,1]}}}",
            JsonUtils.toJson(target));
    }

}
