package me.lachlanap.lct.data;

import java.util.Properties;
import me.lachlanap.lct.ConstantSettingException;

/**
 *
 * @author lachlan
 */
public class FloatConstantField extends ConstantField {

    public final float min, max;

    public FloatConstantField(Class<?> container, String field, String name,
            String constraints) {
        super(container, field, name);

        float min, max;
        min = Float.MIN_VALUE;
        max = Float.MAX_VALUE;

        if (!constraints.isEmpty()) {
            int comma = constraints.indexOf(',');
            if (comma == -1)
                min = Float.parseFloat(constraints);
            else if (comma == constraints.length() - 1)
                min = Float.parseFloat(constraints.substring(0, constraints.length() - 1));
            else if (comma == 0)
                max = Float.parseFloat(constraints.substring(1));
            else {
                String[] split = constraints.split(",");
                min = Float.parseFloat(split[0]);
                max = Float.parseFloat(split[1]);
            }
        }

        this.min = min;
        this.max = max;
    }

    public FloatConstantField(
            Class<?> container, String field, String name, float min, float max) {
        super(container, field, name);
        this.min = min;
        this.max = max;
    }

    public float get() {
        try {
            return container.getField(field).getFloat(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ConstantSettingException(this, e);
        }
    }

    public void set(float value) {
        try {
            container.getField(field).setFloat(null, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ConstantSettingException(this, e);
        }
    }

    /**
     * Loads this constant's settings from the Properties. If they don't exist this will do nothing.
     */
    @Override
    public void loadFromProperties(Properties props) {
        String strValue = props.getProperty(container.getSimpleName() + "." + name);
        if (strValue == null)
            return;

        try {
            float value = Float.parseFloat(strValue);
            set(value);
        } catch (NumberFormatException nfe) {
        }
    }

    @Override
    public void saveToProperties(Properties props) {
        String strValue = String.valueOf(get());
        props.setProperty(container.getSimpleName() + "." + name, strValue);
    }
}
