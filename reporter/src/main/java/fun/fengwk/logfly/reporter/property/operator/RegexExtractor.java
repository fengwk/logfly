package fun.fengwk.logfly.reporter.property.operator;

import fun.fengwk.convention4j.common.NullSafe;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fengwk
 */
public class RegexExtractor implements Extractor {

    private final boolean multi;
    private final Pattern pattern;
    private final Set<String> groups;

    public RegexExtractor(Pattern pattern, Set<String> multi) {
        this.pattern = Objects.requireNonNull(pattern, "Pattern must not be null");
        this.multi = NullSafe.of(multi);
    }

    @Override
    public Map<String, Object> extract(Object source) {
        if (!(source instanceof String)) {
            throw new IllegalArgumentException("Source must be a string");
        }

        Map<String, Object> context = new HashMap<>();
        Matcher matcher = pattern.matcher((String) source);
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int g = 0; g < groupCount; g++) {
                String value = matcher.group(g);
                if (multi.contains(matcher.group(g))) {
                    List<String> list = (List<String>) context.computeIfAbsent(
                        String.valueOf(g), k -> new ArrayList<>());
                    list.add(value);
                } else {
                    context.put(String.valueOf(g), value);
                }
            }
            pattern.pattern()
            matcher.group()
        }
        return null;
    }

}
