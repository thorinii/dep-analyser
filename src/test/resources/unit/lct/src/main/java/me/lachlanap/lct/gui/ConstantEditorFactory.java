package me.lachlanap.lct.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import me.lachlanap.lct.ConstantException;
import me.lachlanap.lct.data.*;

/**
 *
 * @author lachlan
 */
public class ConstantEditorFactory {

    private final Map<String, Class<? extends ConstantEditor>> editors;

    public ConstantEditorFactory() {
        editors = new HashMap<>();

        addEditor(IntConstantField.class, IntEditor.class);
        addEditor(LongConstantField.class, LongEditor.class);
        addEditor(FloatConstantField.class, FloatEditor.class);
        addEditor(DoubleConstantField.class, DoubleEditor.class);
    }

    public void addEditor(Class<? extends ConstantField> constant, Class<? extends ConstantEditor> editor) {
        editors.put(constant.getCanonicalName(), editor);
    }

    public ConstantEditor createEditor(ConstantField constant) {
        String type = constant.getClass().getCanonicalName();
        if (editors.containsKey(type)) {
            Class<? extends ConstantEditor> klass = editors.get(type);

            try {
                Constructor<? extends ConstantEditor> constructor = klass.getConstructor(constant.getClass());
                return constructor.newInstance(constant);
            } catch (IllegalAccessException |
                    InstantiationException |
                    InvocationTargetException |
                    NoSuchMethodException e) {
                throw new ConstantException(
                        "Could not create editor for " + constant.name, e);
            }
        } else
            throw new ConstantException(
                    "No editor for " + constant.name);
    }
}
