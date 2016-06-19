package tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.a;

import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.impl.AbstractSimpleBeanEditorDriver;
import com.google.gwt.editor.client.impl.RootEditorContext;
import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
import java.lang.Override;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.Person;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b.MyEditor;
import tests.EditorOkTwoPackages.test.de.gishmo.gwt.editor.b.MyEditor_SimpleBeanEditorDelegate;

public class EditorOkTwoPackagesConsumerDriverEditorDriverImpl extends AbstractSimpleBeanEditorDriver<Person, MyEditor> implements EditorOkTwoPackagesConsumer.EditorOkTwoPackagesConsumerDriverEditorDriver {
  @Override
  public void accept(EditorVisitor visitor) {
    RootEditorContext ctx = new RootEditorContext(getDelegate(), Person.class, getObject());
    ctx.traverse(visitor, getDelegate());
  }

  @Override
  protected SimpleBeanEditorDelegate createDelegate() {
    return new MyEditor_SimpleBeanEditorDelegate();
  }
}
