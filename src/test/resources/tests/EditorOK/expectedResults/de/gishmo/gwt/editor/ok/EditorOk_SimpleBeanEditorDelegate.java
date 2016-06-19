package tests.EditorOK.test.de.gishmo.gwt.editor.ok;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate;
import com.google.gwt.editor.client.impl.SimpleBeanEditorDelegate;
import com.google.gwt.editor.ui.client.adapters.ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate;
import java.lang.Object;
import java.lang.Override;
import tests.EditorOK.test.de.gishmo.gwt.editor.Person;

public class EditorOk_SimpleBeanEditorDelegate extends SimpleBeanEditorDelegate {
  private EditorOk editor;

  private Person object;

  SimpleBeanEditorDelegate nameDelegate;

  SimpleBeanEditorDelegate firstNameDelegate;

  SimpleBeanEditorDelegate streetDelegate;

  SimpleBeanEditorDelegate dateDelegate;

  @Override
  protected EditorOk getEditor() {
    return editor;
  }

  @Override
  protected void setEditor(Editor editor) {
    this.editor = (EditorOk) editor;
  }

  @Override
  public Person getObject() {
    return object;
  }

  @Override
  protected void setObject(Object object) {
    this.object = (Person) object;
  }

  @Override
  protected void initializeSubDelegates() {
    if (editor.name.asEditor() != null) {
      nameDelegate = new ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate();
      addSubDelegate(nameDelegate, appendPath("name"), editor.name.asEditor());
    }
    if (editor.firstname.asEditor() != null) {
      firstNameDelegate = new ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate();
      addSubDelegate(firstNameDelegate, appendPath("firstName"), editor.firstname.asEditor());
    }
    if (editor.street.asEditor() != null) {
      streetDelegate = new ValueBoxEditor_java_lang_String_SimpleBeanEditorDelegate();
      addSubDelegate(streetDelegate, appendPath("street"), editor.street.asEditor());
    }
    if (editor.date.asEditor() != null) {
      dateDelegate = new LeafValueEditor_java_util_Date_SimpleBeanEditorDelegate();
      addSubDelegate(dateDelegate, appendPath("date"), editor.date.asEditor());
    }
  }

  @Override
  public void accept(EditorVisitor visitor) {
    if (nameDelegate != null) {
      EditorOk_name_Context ctx = new EditorOk_name_Context(getObject(), editor.name.asEditor(), appendPath("name"));
      ctx.setEditorDelegate(nameDelegate);
      ctx.traverse(visitor, nameDelegate);
    }
    if (firstNameDelegate != null) {
      EditorOk_firstName_Context ctx = new EditorOk_firstName_Context(getObject(), editor.firstname.asEditor(), appendPath("firstName"));
      ctx.setEditorDelegate(firstNameDelegate);
      ctx.traverse(visitor, firstNameDelegate);
    }
    if (streetDelegate != null) {
      EditorOk_address_street_Context ctx = new EditorOk_address_street_Context(getObject(), editor.street.asEditor(), appendPath("street"));
      ctx.setEditorDelegate(streetDelegate);
      ctx.traverse(visitor, streetDelegate);
    }
    if (dateDelegate != null) {
      EditorOk_date_Context ctx = new EditorOk_date_Context(getObject(), editor.date.asEditor(), appendPath("date"));
      ctx.setEditorDelegate(dateDelegate);
      ctx.traverse(visitor, dateDelegate);
    }
  }
}