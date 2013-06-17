package me.lachlanap.lct.data;

import java.util.Properties;
import me.lachlanap.lct.ConstantSettingException;

/**
 *
 * @author lachlan
 */
public class LongConstantField extends ConstantField {

    public final long min, max;

    public LongConstantField(Class<?> container, String field, String name,
            String constraints) {
        super(container, field, name);

        long min, max;
        min = Long.MIN_VALUE;
        max = Long.MAX_VALUE;

        if (!constraints.isEmpty()) {
            int comma = constraints.indexOf(',');
            if (comma == -1)
                min = Long.parseLong(constraints);
            else if (comma == constraints.length() - 1)
                min = Long.parseLong(constraints.substring(0, constraints.length() - 1));
            else if (comma == 0)
                max = Long.parseLong(constraints.substring(1));
            else {
                String[] split = constraints.split(",");
                min = Long.parseLong(split[0]);
                max = Long.parseLong(split[1]);
            }
        }

        this.min = min;
        this.max = max;
    }

    public LongConstantField(
            Class<?> container, String field, String name, long min, long max) {
        super(container, field, name);
        this.min = min;
        this.max = max;
    }

    public long get() {
        try {
            return container.getField(field).getLong(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ConstantSettingException(this, e);
        }
    }

    public void set(long value) {
        try {
            container.getField(field).setLong(null, value);
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
            long value = Long.parseLong(strValue);
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
