package org.mycommon.modules.jpa.persistence;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mycommon.modules.utils.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SearchFilter {

    public enum Operator {
        EQ, NEQ, LIKE, LIKEP, PLIKE, GT, LT, GTE, LTE, IN
    }

    public enum Type {
        DATETIME,
        INTEGER,
        BOOLEAN
    }

    public String fieldName;
    public Object value;
    public Operator operator;

    public SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME
     */
    public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = Maps.newHashMap();

        for (Entry<String, Object> entry : searchParams.entrySet()) {
            // 过滤掉空值
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            if (value instanceof String) {
                if (Strings.isBlank((String) value)) {
                    continue;
                }
            }

            // 拆分operator与filedAttribute
            String[] names = Strings.split(key, "_");
            if (names.length < 2 || names.length > 3) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            String filedName = names[1];
            Operator operator = Operator.valueOf(names[0]);
            if (names.length == 3) {
                switch (Type.valueOf(names[2])) {
                    case DATETIME:
                        if (value instanceof Date) {
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                value = sdf.parse(value.toString());
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(key + " is not the time format yyyy-MM-dd HH: mm: ss");
                            }
                        }
                        break;
                    case INTEGER:
                        if (value.getClass().isArray()) {
                            List<Integer> valueList = Lists.newArrayList();
                            List<Object> objectList = Arrays.asList((Object[]) value);
                            for (Object o : objectList) {
                                valueList.add(Integer.valueOf(o.toString()));
                            }
                            value = valueList;
                        } else if (value instanceof Iterable) {

                        } else {
                            value = Integer.valueOf(value.toString());
                        }
                        break;
                    case BOOLEAN:
                        if (value instanceof Boolean) {
                        } else {
                            if (Strings.equals("0", value.toString())) {
                                value = false;
                            } else if (Strings.equals("1", value.toString())) {
                                value = true;
                            } else {
                                value = Boolean.valueOf(value.toString());
                            }
                        }
                        break;
                }
            }

            // 创建searchFilter
            SearchFilter filter = new SearchFilter(filedName, operator, value);
            filters.put(key, filter);
        }

        return filters;
    }
}
