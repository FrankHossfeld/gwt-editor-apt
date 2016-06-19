package tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.impl.AbstractEditorContext;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Date;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Person;

public class MyEditor_date_Context extends AbstractEditorContext<Date> {
  private final Person parent;

  public MyEditor_date_Context(Person parent, Editor<Date> editor, String path) {
    super(editor, path);
    this.parent = parent;
  }

  @Override
  public boolean canSetInModel() {
    return parent != null;
  }

  @Override
  public Date checkAssignment(Object value) {
    return (Date) value;
  }

  @Override
  public Class getEditedType() {
    return Date.class;
  }

  @Override
  public Date getFromModel() {
    return (parent != null) ? parent.getDate() : null;
  }

  @Override
  public void setInModel(Date data) {
    parent.setDate(data);
  }
}
