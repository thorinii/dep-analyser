package me.lachlanap.lct.spi;

import me.lachlanap.lct.Constant;
import me.lachlanap.lct.data.ConstantField;

/**
 *
 * @author lachlan
 */
public interface ConstantFieldProvider {

    public boolean canProvide(Class<?> type);

    public ConstantField getField(Class<?> type, Class<?> container, String field, Constant annot);
}
