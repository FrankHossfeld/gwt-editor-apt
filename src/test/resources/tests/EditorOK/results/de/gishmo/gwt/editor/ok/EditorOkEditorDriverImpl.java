package de.gishmo.gwt.editor.ok;

import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.impl.AbstractSimpleBeanEditorDriver;
import com.google.gwt.editor.client.impl.RootEditorContext;
import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
import de.gishmo.gwt.editor.tests.Person;
import java.lang.Override;

public class EditorOkEditorDriverImpl extends AbstractSimpleBeanEditorDriver<Person, EditorOk> implements SimpleBeanEditorDriver<Person, EditorOk> {
  @Override
  public void accept(EditorVisitor visitor) {
    RootEditorContext ctx = new RootEditorContext(getDelegate(), Person.class, getObject());
    ctx.traverse(visitor, getDelegate());
  }

  @Override
  protected SimpleBeanEditorDelegate createDelegate() {
    return new EditorOk_SimpleBeanEditorDelegate();
  }
}
