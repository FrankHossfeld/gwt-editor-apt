package de.gishmo.gwt.editor.processor;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;

@AutoService(Processor.class)
public class EditorProcessor
  extends BasicAnnotationProcessor {

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  protected Iterable<? extends ProcessingStep> initSteps() {
    return ImmutableList.of(EditorProcessingStep.builder()
                                                .withMessenger(processingEnv.getMessager())
                                                .withFiler(processingEnv.getFiler())
                                                .withTypes(processingEnv.getTypeUtils())
                                                .withElements(processingEnv.getElementUtils())
                                                .build());
  }
}
