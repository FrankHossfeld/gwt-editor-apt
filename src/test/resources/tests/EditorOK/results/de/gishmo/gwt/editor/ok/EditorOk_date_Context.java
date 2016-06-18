package tests.EditorOK.test.de.gishmo.gwt.editor.ok;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.impl.AbstractEditorContext;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Date;
import tests.EditorOK.test.de.gishmo.gwt.editor.Person;

public class EditorOk_date_Context extends AbstractEditorContext<Date> {
  private final Person parent;

  public EditorOk_date_Context(Person parent, Editor<Date> editor, String path) {
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
