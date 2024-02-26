package fun.fengwk.logfly.reporter.property;

import lombok.Data;

import java.util.*;

/**
 * 属性。
 * <ul>
 * <li>
 * 允许使用.分隔符表示属性路径，如：a.b.c
 * </li>
 * <li>
 * 允许使用[]表示数组追加，如：a[].b，表示向a的最后一个位置追加对象，b为新追加对象的属性
 * </li>
 * <li>
 * 允许使用[num]指定数组元素，如：a[0].b
 * </li>
 * </ul>
 *
 * @author fengwk
 */
public class Property {

    private final List<Node> path;

    private Property(List<Node> path) {
        this.path = path;
    }

    /**
     * 编译属性路径。
     *
     * @param path 属性路径
     * @return 编译后的属性路径
     */
    public static Property compile(String path) {
        List<Node> compliedPath = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(path, ".");
        while (tokenizer.hasMoreElements()) {
            String key = tokenizer.nextToken().trim();
            if (key.isBlank()) {
                throw new IllegalArgumentException("Invalid path: " + path);
            }

            boolean array = false;
            int index = -1;
            if (key.charAt(key.length() - 1) == ']') {
                int i = key.lastIndexOf('[');
                if (i == -1 || i >= key.length() - 1) {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
                String num = key.substring(i + 1, key.length() - 1);
                if (!num.isBlank()) {
                    index = Integer.parseInt(num);
                }
                array = true;
                key = key.substring(0, i);
            }

            compliedPath.add(new Node(key, array, index));
        }

        return new Property(compliedPath);
    }

    /**
     * 向目标的当前属性路径设置值。
     *
     * @param target 目标
     * @param propertyValue  值
     */
    public void setTarget(Map<String, Object> target, PropertyValue propertyValue) {
        Map<String, Object> current = target;
        for (int i = 0; i < path.size(); i++) {
            boolean last = i == path.size() - 1;
            Node node = path.get(i);
            if (node.isArray()) {
                // 当前节点表示属性为数组的情况
                if (!current.containsKey(node.getKey())
                    || !(current.get(node.getKey()) instanceof List)) {
                    // 如果属性不存在或数据类型不匹配则创建一个新的List
                    current.put(node.getKey(), new ArrayList<>());
                }
                // 获取当前层的array
                List<Object> array = (List<Object>) current.get(node.getKey());
                // 处理array中的元素
                if (node.getIndex() >= 0) {
                    // 确保索引位置合法
                    while (node.getIndex() >= array.size()) {
                        array.add(null);
                    }
                    if (last) {
                        // 如果是最后一个节点则设置值
                        array.set(node.getIndex(), propertyValue.getValue());
                    } else {
                        // 如果不是最后一个节点则添加对象进入下一层
                        if (array.get(node.getIndex()) == null
                            || !(array.get(node.getIndex()) instanceof Map)) {
                            // 如果索引位置为空或数据类型不匹配则创建一个新的Map
                            array.set(node.getIndex(), new LinkedHashMap<>());
                        }
                        current = (Map<String, Object>) array.get(node.getIndex());
                    }
                } else {
                    // 追加的情况
                    if (last) {
                        // 如果是最后一个节点则添加值
                        array.add(propertyValue.getValue());
                    } else {
                        // 如果不是最后一个节点则添加对象进入下一层
                        current = new LinkedHashMap<>();
                        array.add(current);
                    }
                }
            } else {
                // 当前节点表示属性为对象的情况
                if (last) {
                    // 如果是最后一个节点则设置值
                    current.put(node.getKey(), propertyValue.getValue());
                } else {
                    // 如果不是最后一个节点则添加对象进入下一层
                    if (!current.containsKey(node.getKey())) {
                        current.put(node.getKey(), new LinkedHashMap<>());
                    }
                    current = (Map<String, Object>) current.get(node.getKey());
                }
            }
        }
    }

    @Data
    static class Node {

        private final String key;
        private final boolean array;
        private final int index;

    }

}
