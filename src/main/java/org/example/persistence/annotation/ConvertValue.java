package org.example.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ConvertValue {

    public String getter() default "";

    public String setter() default "";

    public String aggregateGetter() default "";

    public int aggregateConstructorParam() default -1;
}
