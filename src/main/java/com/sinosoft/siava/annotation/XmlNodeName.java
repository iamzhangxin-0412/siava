package com.sinosoft.siava.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于映射实体类和xml节点名称
 *
 * @author ZhangXin
 * @version 1.0
 *
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNodeName {

    /**
     * 在xml节点中某数据的名称
     *
     * @return 名称
     */
    String name() default "";
}
