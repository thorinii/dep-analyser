package me.lachlanap.lct.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.lachlanap.lct.Constant;

/**
 *
 * @author lachlan
 */
public class ClassInspector {

    private final ConstantFieldFactory factory;

    public ClassInspector(ConstantFieldFactory factory) {
        this.factory = factory;
    }

    public List<ConstantField> getConstants(Class<?> aClass) {
        List<ConstantField> constants = new ArrayList<>();
        List<Field> fields = Arrays.asList(aClass.getFields());

        for (Field field : fields) {
            constants.add(processField(aClass, field));
        }

        Iterator<ConstantField> itr = constants.iterator();
        while (itr.hasNext()) {
            if (itr.next() == null)
                itr.remove();
        }

        return constants;
    }

    private ConstantField processField(Class<?> aClass, Field field) {
        Constant annot = field.getAnnotation(Constant.class);
        if (annot != null) {
            return factory.createConstantField(aClass, field.getName(), annot);
        }

        return null;
    }
}
