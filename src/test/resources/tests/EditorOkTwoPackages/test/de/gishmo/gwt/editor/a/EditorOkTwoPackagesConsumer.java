package tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.a;

import java.util.Date;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;

import de.gishmo.gwt.editor.client.annotation.IsEditor;

import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Address;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Person;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b.MyEditor;

public class EditorOkTwoPackagesConsumer {

  private Person person = new Person("Simpson",
                                     "Bart",
                                     new Date(),
                                     new Address("abs",
                                                 "xyz",
                                                 "Springfield"));

  private EditorOkTwoPackagesConsumerDriverEditorDriver editorDriver;

  public EditorOkTwoPackagesConsumer() {
    MyEditor editor = new MyEditor();

    editorDriver = new EditorOkTwoPackagesConsumerDriverEditorDriverImpl();
    editorDriver.initialize(editor);

    editorDriver.edit(person);

    person = editorDriver.flush();
  }

  @IsEditor
  interface EditorOkTwoPackagesConsumerDriverEditorDriver
    extends SimpleBeanEditorDriver<Person, MyEditor> {
  }
}
