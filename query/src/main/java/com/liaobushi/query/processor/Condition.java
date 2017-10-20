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
        this.text = Utils.removeParentheses(s.trim());
        this.position = pos;


    }

    public abstract void buildCondition(String parentVarName, List<ParameterSpec> parameters);

    public abstract void brewJava(MethodSpec.Builder builder);

    private static int checkConditionType(String s) {
        String[] result = s.split(SIMPLE_SPLIT_PATTERN);
        if (result.length > 1) {
            return COMPOUND_CONDITION;
        }
        return PURE_CONDITION;
    }


    static String build(MethodSpec.Builder builder, Messager messager, String s, List<ParameterSpec> parameters) {
        s = Utils.removeParentheses(s);
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
