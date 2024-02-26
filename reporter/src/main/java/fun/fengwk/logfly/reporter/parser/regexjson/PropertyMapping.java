package fun.fengwk.logfly.reporter.parser.regexjson;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.logfly.reporter.property.Property;
import fun.fengwk.logfly.reporter.property.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * @author fengwk
 */
@Builder
@Data
public class PropertyMapping {

    /**
     * 正则匹配的组名
     */
    private final String group;

    /**
     * 要映射的属性
     */
    private final Property property;

    /**
     * 属性类型
     */
    private final PropertyType type;

    private PropertyMapping(String group, Property property, PropertyType type) {
        this.group = group;
        this.property = Objects.requireNonNull(property, "Property must not be null");
        this.type = NullSafe.of(type, PropertyType.STRING);
    }

}
