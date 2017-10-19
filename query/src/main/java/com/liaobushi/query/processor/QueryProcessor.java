package com.liaobushi.query.processor;

import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.liaobushi.query.BaasCall;
import com.liaobushi.query.DefaultBaasCall;
import com.liaobushi.query.Query;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.tools.Diagnostic;


/**
 * Created by liaozhongjun on 2017/10/11.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.liaobushi.query.Query", "com.liaobushi.query.Service"})
public class QueryProcessor extends AbstractProcessor {

    public static final String VAR_REF_SYMBOL = "$";
    private static final ClassName BAAS_CALL = ClassName.get(BaasCall.class);

    private TypeElement serviceElement;
    private ExecutableElement queryElement;
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        serviceElement = elementUtils.getTypeElement("com.liaobushi.query.Service");
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> serviceElements = roundEnvironment.getElementsAnnotatedWith(serviceElement);
        for (Element element : serviceElements) {
            processService(element);
        }
        return false;
    }

    private void processService(Element service) {
        TypeElement typeElement = (TypeElement) service;
        try {
            TypeName superInterface = TypeName.get(service.asType());
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "Imp").addSuperinterface(superInterface).addModifiers(Modifier.PUBLIC);
            List<? extends Element> methodElements = typeElement.getEnclosedElements();
            for (Element element : methodElements) {
                if (element instanceof ExecutableElement) {
                    Query query = element.getAnnotation(Query.class);
                    if (query != null) {
                        MethodSpec methodSpec = parseMethodAnnotation((ExecutableElement) element, query);
                        typeSpecBuilder.addMethod(methodSpec);
                    }
                }
            }
            JavaFile.builder(elementUtils.getPackageOf(typeElement).getQualifiedName().toString(), typeSpecBuilder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MethodSpec.Builder buildQueryCodeBlock(MethodSpec.Builder methodSpecBuild, Query query) {
        methodSpecBuild.addStatement("$T builder=$T.newBuilder()", DroiQuery.Builder.class, DroiQuery.Builder.class);
        //table param
        String tableName = query.table();
        if (tableName.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "table name can not be null or \"\"");
        }
        methodSpecBuild.addStatement("builder.query($S)", tableName);
        //condition param
        String condition = query.condition();
        if (condition.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING, "condition statement is null ,so query all data");
        }
        String varName = Condition.build(methodSpecBuild, messager, query.condition());
        methodSpecBuild.addStatement("$T cond=" + varName, DroiCondition.class)
                .addStatement("$T droiError=new $T()", DroiError.class, DroiError.class)
                .addStatement("builder.where(cond)");
        //limit param
        String limit = query.limit();
        if (!limit.isEmpty()) {
            if (limit.startsWith(VAR_REF_SYMBOL)) {
                methodSpecBuild.addStatement("builder.limit(" + limit.substring(1).trim() + ")");
            } else {
                try {
                    int limitValue = Integer.parseInt(limit);
                    if (limitValue == -1) {
                        messager.printMessage(Diagnostic.Kind.NOTE, "omit limit value");
                    }else {
                        methodSpecBuild.addStatement("builder.limit($L)", Integer.parseInt(limit));
                    }
                } catch (NumberFormatException e) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "limit value is illegal: " + limit);
                }
            }
        }
        //orderBy param
        String[] orderBy = query.orderBy();
        if (!orderBy[0].isEmpty()) {
            boolean asc = true;
            if (orderBy[1].equals(Query.DESC)) {
                asc = false;
            }
            if (orderBy[0].startsWith(VAR_REF_SYMBOL)) {
                methodSpecBuild.addStatement("builder.orderBy(" + orderBy[0].substring(1).trim() + "," + asc + ")");
            } else {
                methodSpecBuild.addStatement("builder.orderBy($S," + asc + ")", orderBy[0].trim());
            }
        }
        methodSpecBuild.addStatement("return builder.build().runQuery(droiError)");
        return methodSpecBuild;
    }


    private MethodSpec parseMethodAnnotation(ExecutableElement element, Query query) {
        TypeMirror returnTypeMirror = element.getReturnType();
        TypeName returnTypeName = TypeName.get(returnTypeMirror);
        List<? extends VariableElement> vars = element.getParameters();
        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        for (VariableElement var : vars) {
            parameterSpecs.add(ParameterSpec.get(var).toBuilder().addModifiers(Modifier.FINAL).build());
        }
        MethodSpec.Builder methodSpecBuild = MethodSpec.methodBuilder(element.getSimpleName().toString()).returns(returnTypeName).addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
                .addParameters(parameterSpecs);

        if (returnTypeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) returnTypeName;
            if (parameterizedTypeName.rawType.equals(BAAS_CALL)) {
                List<TypeName> typeArguments = parameterizedTypeName.typeArguments;
                if (typeArguments.size() != 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "type argument must be 1");
                }
                TypeName enclosedTypeName = typeArguments.get(0);
                MethodSpec.Builder callMethodSpecBuilder = MethodSpec.methodBuilder("call").addModifiers(Modifier.PUBLIC).returns(enclosedTypeName).addException(ClassName.get(Exception.class)).addAnnotation(Override.class);
                buildQueryCodeBlock(callMethodSpecBuilder, query);
                TypeSpec callable = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callable.class), enclosedTypeName))
                        .addMethod(callMethodSpecBuilder.build())
                        .build();
                methodSpecBuild.addStatement("$T<$T> baasCall=new $T($L)", BaasCall.class, enclosedTypeName, DefaultBaasCall.class, callable);
                methodSpecBuild.addStatement("return baasCall");
            } else {
                buildQueryCodeBlock(methodSpecBuild, query);
            }
        }
        return methodSpecBuild.build();
    }
}
