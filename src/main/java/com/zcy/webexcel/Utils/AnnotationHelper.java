package com.zcy.webexcel.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AnnotationHelper {
    /**
     * 变更注解的属性值
     *
     * @param clazz     注解所在的实体类
     * @param tClass    注解类
     * @param filedName 要修改的注解属性名
     * @param value     要设置的属性值
     */
    public static <A extends Annotation> Class<?> changeAnnotationValue(Class<?> clazz, Class<A> tClass, String filedName, Object value) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                A annotation = field.getAnnotation(tClass);
                setAnnotationValue(annotation, filedName, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 设置注解中的字段值
     *
     * @param annotation 要修改的注解实例
     * @param fieldName  要修改的注解属性名
     * @param value      要设置的属性值
     */
    public static void setAnnotationValue(Annotation annotation, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        InvocationHandler handler = Proxy.getInvocationHandler(annotation);
        Field field = handler.getClass().getDeclaredField("memberValues");
        field.setAccessible(true);
        Map memberValues = (Map) field.get(handler);
        memberValues.put(fieldName, value);
    }

//    public static void main(String[] args) {
//        // 测试修改属性
//        changeAnnotationValue(Training.class, ExcelProperty.class, "value", new String[]{"需求收集名称（2021年度需求收集）", "需求编号收集"});
//        try {
//            Field[] fields = trainingClass.getDeclaredFields();
//            for (Field field : fields) {
//                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
//                System.out.println(Arrays.toString(annotation.value()));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
