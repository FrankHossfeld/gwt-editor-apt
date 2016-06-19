/*
 * Copyright (C) 2016 Frank Hossfeld
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gishmo.gwt.editor.tests.ok;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.testing.compile.JavaFileObjects;

import de.gishmo.gwt.editor.processor.EditorProcessor;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class EditorOkTwoPackagesTest {

  @Test
  public void EditorOkTwoPackagesTest() {
    JavaFileObject ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate_Object = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/com/google/gwt/editor/ui/client/adapters/ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate.java");
    JavaFileObject LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate_Object  = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/com/google/gwt/editor/client/LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate.java");

    JavaFileObject MyEditor_address_street_Context_Object = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/b/MyEditor_address_street_Context.java");
    JavaFileObject MyEditor_date_Context_Object           = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/b/MyEditor_date_Context.java");
    JavaFileObject MyEditor_firstName_Context_Object      = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/b/MyEditor_firstName_Context.java");
    JavaFileObject MyEditor_name_Context_Object           = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/b/MyEditor_name_Context.java");

    JavaFileObject MyEditor_SimpleBeanEditorDelegate_Object = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/b/MyEditor_SimpleBeanEditorDelegate.java");

    JavaFileObject EditorOkTwoPackagesConsumerDriverEditorDriverImpl_Object = JavaFileObjects.forResource("tests/EditorOkTwoPackages/expectedResults/de/gishmo/gwt/editor/a/EditorOkTwoPackagesConsumerDriverEditorDriverImpl.java");

    ASSERT.about(javaSource())
          .that(JavaFileObjects.forResource("tests/EditorOkTwoPackages/test/de/gishmo/gwt/editor/a/EditorOkTwoPackagesConsumer.java"))
          .processedWith(new EditorProcessor())
          .compilesWithoutError()
          .and()
          .generatesSources(ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate_Object,
                            LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate_Object,
                            MyEditor_address_street_Context_Object,
                            MyEditor_date_Context_Object,
                            MyEditor_firstName_Context_Object,
                            MyEditor_name_Context_Object,
                            MyEditor_SimpleBeanEditorDelegate_Object,
                            EditorOkTwoPackagesConsumerDriverEditorDriverImpl_Object);
  }
}
