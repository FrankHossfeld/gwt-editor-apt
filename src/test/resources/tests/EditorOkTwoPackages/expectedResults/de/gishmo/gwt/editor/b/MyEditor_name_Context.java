package tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.impl.AbstractEditorContext;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Person;

public class MyEditor_name_Context extends AbstractEditorContext<String> {
  private final Person parent;

  public MyEditor_name_Context(Person parent, Editor<String> editor, String path) {
    super(editor, path);
    this.parent = parent;
  }

  @Override
  public boolean canSetInModel() {
    return parent != null;
  }

  @Override
  public String checkAssignment(Object value) {
    return (String) value;
  }

  @Override
  public Class getEditedType() {
    return String.class;
  }

  @Override
  public String getFromModel() {
    return (parent != null) ? parent.getName() : null;
  }

  @Override
  public void setInModel(String data) {
    parent.setName(data);
  }
}
