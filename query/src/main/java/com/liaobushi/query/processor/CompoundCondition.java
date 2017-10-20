package com.liaobushi.query.processor;

import com.droi.sdk.core.DroiCondition;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by liaozhongjun on 2017/10/17.
 */

public class CompoundCondition extends Condition {

    private static final String AND = "&&";

    private static final String OR = "||";

    public static final String SPLIT_PATTERN = "(?!\\([^\\(\\)]*)(&&|\\|\\|)(?![^\\(\\)]*\\))";

    public static final String PARENTHESES_PATTERN = "(?<=\\()[^\\)]+";


    private List<Condition> subConditions = new ArrayList<>();

    private List<String> operations = new ArrayList<>();

    public CompoundCondition(Messager messager, String s, int position) {
        super(messager, s, position);
    }


    @Override
    public void buildCondition(String parentVarName, List<ParameterSpec> parameters) {
        Pattern pattern = Pattern.compile(SPLIT_PATTERN);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            operations.add(matcher.group());
        }
        String[] results = text.split(SPLIT_PATTERN);
        varName = parentVarName + position;
        for (int i = 0; i < results.length; i++) {
            String sub = results[i].trim();
            messager.printMessage(Diagnostic.Kind.WARNING, sub + "");
            Condition condition;
            if (sub.startsWith("(") && sub.endsWith(")")) {
                sub = sub.substring(1, sub.length() - 1);
            }
            if (sub.contains(AND) || sub.contains(OR)) {
                condition = new CompoundCondition(messager, sub, i);
            } else {
                condition = new PureCondition(messager, sub, i);
            }
            condition.buildCondition(varName,parameters);
            subConditions.add(condition);
        }
    }

    @Override
    public void brewJava(MethodSpec.Builder builder) {

        StringBuilder statement = new StringBuilder("$T " + varName + "=");
        for (Condition condition : subConditions) {
            condition.brewJava(builder);
        }

        for (int i = 0; i < operations.size(); i++) {
            String operation = operations.get(i);
            if (operation.equals(AND)) {
                Condition leftSub = subConditions.get(i);
                Condition rightSub = subConditions.get(i + 1);
                String newVarName = leftSub.varName + "_" + rightSub.varName.substring(Condition.VAR_NAME_PRRFIX.length());
                builder.addStatement("$T " + newVarName + "=" + leftSub.varName + ".and(" + rightSub.varName + ")", DroiCondition.class);
                rightSub.varName = newVarName;
                subConditions.set(i, null);
            }
        }

        boolean findFirstNoNull = false;
        for (int i = 0; i < subConditions.size(); i++) {
            Condition condition = subConditions.get(i);
            if (condition == null) {
                continue;
            }
            if (!findFirstNoNull) {
                findFirstNoNull = true;
                statement.append(condition.varName);
                continue;
            }
            statement.append(".or(").append(condition.varName).append(")");
        }
        builder.addStatement(statement.toString(), DroiCondition.class);
    }
}
