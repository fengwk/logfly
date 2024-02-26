package fun.fengwk.logfly.reporter.parser.regexjson;

import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.logfly.reporter.parser.LogLineParser;
import fun.fengwk.logfly.reporter.property.PropertyValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * @author fengwk
 */
public class RegexJsonLogLineParser implements LogLineParser {

    private final List<RegexJsonParseRule> rules;

    public RegexJsonLogLineParser(List<RegexJsonParseRule> rules) {
        this.rules = Objects.requireNonNull(rules, "Rules must not be null");
    }

    @Override
    public Map<String, Object> parse(String line) {
        // 目标对象
        Map<String, Object> target = new LinkedHashMap<>();
        // 使用所有规则解析目标对象
        for (RegexJsonParseRule rule : rules) {
            Matcher matcher = rule.getRegex().matcher(line);
            while (matcher.find()) {
                for (PropertyMapping mapping : rule.getMappings()) {
                    String value;
                    if (StringUtils.isBlank(mapping.getGroup())) {
                        // 如果没有设置group则默认取整个匹配的值
                        value = matcher.group();
                    } else {
                        try {
                            // 如果group为索引值则按照索引取值
                            int i = Integer.parseInt(mapping.getGroup());
                            value = matcher.group(i);
                        } catch (NumberFormatException ignore) {
                            // 如果group不是索引值则按照组名取值
                            value = matcher.group(mapping.getGroup());
                        }
                    }
                    // 设置属性值
                    mapping.getProperty().setTarget(
                        target, PropertyValue.parse(value, mapping.getType()));
                }
            }
        }
        // 将解析后的数据行转换为JSON字符串
        return target;
    }

}
