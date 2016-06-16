package com.google.gwt.editor.ui.client.adapters;

  import com.google.gwt.editor.client.Editor;
  import com.google.gwt.editor.client.EditorVisitor;
  import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
  import java.lang.Object;
  import java.lang.Override;
  import java.lang.String;

public class ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate extends SimpleBeanEditorDelegate {
  private ValueBoxEditor editor;

  private String object;

  @Override
  protected ValueBoxEditor getEditor() {
    return editor;
  }

  protected void setEditor(Editor editor) {
    this.editor = (ValueBoxEditor) editor;
  }

  @Override
  public String getObject() {
    return object;
  }

  @Override
  protected void setObject(Object object) {
    this.object = (String) object;
  }

  @Override
  protected void initializeSubDelegates() {
  }

  @Override
  public void accept(EditorVisitor visitor) {
  }
}
