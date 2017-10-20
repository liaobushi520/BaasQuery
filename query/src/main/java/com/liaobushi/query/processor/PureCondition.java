package com.liaobushi.query.processor;

import com.droi.sdk.core.DroiCondition;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by liaozhongjun on 2017/10/17.
 */

class PureCondition extends Condition {

    private static final String EQ = "==";

    private static final String LT = "<";

    private static final String LT_OR_EQ = "<=";

    private static final String GT = ">";

    private static final String GT_OR_EQ = ">=";

    private static final String CANTAINS = "CONTAINS";

    private static final String IN = "IN";

    private String leftVar;

    private Object rightVar;

    private boolean rightVarLiteral;

    private String operation;

    PureCondition(Messager messager, String s, int pos) {
        super(messager, s, pos);
    }

    @Override
    public void buildCondition(String parentVarName, List<ParameterSpec> parameters) {
        if (text.contains(EQ)) {
            buildConditinInner(EQ, DroiCondition.Type.EQ, parameters);
        } else if (text.contains(LT_OR_EQ)) {
            buildConditinInner(LT_OR_EQ, DroiCondition.Type.LT_OR_EQ, parameters);
        } else if (text.contains(GT_OR_EQ)) {
            buildConditinInner(GT_OR_EQ, DroiCondition.Type.GT_OR_EQ, parameters);
        } else if (text.contains(LT)) {
            buildConditinInner(LT, DroiCondition.Type.LT, parameters);
        } else if (text.contains(GT)) {
            buildConditinInner(GT, DroiCondition.Type.GT, parameters);
        } else if (text.contains(CANTAINS)) {
            buildConditinInner(CANTAINS, DroiCondition.Type.CONTAINS, parameters);
        } else if (text.contains(IN)) {
            buildConditinInner(IN, DroiCondition.Type.IN, parameters);
        }
        varName = parentVarName + position;
    }

    private boolean isSuperInterface(Class checked, Class superInterface) {
        Class[] checkedInterfaces = checked.getInterfaces();
        if (checkedInterfaces == null || checkedInterfaces.length <= 0) {
            return false;
        }
        List<Class> interfaces = Arrays.asList(checkedInterfaces);
        List<Class> tempArray = new ArrayList<>();
        while (interfaces.size() > 0) {
            for (Class c : interfaces) {
                messager.printMessage(Diagnostic.Kind.WARNING, c.getCanonicalName() + "");
                if (superInterface.getCanonicalName().equals(c.getCanonicalName())) {
                    return true;
                }
            }
            tempArray.clear();
            for (Class c : interfaces) {
                Class[] arr = c.getInterfaces();
                if (arr != null && arr.length > 0) {
                    tempArray.addAll(Arrays.asList(arr));
                }
            }
            interfaces = tempArray;
        }
        return false;
    }

    private void checkINOperation(List<ParameterSpec> parameters) {
        for (ParameterSpec parameterSpec : parameters) {
            if (parameterSpec.name.equals(rightVar)) {
                try {
                    String rawClassName = parameterSpec.type.toString();
                    if (parameterSpec.type instanceof ParameterizedTypeName) {
                        rawClassName = ((ParameterizedTypeName) parameterSpec.type).rawType.toString();
                    }
                    Class c = Class.forName(rawClassName, true, List.class.getClassLoader());
                    if (!isSuperInterface(c, List.class)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "condition :" + text + " ,IN operation's right var must be array,"
                                + "current is " + parameterSpec.type);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void buildConditinInner(String op, String droiOp, List<ParameterSpec> parameters) {
        String[] result = text.split(op);
        if (result.length != 2) {
            messager.printMessage(Diagnostic.Kind.WARNING, "condition text error:" + text);
        }
        operation = droiOp;
        leftVar = result[0].trim();
        String tempRightVar = result[1].trim();
        rightVarLiteral = true;
        if (tempRightVar.startsWith(QueryProcessor.VAR_REF_SYMBOL)) {
            rightVar = tempRightVar.substring(1);
            if (IN.equals(operation)) {
                checkINOperation(parameters);
            }
            rightVarLiteral = false;
        } else if (tempRightVar.startsWith("'") && tempRightVar.endsWith("'")) {
            rightVar = tempRightVar.substring(1, tempRightVar.length() - 1);
        } else if (tempRightVar.startsWith("[") && tempRightVar.endsWith("]")) {
            if (!IN.equals(operation)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "only IN operation's right var is arrry");
            }
        } else {
            try {
                rightVar = Integer.parseInt(tempRightVar);
            } catch (NumberFormatException e) {
                if (tempRightVar.equalsIgnoreCase("true")) {
                    rightVar = true;
                } else if (tempRightVar.equalsIgnoreCase("false")) {
                    rightVar = false;
                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR, "condition statement illegal,please check:" + text);
                }
            }
        }
    }

    @Override
    public void brewJava(MethodSpec.Builder builder) {
        String statement;
        Object[] types;
        if (!rightVarLiteral) {
            statement = "$T " + varName + "=$T.cond($S,$S," + rightVar + ")";
            types = new Object[]{DroiCondition.class, DroiCondition.class, leftVar, operation};
        } else {
            if (rightVar instanceof String) {
                statement = "$T " + varName + "=$T.cond($S,$S,$S)";
            } else {
                statement = "$T " + varName + "=$T.cond($S,$S,$L)";
            }
            types = new Object[]{DroiCondition.class, DroiCondition.class, leftVar, operation, rightVar};
        }
        builder.addStatement(statement, types);
    }


}
