package de.gishmo.gwt.editor.ok;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.impl.AbstractEditorContext;
import de.gishmo.gwt.editor.tests.Person;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

public class EditorOk_name_Context extends AbstractEditorContext<String> {
  private final Person parent;

  public EditorOk_name_Context(Person parent, Editor<String> editor, String path) {
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
