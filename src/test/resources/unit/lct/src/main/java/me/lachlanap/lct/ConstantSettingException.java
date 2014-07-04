package me.lachlanap.lct;

import me.lachlanap.lct.data.ConstantField;

/**
 *
 * @author lachlan
 */
public class ConstantSettingException extends ConstantException {

    public ConstantSettingException(ConstantField field) {
        super("Could not set field " + field.name + " of " + field.container.getCanonicalName());
    }

    public ConstantSettingException(ConstantField field, Throwable cause) {
        super("Could not set field " + field.name + " of " + field.container.getCanonicalName(), cause);
    }
}
