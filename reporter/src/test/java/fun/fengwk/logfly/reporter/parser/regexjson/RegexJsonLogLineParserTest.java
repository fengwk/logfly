package fun.fengwk.logfly.reporter.parser.regexjson;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.logfly.core.transfer.LogLine;
import fun.fengwk.logfly.reporter.property.Property;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author fengwk
 */
public class RegexJsonLogLineParserTest {

    @Test
    public void test() {
        Pattern regex = Pattern.compile("(\\[.+\\])(.+)");
        PropertyMapping mapping1 = PropertyMapping.builder()
            .group("1")
            .property(Property.compile("log_level"))
            .build();
        PropertyMapping mapping2 = PropertyMapping.builder()
            .group("2")
            .property(Property.compile("source"))
            .build();
        RegexJsonParseRule rule = RegexJsonParseRule.builder()
            .regex(regex)
            .mappings(Arrays.asList(mapping1, mapping2))
            .build();
        RegexJsonLogLineParser parser = new RegexJsonLogLineParser(Collections.singletonList(rule));
        Map<String, Object> target = parser.parse("[DEBUG] user: {\"name\": \"fengwk\", \"age\": 18}");
        assertEquals(
            "{\"log_level\":\"[DEBUG]\",\"source\":\" user: {\\\"name\\\": \\\"fengwk\\\", \\\"age\\\": 18}\"}",
            JsonUtils.toJson(target));
    }

}
