package de.gishmo.gwt.editor.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.HasValue;

import de.gishmo.gwt.editor.client.annotation.IsEditor;

class EditorProcessingContext {

  private static final String IMPL_NAME = "EditorDriverImpl";

  private Messager messager;
  private Filer    filer;
  private Types    types;
  private Elements elements;
  private Element  element;

  private String            consumerPackageName;
  private String            consumerSimpleName;
  private String            consumerSimpleImplName;

  private TypeElement       editorElement;
  private String            editorPackageName;
  private String            editorSimpleName;
  private TypeElement       modelElement;
  private String            modelPackageName;
  private String            modelSimpleName;
  private List<EditorModel> editorModels;

  private EditorProcessingContext(Builder builder) {
    this.messager = builder.messager;
    this.filer = builder.filer;
    this.types = builder.types;
    this.elements = builder.elements;
    this.element = builder.element;

    this.editorModels = new ArrayList<>();
  }

  static Builder builder() {
    return new Builder();
  }

  public List<EditorModel> getEditorModels() {
    return editorModels;
  }

  public String getConsumerPackageName() {
    return consumerPackageName;
  }

  public String getConsumerSimpleName() {
    return consumerSimpleName;
  }

  public String getConsumerSimpleImplName() {
    return consumerSimpleImplName;
  }

  public TypeElement getEditorElement() {
    return editorElement;
  }

  public String getEditorPackageName() {
    return editorPackageName;
  }

  public String getEditorSimpleName() {
    return editorSimpleName;
  }

  public TypeElement getModelElement() {
    return modelElement;
  }

  public Element getElement() {
    return element;
  }
//
//  public List<TypeElement> getEditorElementList() {
//    return editorElementList;
//  }

  public TypeElement getModelReturnTypeForAttribute(String attribute) {
    return getModelReturnTypeForAttribute(this.modelElement,
                                          attribute);
  }

  private TypeElement getModelReturnTypeForAttribute(TypeElement model,
                                                     String attribute) {
    // TODO ... woher wissen wir, dass das Feld genauso heisst wie die Setter.Methode
    // TODO bei address.street, erst mal das Street-Model holen .....
    if (attribute.contains(".")) {
      String parentAttribute = attribute.substring(0,
                                                   attribute.indexOf("."));
      Optional<? extends Element> variableElementOptional = elements.getAllMembers(model)
                                                                    .stream()
                                                                    .filter(element -> element.getKind()
                                                                                              .equals(ElementKind.FIELD))
                                                                    .filter(element -> element.getSimpleName()
                                                                                              .toString()
                                                                                              .equals(parentAttribute))
                                                                    .findFirst();
      Element variableElement;
      if (variableElementOptional.isPresent()) {
        variableElement = variableElementOptional.get();
      } else {
        // todo message
        messager.printMessage(Diagnostic.Kind.ERROR,
                              String.format("%s applied on a type that doesn't implement %s; ignoring.",
                                            IsEditor.class.getCanonicalName(),
                                            Editor.class.getCanonicalName()));
        return null;
      }
      return getModelReturnTypeForAttribute((TypeElement) MoreTypes.asDeclared(variableElement.asType())
                                                                   .asElement(),
                                            attribute.substring(attribute.indexOf(".") + 1));
    } else {
      Optional<? extends Element> variableElementOptional = elements.getAllMembers(model)
                                                                    .stream()
                                                                    .filter(element -> element.getKind()
                                                                                              .equals(ElementKind.FIELD))
                                                                    .filter(element -> element.getSimpleName()
                                                                                              .toString()
                                                                                              .equals(attribute))
                                                                    .findFirst();
      Element variableElement;
      if (variableElementOptional.isPresent()) {
        variableElement = variableElementOptional.get();
      } else {
        // todo message
        messager.printMessage(Diagnostic.Kind.ERROR,
                              String.format("%s applied on a type that doesn't implement %s; ignoring.",
                                            IsEditor.class.getCanonicalName(),
                                            Editor.class.getCanonicalName()));
        return null;
      }
      return (TypeElement) MoreTypes.asDeclared(variableElement.asType())
                                    .asElement();
    }
  }

  private EditorProcessingContext create() {
    // do some stuff with the consumer ...
    // element is an interface
    if (!types.isAssignable(element.asType(),
                            types.erasure(requireType(elements,
                                                      SimpleBeanEditorDriver.class).asType()))) {
      System.out.println(element.asType().toString());
      System.out.println(types.erasure(requireType(elements,
                                                   SimpleBeanEditorDriver.class).asType()));
      messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s applied on a type that doesn't implement %s; ignoring. hoss 01",
                                          IsEditor.class.getCanonicalName(),
                                          SimpleBeanEditorDriver.class.getCanonicalName()));
      return null;
    }
    // element is extending SimpleBeanEditorDriver
    if (!element.getKind()
                .equals(ElementKind.INTERFACE)) {
      messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s applied on a type that is not an interface; ignoring.",
                                          IsEditor.class.getCanonicalName()));
      return null;
    }
    // two generic parameters are set
    Optional<? extends TypeMirror> optional = types.directSupertypes(element.asType())
                                                     .stream()
                                                     .filter(superType -> types.isAssignable(superType,
                                                                                             types.erasure(requireType(elements,
                                                                                                                       SimpleBeanEditorDriver.class).asType())))
                                                     .findFirst();
    if (optional.isPresent()) {
      TypeMirror superType = optional.get();
      List<? extends TypeMirror> typeArguments = ((DeclaredType) superType).getTypeArguments();
      if (typeArguments.size() != 2) {
        messager.printMessage(Diagnostic.Kind.ERROR,
                              String.format("%s has no type arguments; ignoring.",
                                            SimpleBeanEditorDriver.class.getCanonicalName()));
        return null;
      }
      this.modelElement = (TypeElement) MoreTypes.asElement(typeArguments.get(0));
      this.editorElement = (TypeElement) MoreTypes.asElement(typeArguments.get(1));

      this.modelPackageName = MoreElements.getPackage(modelElement)
                                          .toString();
      this.modelSimpleName = modelElement.getSimpleName()
                                           .toString();

      this.editorPackageName = MoreElements.getPackage(editorElement)
                                           .toString();
      this.editorSimpleName = editorElement.getSimpleName()
                                           .toString();
    }

    this.consumerPackageName = MoreElements.getPackage(element)
                                           .toString();
    this.consumerSimpleName = element.getSimpleName()
                                     .toString();
    this.consumerSimpleImplName = element.getSimpleName()
                                         .toString() + EditorProcessingContext.IMPL_NAME;

    if (!types.isAssignable(editorElement.asType(),
                            types.erasure(requireType(elements,
                                                      Editor.class).asType()))) {
      messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s applied on a type that doesn't implement %s; ignoring.",
                                          editorElement.getQualifiedName().toString(),
                                          Editor.class.getCanonicalName()));
      return null;
    }

    ElementFilter.fieldsIn(editorElement.getEnclosedElements())
                 .stream()
                 .filter(variableElement -> isInstanceOfHasValue(variableElement))
                 .filter(variableElement -> variableElement.getAnnotation(Ignore.class) == null)
                 .forEach(variableElement -> editorModels.add(EditorModel.builder()
                                                                         .element(variableElement)
                                                                         .path(getPath(variableElement))
                                                                         .parent(editorElement)
                                                                         .modelElement(modelElement)
                                                                         .messenger(messager)
                                                                         .types(types)
                                                                         .build()));
    return this;
  }

  private TypeElement requireType(Elements elements,
                                  Class<?> clazz) {
    TypeElement type = elements.getTypeElement(clazz.getCanonicalName());
    if (type == null) {
      throw new AssertionError();
    }
    return type;
  }

  private boolean isInstanceOfHasValue(VariableElement variableElement) {
    return types.isAssignable(variableElement.asType(),
                              types.erasure(requireType(elements,
                                                        HasValue.class).asType()));
  }

  private String getPath(VariableElement variableElement) {
    Path pathAnotation = variableElement.getAnnotation(Path.class);
    if (pathAnotation != null) {
      return pathAnotation.value();
    } else {
      return variableElement.getSimpleName()
                            .toString();
    }
  }

  public static final class Builder {
    Messager messager;
    Filer    filer;
    Types    types;
    Elements elements;

    Element element;

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

    public Builder forElement(Element element) {
      this.element = element;
      return this;
    }

    public EditorProcessingContext build() {
      EditorProcessingContext instance = new EditorProcessingContext(this);
      return instance.create();
    }
  }
}
