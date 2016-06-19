package tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Person;

public class MyEditor
  implements Editor<Person> {

  TextBox name;

  @Path("firstName")
  TextBox firstname;

  @Path("address.street")
  TextBox street;

  @Path("date")
  DateBox date;

  public MyEditor() {
  }
}
