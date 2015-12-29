package org.mycommon.modules.persistence;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;
import org.mycommon.modules.utils.Collections3;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DynamicSpecifications {

    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (Collections3.isNotEmpty(filters)) {

                    List<Predicate> predicates = Lists.newArrayList();
                    for (SearchFilter filter : filters) {
                        // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
                        String[] names = StringUtils.split(filter.fieldName, ".");

                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(DynamicSpecifications.getExpression(root, names), filter.value));
                                break;
                            case NEQ:
                                predicates.add(builder.notEqual(DynamicSpecifications.getExpression(root, names), filter.value));
                                break;
                            case LIKE:
                                predicates.add(builder.like(DynamicSpecifications.<String>getExpression(root, names), "%" + filter.value + "%"));
                                break;
                            case LIKEP:
                                predicates.add(builder.like(DynamicSpecifications.<String>getExpression(root, names), filter.value + "%"));
                                break;
                            case PLIKE:
                                predicates.add(builder.like(DynamicSpecifications.<String>getExpression(root, names), "%" + filter.value));
                                break;
                            case GT:
                                predicates.add(builder.<Comparable>greaterThan(DynamicSpecifications.getExpression(root, names), (Comparable) filter.value));
                                break;
                            case LT:
                                predicates.add(builder.<Comparable>lessThan(DynamicSpecifications.getExpression(root, names), (Comparable) filter.value));
                                break;
                            case GTE:
                                predicates.add(builder.<Comparable>greaterThanOrEqualTo(DynamicSpecifications.getExpression(root, names), (Comparable) filter.value));
                                break;
                            case LTE:
                                predicates.add(builder.<Comparable>lessThanOrEqualTo(DynamicSpecifications.getExpression(root, names), (Comparable) filter.value));
                                break;
                            case IN:
                                if (filter.value instanceof Iterable) {
                                    predicates.add(builder.in(DynamicSpecifications.getExpression(root, names)).value(filter.value));
                                } else if (filter.value.getClass().isArray()) {
                                    predicates.add(builder.in(DynamicSpecifications.getExpression(root, names)).value(Arrays.asList(filter.value)));
                                } else {
                                    // only one element, convert to equal expression
                                    predicates.add(builder.equal(DynamicSpecifications.getExpression(root, names), filter.value));
                                }
                                break;
                        }
                    }

                    // 将所有条件用 and 联合起来
                    if (!predicates.isEmpty()) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }
                Where annotationWhere = entityClazz.getAnnotation(Where.class);
                if (annotationWhere != null) {//如果有Where 就不用加 builder.conjunction() 1=1,注入拦截会拦截掉,报错
                    return null;
                } else {
                    return builder.conjunction();
                }
            }
        };
    }

    private static <T> Expression<T> getExpression(Root<?> root, String[] names) {
        Path<T> expression = root.<T>get(names[0]);
        for (int i = 1; i < names.length; i++) {
            expression = expression.<T>get(names[i]);
        }
        return expression;
    }
}
