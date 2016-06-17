package de.gishmo.gwt.editor.processor;

import java.util.StringJoiner;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.editor.client.Editor.Path;
import com.squareup.javapoet.ClassName;

public class EditorModel {

  private Messager messager;
  private Types    types;

  /* parent element */
  private TypeElement       parent;
  /* the element */
  private VariableElement   variableElement;
  /* path */
  private String            path;
  /* model element */
  private TypeElement       modelElement;
  /* getter method in model (will be null if it does not exist) */
  private ExecutableElement getterMethod;
  private ExecutableElement setterMethod;
  private TypeElement       dataType;
  /* editor type */
  private TypeElement       editorTypeElement;

//  private boolean hasGetterMethod;
//  private boolean hasSetterMethod;

  private EditorModel(Builder builder) {
    this.variableElement = builder.variableElement;
    this.path = builder.path;
    this.parent = builder.parent;
    this.modelElement = builder.modelElement;
    this.messager = builder.messager;
    this.types = builder.types;
  }

  static EditorModel.Builder builder() {
    return new EditorModel.Builder();
  }

  public String getEditorSimpleName() {
    StringJoiner stringJoiner = new StringJoiner("");
    stringJoiner.add(this.editorTypeElement.getSimpleName()
                                           .toString())
                .add("_")
                .add(this.dataType.toString()
                                  .replace(".",
                                           "_"))
                .add("_SimpleBeanEditorDelegate");

    return stringJoiner.toString();
  }

  public TypeElement getEditorTypeElement() {
    return editorTypeElement;
  }

  public ClassName getDataTypeAsClassName() {
    return ClassName.get(MoreElements.getPackage(this.dataType)
                                     .toString(),
                         this.dataType.getSimpleName()
                                      .toString());
  }

  public ClassName getEditorTypeAsClassName() {
    return ClassName.get(MoreElements.getPackage(this.editorTypeElement)
                                     .toString(),
                         this.editorTypeElement.getSimpleName()
                                               .toString());
  }

  public TypeElement getModelElement() {
    return modelElement;
  }

  public String getContextName() {
    StringJoiner stringJoiner = new StringJoiner("");
    return stringJoiner.add(parent.getSimpleName())
                       .add("_")
                       .add(getAttibuteName().replace(".",
                                                      "_"))
                       .add("_Context")
                       .toString();
  }

  public String getAttibuteName() {
    String attributeName;

    Path pathAnotation = this.variableElement.getAnnotation(Path.class);
    if (pathAnotation != null) {
      attributeName = pathAnotation.value();
    } else {
      attributeName = variableElement.getSimpleName()
                                     .toString();
    }

    return attributeName;
  }

  public String getSimpleAttibuteName() {
    return getAttibuteName().indexOf(".") > 0 ?
           getAttibuteName().substring(getAttibuteName().indexOf(".") + 1) :
           getAttibuteName();
  }

  public String getPath() {
    return path;
  }

  private EditorModel setUp() {
    // check getter Method
    this.getterMethod = ModelUtils.findGetMethod(modelElement,
                                                 path);
    if (this.getterMethod == null) {
      messager.printMessage(Kind.WARNING,
                            String.format("%s does not implement a get method for path %s; ignoring.",
                                          modelElement.toString(),
                                          path));
    } else {
      if (this.getterMethod.getParameters()
                           .size() != 0) {
        messager.printMessage(Kind.WARNING,
                              String.format("%s -> %s does not have a zero argument signature; ignoring.",
                                            modelElement.toString(),
                                            path));
        this.getterMethod = null;
      }
    }
    // check setter Method
    this.setterMethod = ModelUtils.findSetMethod(modelElement,
                                                 path);
    if (this.setterMethod == null) {
      messager.printMessage(Kind.WARNING,
                            String.format("%s does not implement a set method for path %s; ignoring.",
                                          modelElement.toString(),
                                          path));
    } else {
      if (this.setterMethod.getParameters()
                           .size() != 1) {
        messager.printMessage(Kind.WARNING,
                              String.format("%s -> %s set method has wrong number of arguments; ignoring.",
                                            modelElement.toString(),
                                            path));
        this.setterMethod = null;
      }
    }
    // get data type
    if (this.getterMethod != null) {
      this.dataType = (TypeElement) MoreTypes.asDeclared(this.getterMethod.getReturnType())
                                             .asElement();
    } else if (setterMethod != null) {
      if (this.setterMethod.getParameters()
                           .size() == 1) {
        this.dataType = (TypeElement) MoreTypes.asDeclared(this.setterMethod.getParameters()
                                                                            .get(0)
                                                                            .asType())
                                               .asElement();
      }
    }
    // get the editor type
    TypeMirror editorTypeMirror = ModelUtils.getInterfaceType(types,
                                                              (TypeElement) MoreTypes.asElement(variableElement.asType()),
                                                              com.google.gwt.editor.client.IsEditor.class);
    ImmutableSet<TypeElement> listOfTypes = MoreTypes.referencedTypes(editorTypeMirror);
    if (listOfTypes.size() > 2) {
      this.editorTypeElement = listOfTypes.asList()
                                          .get(1);
    } else {
      messager.printMessage(Kind.WARNING,
                            String.format("no editor defined in %s ; ignoring.",
                                          com.google.gwt.editor.client.IsEditor.class.getCanonicalName()));
      this.setterMethod = null;
    }
    return this;
  }

  public static final class Builder {
    private Messager        messager;
    private Types           types;
    private TypeElement     parent;
    private TypeElement     modelElement;
    private VariableElement variableElement;
    private String          path;

    public EditorModel.Builder element(VariableElement variableElement) {
      this.variableElement = variableElement;
      return this;
    }

    public EditorModel.Builder messenger(Messager messager) {
      this.messager = messager;
      return this;
    }

    public EditorModel.Builder types(Types types) {
      this.types = types;
      return this;
    }

    public EditorModel.Builder modelElement(TypeElement modelElement) {
      this.modelElement = modelElement;
      return this;
    }

    public EditorModel.Builder parent(TypeElement parent) {
      this.parent = parent;
      return this;
    }

    public EditorModel.Builder path(String path) {
      this.path = path;
      return this;
    }

    public EditorModel build() {
      EditorModel model = new EditorModel(this);
      return model.setUp();
    }
  }
}
