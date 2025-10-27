package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME) 

@Target({ElementType.TYPE}) 
public @interface annotationType {
    String url() default "/liste";
    String methode() default "";
}
