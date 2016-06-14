package de.gishmo.gwt.editor.processor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.google.auto.common.BasicAnnotationProcessor.ProcessingStep;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.impl.AbstractEditorContext;
import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import de.gishmo.gwt.editor.client.annotation.IsEditor;

public class EditorProcessingStep
  implements ProcessingStep {

  private Messager messager;
  private Filer    filer;
  private Types    types;
  private Elements elements;

  private List<String> alreadyGeneratedEditorDelegates;

  private EditorProcessingStep(Builder builder) {
    this.messager = builder.messager;
    this.filer = builder.filer;
    this.types = builder.types;
    this.elements = builder.elements;

    this.alreadyGeneratedEditorDelegates = new ArrayList<>();
  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public Set<? extends Class<? extends Annotation>> annotations() {
    return Collections.singleton(IsEditor.class);
  }

  @Override
  public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
    for (Element element : elementsByAnnotation.get(IsEditor.class)) {
      EditorProcessingContext context = EditorProcessingContext.builder()
                                                               .withElements(this.elements)
                                                               .withFiler(this.filer)
                                                               .withMessenger(this.messager)
                                                               .withTypes(this.types)
                                                               .forElement(element)
                                                               .build();
      if (context != null) {
        generate(context);
      }
    }
    return ImmutableSet.of();
  }

  private void generate(EditorProcessingContext context) {
    context.getEditorModels()
           .stream()
           .forEach(editorModel -> generateEditorClass(editorModel));
//    context.getVariableElementList()
//           .stream()
//           .forEach(varibaleElement -> generateEditorClass(varibaleElement));
//    context.getVariableElementList()
//           .stream()
//           .forEach(varibaleElement -> generateEditorContextClass(context,
//                                                                  varibaleElement));
  }

  private void generateEditorClass(EditorModel editorModel) {
    // check weather we did already generate the class
//    if (variableElement)
    // name of editor context

    TypeSpec.Builder typeSpec = TypeSpec.classBuilder(editorModel.getEditorName())
                                        .addModifiers(Modifier.PUBLIC)
                                        .superclass(ClassName.get(SimpleBeanEditorDelegate.class));

    FieldSpec editorField = FieldSpec.builder(editorModel.getEditorTypeAsClassName(),
                                              "editor",
                                              Modifier.PRIVATE)
                                     .build();
    typeSpec.addField(editorField);

    FieldSpec objectField = FieldSpec.builder(editorModel.getDataTypeAsClassName(),
                                              "object",
                                              Modifier.PRIVATE)
                                     .build();
    typeSpec.addField(objectField);

    typeSpec.addMethod(MethodSpec.methodBuilder("getEditor")
                                 .returns(editorModel.getEditorTypeAsClassName())
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PROTECTED)
                                 .addStatement("return $N",
                                               editorField)
                                 .build());

    ParameterSpec editorParameter = ParameterSpec.builder(Editor.class,
                                                          "editor")
                                                 .build();
    typeSpec.addMethod(MethodSpec.methodBuilder("setEditor")
                                 .addParameter(editorParameter)
                                 .addStatement("this.$N = ($T) $N",
                                               editorField,
                                               editorModel.getEditorTypeAsClassName(),
                                               editorParameter)
                                 .addModifiers(Modifier.PROTECTED)
                                 .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("getObject")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .returns(editorModel.getDataTypeAsClassName())
                                 .addStatement("return $N",
                                               objectField)
                                 .build());

    ParameterSpec objectParameter = ParameterSpec.builder(Object.class,
                                                          "object")
                                                 .build();
    typeSpec.addMethod(MethodSpec.methodBuilder("setObject")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PROTECTED)
                                 .addParameter(objectParameter)
                                 .addStatement("this.$N = ($T) $N",
                                               objectField,
                                               editorModel.getDataTypeAsClassName(),
                                               objectParameter)
                                 .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("initializeSubDelegates")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PROTECTED)
                                 .build());

    ParameterSpec visitorParameter = ParameterSpec.builder(EditorVisitor.class,
                                                           "visitor")
                                                  .build();
    typeSpec.addMethod(MethodSpec.methodBuilder("accept")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .addParameter(visitorParameter)
                                 .build());

    JavaFile javaFile = JavaFile.builder(MoreElements.getPackage(editorModel.getEditorTypeElement())
                                                     .toString(),
                                         typeSpec.build())
                                .build();
    System.out.println(javaFile.toString());
//    try {
//      javaFile.writeTo(filer);
//    } catch (IOException e) {
//      // todo error handling
//      e.printStackTrace();
//      messager.printMessage(Diagnostic.Kind.ERROR,
//                            String.format("%s applied on a type that doesn't implement %s; ignoring.",
//                                          IsEditor.class.getCanonicalName(),
//                                          Editor.class.getCanonicalName()));
//    }

  }

  /**
   * <p>Create an EditorContext implementation that will provide access to
   * data owned by arent. In other words, given the EditorData
   * for a {@code PersonEditor} and the EditorData for a {@code AddressEditor}
   * nested in the {@code PersonEditor}, create an EditorContext that will
   * describe the relationship.</p>
   *
   * @param context
   * @param varibaleElement
   */
  private void generateEditorContextClass(EditorProcessingContext context,
                                          VariableElement varibaleElement) {
    // @Ignore ==> noting to do ToDo raus
    if (context.getModelElement()
               .getAnnotation(Ignore.class) != null) {
      return;
    }
    // attribute name
    String attributeName;
    // todo ist falsch ...
    Path pathAnotation = varibaleElement.getAnnotation(Path.class);
    if (pathAnotation != null) {
      attributeName = pathAnotation.value();
    } else {
      attributeName = varibaleElement.getSimpleName()
                                     .toString();
    }

    // name of editor context
    StringJoiner stringJoiner = new StringJoiner("");
    stringJoiner.add(context.getSimpleName())
                .add("_")
                .add(attributeName.replace(".",
                                           "_"))
                .add("_Context");

    TypeElement returnType = context.getModelReturnTypeForAttribute(attributeName);
    if (returnType == null) {
      return;
    }
    TypeSpec.Builder typeSpec = TypeSpec.classBuilder(stringJoiner.toString())
                                        .addModifiers(Modifier.PUBLIC)
                                        .superclass(ParameterizedTypeName.get(ClassName.get(AbstractEditorContext.class),
                                                                              ClassName.get(MoreElements.getPackage(returnType)
                                                                                                        .toString(),
                                                                                            returnType.getSimpleName()
                                                                                                      .toString())));

    FieldSpec parentField = FieldSpec.builder(TypeName.get(context.getModelElement()
                                                                  .asType()),
                                              "parent")
                                     .addModifiers(Modifier.PRIVATE,
                                                   Modifier.FINAL)
                                     .build();
    typeSpec.addField(parentField);

    // constructor
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                                               .addModifiers(Modifier.PUBLIC)
                                               .addParameter(ParameterSpec.builder(ClassName.get(MoreElements.getPackage(context.getModelElement())
                                                                                                             .toString(),
                                                                                                 context.getModelElement()
                                                                                                        .getSimpleName()
                                                                                                        .toString()),
                                                                                   "parent")
                                                                          .build())
                                               .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Editor.class),
                                                                                                             ClassName.get(context.getModelReturnTypeForAttribute(attributeName))),
                                                                                   "editor")
                                                                          .build())
                                               .addParameter(ClassName.get(String.class),
                                                             "path");
    constructor.addStatement("super(editor, path)");
    constructor.addStatement("this.parent = parent");
    typeSpec.addMethod(constructor.build());

    typeSpec.addMethod(MethodSpec.methodBuilder("canSetInModel")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .returns(ClassName.get(Boolean.class))
                                 .addStatement(createCanSetInModelStatement(context,
                                                                            attributeName))
                                 .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("checkAssignment")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .addParameter(ClassName.get(Object.class),
                                               "value")
                                 .returns(ClassName.get(context.getModelReturnTypeForAttribute(attributeName)))
                                 .addStatement("return ($T) value",
                                               ClassName.get(context.getModelReturnTypeForAttribute(attributeName)))
                                 .build());

    typeSpec.addMethod(MethodSpec.methodBuilder("getEditedType")
                                 .addAnnotation(Override.class)
                                 .addModifiers(Modifier.PUBLIC)
                                 .returns(ClassName.get(Class.class))
                                 .addStatement("return $T.class",
                                               ClassName.get(context.getModelReturnTypeForAttribute(attributeName)))
                                 .build());

    JavaFile javaFile = JavaFile.builder(MoreElements.getPackage(context.getModelElement())
                                                     .toString(),
                                         typeSpec.build())
                                .build();
    System.out.println(javaFile.toString());
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      // todo error handling
      e.printStackTrace();
      messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s applied on a type that doesn't implement %s; ignoring.",
                                          IsEditor.class.getCanonicalName(),
                                          Editor.class.getCanonicalName()));
    }
  }

  private String createCanSetInModelStatement(EditorProcessingContext context,
                                              String attributeName) {
    String statement = "parent != null &&";

    return statement;
  }

  private TypeMirror getInterfaceType(TypeElement element,
                                      Class<?> clazz) {
    Optional<? extends TypeMirror> optinals = element.getInterfaces()
                                                     .stream()
                                                     .filter(interfaeType -> ClassName.get(types.erasure(interfaeType))
                                                                                      .toString()
                                                                                      .contains(clazz.getCanonicalName()))
                                                     .findFirst();
    if (optinals.isPresent()) {
      return optinals.get();
    } else {
      return getInterfaceType((TypeElement) types.asElement(element.getSuperclass()),
                              clazz);
    }
  }

  public static final class Builder {
    Messager messager;
    Filer    filer;
    Types    types;
    Elements elements;

    public Builder withMessenger(Messager messager) {
      this.messager = messager;
      return this;
    }

    public Builder withFiler(Filer filer) {
      this.filer = filer;
      return this;
    }

    public Builder withTypes(Types types) {
      this.types = types;
      return this;
    }

    public Builder withElements(Elements elements) {
      this.elements = elements;
      return this;
    }

    public EditorProcessingStep build() {
      return new EditorProcessingStep(this);
    }
  }
}
