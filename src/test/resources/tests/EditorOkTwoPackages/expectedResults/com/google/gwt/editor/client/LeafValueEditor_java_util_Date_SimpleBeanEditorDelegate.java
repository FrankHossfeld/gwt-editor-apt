package com.google.gwt.editor.client;

import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
import java.lang.Object;
import java.lang.Override;
import java.util.Date;

public class LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate extends SimpleBeanEditorDelegate {
  private LeafValueEditor editor;

  private Date object;

  @Override
  protected LeafValueEditor getEditor() {
    return editor;
  }

  protected void setEditor(Editor editor) {
    this.editor = (LeafValueEditor) editor;
  }

  @Override
  public Date getObject() {
    return object;
  }

  @Override
  protected void setObject(Object object) {
    this.object = (Date) object;
  }

  @Override
  protected void initializeSubDelegates() {
  }

  @Override
  public void accept(EditorVisitor visitor) {
  }
}
