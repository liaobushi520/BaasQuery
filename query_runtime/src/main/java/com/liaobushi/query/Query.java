package com.liaobushi.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liaozhongjun on 2017/10/11.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    String ASC = "ASC";

    String DESC = "DESC";

    String EMPTY_STRING="";

    String table() default EMPTY_STRING;

    String condition() default EMPTY_STRING;

    String limit() default "-1";

    String[] orderBy() default {EMPTY_STRING, ASC};

}
