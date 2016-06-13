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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HasValue;

import de.gishmo.gwt.editor.client.annotation.IsEditor;

class EditorProcessingContext {

  private static final String IMPL_NAME = "EditorDriverImpl";

  private Messager messager;
  private Filer    filer;
  private Types    types;
  private Elements elements;
  private Element  element;

  private String            packageName;
  private String            simpleName;
  private String            simpleImplName;
  private TypeElement       modelElement;
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

  public String getPackageName() {
    return packageName;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getSimpleImplName() {
    return simpleImplName;
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
    // TODO ... woher wissen wir, dass das Feld genauiso heisst wie die Setter.Methode
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
    this.packageName = MoreElements.getPackage(element)
                                   .toString();
    this.simpleName = element.getSimpleName()
                             .toString();
    this.simpleImplName = element.getSimpleName()
                                 .toString() + EditorProcessingContext.IMPL_NAME;

    if (!types.isAssignable(element.asType(),
                            types.erasure(requireType(elements,
                                                      Editor.class).asType()))) {
      messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s applied on a type that doesn't implement %s; ignoring.",
                                          IsEditor.class.getCanonicalName(),
                                          Editor.class.getCanonicalName()));
      return null;
    }

    IsEditor annotation = element.getAnnotation(IsEditor.class);
    try {
      Class<?> modelClazz = annotation.value();
    } catch (MirroredTypeException e) {
      DeclaredType classTypeMirror = (DeclaredType) e.getTypeMirror();
      this.modelElement = (TypeElement) classTypeMirror.asElement();
    }

    ElementFilter.fieldsIn(element.getEnclosedElements())
                 .stream()
                 .filter(variableElement -> isInstanceOfHasValue(variableElement))
                 .filter(variableElement -> variableElement.getAnnotation(Ignore.class) == null)
                 .forEach(variableElement -> editorModels.add(EditorModel.builder()
                                                                         .element(variableElement)
                                                                         .path(getPath(variableElement))
                                                                         .parent((TypeElement) element)
                                                                         .modelElement(modelElement)
                                                                         .messenger(messager)
                                                                         .types(types)
                                                                         .build()));

//    this.variableElementList.stream()
//                            .forEach(type -> editorElementList.add((TypeElement) MoreTypes.asDeclared(type.asType())
//                                                                                          .asElement()));
//    this.editorElementList = this.editorElementList.parallelStream()
//                                                   .distinct()
//                                                   .collect(Collectors.toList());
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
