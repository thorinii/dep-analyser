package me.lachlanap.lct.spi.impl;

import me.lachlanap.lct.Constant;
import me.lachlanap.lct.data.ConstantField;
import me.lachlanap.lct.data.DoubleConstantField;
import me.lachlanap.lct.data.FloatConstantField;
import me.lachlanap.lct.data.IntConstantField;
import me.lachlanap.lct.data.LongConstantField;
import me.lachlanap.lct.spi.ConstantFieldProvider;

/**
 *
 * @author lachlan
 */
public class PrimitivesProvider implements ConstantFieldProvider {

    @Override
    public boolean canProvide(Class<?> type) {
        if (type == int.class)
            return true;
        if (type == long.class)
            return true;
        if (type == double.class)
            return true;
        if (type == float.class)
            return true;
        return false;
    }

    @Override
    public ConstantField getField(
            Class<?> type,
            Class<?> container, String field, Constant annot) {

        if (type == int.class)
            return new IntConstantField(container, field, annot.name(), annot.constraints());
        if (type == long.class)
            return new LongConstantField(container, field, annot.name(), annot.constraints());
        if (type == float.class)
            return new FloatConstantField(container, field, annot.name(), annot.constraints());
        if (type == double.class)
            return new DoubleConstantField(container, field, annot.name(), annot.constraints());
        throw new UnsupportedOperationException("Cannot create constant of type " + type.getSimpleName());
    }
}
