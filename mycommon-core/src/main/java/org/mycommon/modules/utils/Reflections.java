package org.mycommon.modules.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类.
 * <p>
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author KangXinghua
 */
public class Reflections {
    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory.getLogger(Reflections.class);

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
        invokeMethodByName(obj, setterMethodName, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    public static Class getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * <p>
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class getClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance) {
        Validate.notNull(instance, "Instance must not be null");
        Class clazz = instance.getClass();
        if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if ((e instanceof IllegalAccessException) || (e instanceof IllegalArgumentException)
                || (e instanceof NoSuchMethodException)) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

//    /**
//     * When {@code Type} initialized with a value of an object, its fully qualified class name
//     * will be prefixed with this.
//     *
//     * @see {@link Reflections#getClassName(Type)}
//     */
//    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
//    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";
//
//    /*
//     *  Utility class with static access methods, no need for constructor.
//     */
//
//    /**
//     * {@link Type#toString()} value is the fully qualified class name prefixed
//     * with {@link Reflections#TYPE_NAME_PREFIX}. This method will substring it, for it to be eligible
//     * for {@link Class#forName(String)}.
//     *
//     * @param type the {@code Type} value whose class name is needed.
//     * @return {@code String} class name of the invoked {@code type}.
//     *
//     * @see {@link Reflections#getClass()}
//     */
//    public static String getClassName(Type type) {
//        if (type==null) {
//            return "";
//        }
//        String className = type.toString();
//        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
//            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
//        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
//            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
//        }
//        return className;
//    }
//
//    /**
//     * Returns the {@code Class} object associated with the given {@link Type}
//     * depending on its fully qualified name.
//     *
//     * @param type the {@code Type} whose {@code Class} is needed.
//     * @return the {@code Class} object for the class with the specified name.
//     *
//     * @throws ClassNotFoundException if the class cannot be located.
//     *
//     * @see {@link Reflections#getClassName(Type)}
//     */
//    public static Class<?> getClass(Type type)
//            throws ClassNotFoundException {
//        String className = getClassName(type);
//        if (className==null || className.isEmpty()) {
//            return null;
//        }
//        return Class.forName(className);
//    }
//
//    /**
//     * Creates a new instance of the class represented by this {@code Type} object.
//     *
//     * @param type the {@code Type} object whose its representing {@code Class} object
//     * 		will be instantiated.
//     * @return a newly allocated instance of the class represented by
//     * 		the invoked {@code Type} object.
//     *
//     * @throws ClassNotFoundException if the class represented by this {@code Type} object
//     * 			cannot be located.
//     * @throws InstantiationException if this {@code Type} represents an abstract class,
//     *             an interface, an array class, a primitive type, or void;
//     *             or if the class has no nullary constructor;
//     *             or if the instantiation fails for some other reason.
//     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
//     *
//     * @see {@link Class#newInstance()}
//     */
//    public static Object newInstance(Type type)
//            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//        Class<?> clazz = getClass(type);
//        if (clazz==null) {
//            return null;
//        }
//        return clazz.newInstance();
//    }
//
//    /**
//     * Returns an array of {@code Type} objects representing the actual type
//     * arguments to this object.
//     * If the returned value is null, then this object represents a non-parameterized
//     * object.
//     *
//     * @param object the {@code object} whose type arguments are needed.
//     * @return an array of {@code Type} objects representing the actual type
//     * 		arguments to this object.
//     *
//     * @see {@link Class#getGenericSuperclass()}
//     * @see {@link ParameterizedType#getActualTypeArguments()}
//     */
//    public static Type[] getParameterizedTypes(Object object) {
//        Type superclassType = object.getClass().getGenericSuperclass();
//        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
//            return null;
//        }
//
//        return ((ParameterizedType)superclassType).getActualTypeArguments();
//    }
//
//    /**
//     * Checks whether a {@code Constructor} object with no parameter types is specified
//     * by the invoked {@code Class} object or not.
//     *
//     * @param clazz the {@code Class} object whose constructors are checked.
//     * @return {@code true} if a {@code Constructor} object with no parameter types is specified.
//     * @throws SecurityException If a security manager, <i>s</i> is present and any of the
//     *         following conditions is met:
//     *			<ul>
//     *             <li> invocation of
//     *             {@link SecurityManager#checkMemberAccess
//     *             s.checkMemberAccess(this, Member.PUBLIC)} denies
//     *             access to the constructor
//     *
//     *             <li> the caller's class loader is not the same as or an
//     *             ancestor of the class loader for the current class and
//     *             invocation of {@link SecurityManager#checkPackageAccess
//     *             s.checkPackageAccess()} denies access to the package
//     *             of this class
//     *         </ul>
//     *
//     * @see {@link Class#getConstructor(Class...)}
//     */
//    public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {
//        Class<?>[] empty = {};
//        try {
//            clazz.getConstructor(empty);
//        } catch (NoSuchMethodException e) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Returns a {@code Class} object that identifies the
//     * declared class for the field represented by the given {@code String name} parameter inside
//     * the invoked {@code Class<?> clazz} parameter.
//     *
//     * @param clazz the {@code Class} object whose declared fields to be
//     * 		checked for a certain field.
//     * @param name the field name as {@code String} to be
//     * 		compared with {@link Field#getName()}
//     * @return the {@code Class} object representing the type of given field name.
//     *
//     * @see {@link Class#getDeclaredFields()}
//     * @see {@link Field#getType()}
//     */
//    public static Class<?> getFieldClass(Class<?> clazz, String name) {
//        if (clazz==null || name==null || name.isEmpty()) {
//            return null;
//        }
//
//        Class<?> propertyClass = null;
//
//        for (Field field : clazz.getDeclaredFields()) {
//            field.setAccessible(true);
//            if (field.getName().equalsIgnoreCase(name)) {
//                propertyClass = field.getType();
//                break;
//            }
//        }
//
//        return propertyClass;
//    }
//
//    /**
//     * Returns a {@code Class} object that identifies the
//     * declared class as a return type for the method represented by the given
//     * {@code String name} parameter inside the invoked {@code Class<?> clazz} parameter.
//     *
//     * @param clazz the {@code Class} object whose declared methods to be
//     * 		checked for the wanted method name.
//     * @param name the method name as {@code String} to be
//     * 		compared with {@link Method#getName()}
//     * @return the {@code Class} object representing the return type of the given method name.
//     *
//     * @see {@link Class#getDeclaredMethods()}
//     * @see {@link Method#getReturnType()}
//     */
//    public static Class<?> getMethodReturnType(Class<?> clazz, String name) {
//        if (clazz==null || name==null || name.isEmpty()) {
//            return null;
//        }
//
//        name = name.toLowerCase();
//        Class<?> returnType = null;
//
//        for (Method method : clazz.getDeclaredMethods()) {
//            if (method.getName().equals(name)) {
//                returnType = method.getReturnType();
//                break;
//            }
//        }
//
//        return returnType;
//    }
//
//    /**
//     * Extracts the enum constant of the specified enum class with the
//     * specified name. The name must match exactly an identifier used
//     * to declare an enum constant in the given class.
//     *
//     * @param clazz the {@code Class} object of the enum type from which
//     * 		to return a constant.
//     * @param name the name of the constant to return.
//     * @return the enum constant of the specified enum type with the
//     *      specified name.
//     *
//     * @throws IllegalArgumentException if the specified enum type has
//     *         no constant with the specified name, or the specified
//     *         class object does not represent an enum type.
//     *
//     * @see {@link Enum#valueOf(Class, String)}
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public static Object getEnumConstant(Class<?> clazz, String name) {
//        if (clazz==null || name==null || name.isEmpty()) {
//            return null;
//        }
//        return Enum.valueOf((Class<Enum>)clazz, name);
//    }
}
