package fun.fengwk.logfly.reporter.parser.regexjson;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author fengwk
 */
@Builder
@Data
public class RegexJsonParseRule {

    /**
     * 用于解析的正则表达式
     */
    private final Pattern regex;

    /**
     * 正则解析与属性的映射关系
     */
    private final List<PropertyMapping> mappings;

    private RegexJsonParseRule(Pattern regex, List<PropertyMapping> mappings) {
        this.regex = Objects.requireNonNull(regex, "Regex must not be null");
        this.mappings = Objects.requireNonNull(mappings, "Mappings must not be null");
    }

}
