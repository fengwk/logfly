package fun.fengwk.logfly.reporter.property;

import java.beans.PropertyEditor;

/**
 * 属性类型
 *
 * @author fengwk
 */
public enum PropertyType {

    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean");

    private final String code;

    PropertyType(String code) {
        this.code = code;
    }

    public static PropertyType of(String code) {
        for (PropertyType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

}
