package de.gishmo.gwt.editor.tests.ok;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.TextBox;

import de.gishmo.gwt.editor.client.annotation.IsEditor;
import de.gishmo.gwt.editor.tests.Address;
import de.gishmo.gwt.editor.tests.Person;

import java.util.Date;
import com.google.gwt.user.datepicker.client.DateBox;


@IsEditor(Person.class)
public class EditorOk
  implements Editor<Person> {

  TextBox name;

  @Path("firstName")
  TextBox firstname;

  @Path("address.street")
  TextBox street;

  @Path("date")
  DateBox date;

  private Person person = new Person("Simpson",
                                     "Bart",
                                     new Date(),
                                     new Address("abs",
                                                 "xyz",
                                                 "Springfield"));

  private SimpleBeanEditorDriver<Person, EditorOk> editorDriver;

  public EditorOk() {
    editorDriver = new EditorOkEditorDriverImpl();

    editorDriver.edit(person);

    person = editorDriver.flush();
  }
}
