package com.liaobushi.query.processor;

import com.droi.sdk.core.DroiCondition;
import com.squareup.javapoet.MethodSpec;

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

    private String leftVar;

    private Object rightVar;

    private boolean rightVarLiteral;

    private String operation;

    PureCondition(Messager messager, String s, int pos) {
        super(messager, s, pos);
    }

    @Override
    public void buildCondition(String parentVarName) {
        if (text.contains(EQ)) {
            buildConditinInner(EQ, DroiCondition.Type.EQ);
        } else if (text.contains(LT_OR_EQ)) {
            buildConditinInner(LT_OR_EQ, DroiCondition.Type.LT_OR_EQ);
        } else if (text.contains(GT_OR_EQ)) {
            buildConditinInner(GT_OR_EQ, DroiCondition.Type.GT_OR_EQ);
        } else if (text.contains(LT)) {
            buildConditinInner(LT, DroiCondition.Type.LT);
        } else if (text.contains(GT)) {
            buildConditinInner(GT, DroiCondition.Type.GT);
        } else if (text.contains(CANTAINS)) {
            buildConditinInner(CANTAINS, DroiCondition.Type.CONTAINS);
        }
        varName = parentVarName + position;
    }

    private void buildConditinInner(String op, String droiOp) {
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
            rightVarLiteral = false;
        } else if (tempRightVar.startsWith("'") && tempRightVar.endsWith("'")) {
            rightVar = tempRightVar.substring(1, tempRightVar.length() - 1);
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
