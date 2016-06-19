package de.gishmo.gwt.editor.processor;

import java.util.Optional;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.ClassName;

public class ModelUtils {

//  public static boolean containsMethod(TypeElement element,
//                                       String path,
//                                       String prefix) {
//    if (path.indexOf(".") == -1) {
//      return ElementFilter.methodsIn(element.getEnclosedElements())
//                          .stream()
//                          .filter(executebleElement -> executebleElement.getConsumerSimpleName()
//                                                                        .toString()
//                                                                        .equals(prefix + ModelUtils.capatilize(path)))
//                          .findFirst()
//                          .isPresent();
//    } else {
//      if (path.indexOf(".") == path.length()) {
//        return false;
//      }
//      Optional<? extends ExecutableElement> optional = ElementFilter.methodsIn(element.getEnclosedElements())
//                                                                    .stream()
//                                                                    .filter(executebleElement -> executebleElement.getConsumerSimpleName()
//                                                                                                                  .toString()
//                                                                                                                  .equals("get" + ModelUtils.capatilize(path.substring(0,
//                                                                                                                                                                       path.indexOf(".")))))
//                                                                    .findFirst();
//      if (optional.isPresent()) {
//        ExecutableElement executableElement = optional.get();
//        return ModelUtils.containsMethod((TypeElement) MoreTypes.asDeclared(optional.get()
//                                                                                    .getReturnType())
//                                                                .asElement(),
//                                         path.substring(path.indexOf(".") + 1),
//                                         prefix);
//      } else {
//        return false;
//      }
//    }
//  }

  public static ExecutableElement findGetMethod(TypeElement element,
                                                String path) {
    if (path.indexOf(".") == -1) {
      Optional<? extends ExecutableElement> optional = ElementFilter.methodsIn(element.getEnclosedElements())
                                                                    .stream()
                                                                    .filter(executebleElement -> executebleElement.getSimpleName()
                                                                                                                  .toString()
                                                                                                                  .equals("get" + ModelUtils.capatilize(path)))
                                                                    .findFirst();
      if (optional.isPresent()) {
        return optional.get();
      } else {
        return null;
      }
    } else {
      if (path.indexOf(".") == path.length()) {
        return null;
      }
      Optional<? extends ExecutableElement> optional = ElementFilter.methodsIn(element.getEnclosedElements())
                                                                    .stream()
                                                                    .filter(executebleElement -> executebleElement.getSimpleName()
                                                                                                                  .toString()
                                                                                                                  .equals("get" + ModelUtils.capatilize(path.substring(0,
                                                                                                                                                                       path.indexOf(".")))))
                                                                    .findFirst();
      if (optional.isPresent()) {
        ExecutableElement executableElement = optional.get();
        return ModelUtils.findGetMethod((TypeElement) MoreTypes.asDeclared(optional.get()
                                                                                   .getReturnType())
                                                               .asElement(),
                                        path.substring(path.indexOf(".") + 1));
      } else {
        return null;
      }
    }
  }

  public static ExecutableElement findSetMethod(TypeElement element,
                                                String path) {
    if (path.indexOf(".") == -1) {
      Optional<? extends ExecutableElement> optional = ElementFilter.methodsIn(element.getEnclosedElements())
                                                                    .stream()
                                                                    .filter(executebleElement -> executebleElement.getSimpleName()
                                                                                                                  .toString()
                                                                                                                  .equals("set" + ModelUtils.capatilize(path)))
                                                                    .findFirst();
      if (optional.isPresent()) {
        return optional.get();
      } else {
        return null;
      }
    } else {
      if (path.indexOf(".") == path.length()) {
        return null;
      }
      Optional<? extends ExecutableElement> optional = ElementFilter.methodsIn(element.getEnclosedElements())
                                                                    .stream()
                                                                    .filter(executebleElement -> executebleElement.getSimpleName()
                                                                                                                  .toString()
                                                                                                                  .equals("get" + ModelUtils.capatilize(path.substring(0,
                                                                                                                                                                       path.indexOf(".")))))
                                                                    .findFirst();
      if (optional.isPresent()) {
        ExecutableElement executableElement = optional.get();
        return ModelUtils.findGetMethod((TypeElement) MoreTypes.asDeclared(optional.get()
                                                                                   .getReturnType())
                                                               .asElement(),
                                        path.substring(path.indexOf(".") + 1));
      } else {
        return null;
      }
    }
  }

  public static TypeMirror getInterfaceType(Types types,
                                            TypeElement element,
                                            Class<?> clazz) {
    Optional<? extends TypeMirror> optinals = element.getInterfaces()
                                                     .stream()
                                                     .filter(interfaceType -> ClassName.get(types.erasure(interfaceType))
                                                                                       .toString()
                                                                                       .contains(clazz.getCanonicalName()))
                                                     .findFirst();
    if (optinals.isPresent()) {
      return optinals.get();
    } else {
      if (element.getSuperclass() == null) {
        return null;
      }
      return ModelUtils.getInterfaceType(types,
                                         (TypeElement) types.asElement(element.getSuperclass()),
                                         clazz);
    }
  }

  private static String capatilize(String value) {
    return value.substring(0,
                           1)
                .toUpperCase() + value.substring(1);
  }
}
