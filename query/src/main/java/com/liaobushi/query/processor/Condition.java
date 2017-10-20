package com.liaobushi.query.processor;

import com.droi.sdk.core.DroiCondition;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

import javax.annotation.processing.Messager;

/**
 * Created by liaozhongjun on 2017/10/17.
 */

public abstract class Condition {

    public static final String SIMPLE_SPLIT_PATTERN = "&&|\\|\\|";

    public static final String VAR_NAME_PRRFIX = "condition";

    public static final int COMPOUND_CONDITION = 1;

    public static final int PURE_CONDITION = 2;

    protected int position;

    protected String varName;

    protected Messager messager;

    protected String text;


    public Condition(Messager messager, String s, int pos) {
        this.messager = messager;
        s = s.trim();
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
        }
        this.text = s;
        this.position = pos;


    }

    public abstract void buildCondition(String parentVarName, List<ParameterSpec> parameters);

    public abstract void brewJava(MethodSpec.Builder builder);

    public static int checkConditionType(String s) {
        String[] result = s.split(SIMPLE_SPLIT_PATTERN);
        if (result != null && result.length > 1) {
            return COMPOUND_CONDITION;
        }
        return PURE_CONDITION;
    }


    public static String build(MethodSpec.Builder builder, Messager messager, String s, List<ParameterSpec> parameters) {
        String varName = VAR_NAME_PRRFIX + 0;
        Condition condition;
        if (checkConditionType(s) == COMPOUND_CONDITION) {
            condition = new CompoundCondition(messager, s, 0);
        } else {
            condition = new PureCondition(messager, s, 0);
        }
        condition.buildCondition(varName, parameters);
        condition.brewJava(builder);
        builder.addStatement("$T " + varName + "=" + condition.varName, DroiCondition.class);
        return varName;
    }
}
