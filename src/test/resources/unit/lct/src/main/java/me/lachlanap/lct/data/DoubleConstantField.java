package me.lachlanap.lct.data;

import java.util.Properties;
import me.lachlanap.lct.ConstantSettingException;

/**
 *
 * @author lachlan
 */
public class DoubleConstantField extends ConstantField {

    public final double min, max;

    public DoubleConstantField(Class<?> container, String field, String name,
            String constraints) {
        super(container, field, name);

        double min, max;
        min = Double.MIN_VALUE;
        max = Double.MAX_VALUE;

        if (!constraints.isEmpty()) {
            int comma = constraints.indexOf(',');
            if (comma == -1)
                min = Double.parseDouble(constraints);
            else if (comma == constraints.length() - 1)
                min = Double.parseDouble(constraints.substring(0, constraints.length() - 1));
            else if (comma == 0)
                max = Double.parseDouble(constraints.substring(1));
            else {
                String[] split = constraints.split(",");
                min = Double.parseDouble(split[0]);
                max = Double.parseDouble(split[1]);
            }
        }

        this.min = min;
        this.max = max;
    }

    public DoubleConstantField(
            Class<?> container, String field, String name, double min, double max) {
        super(container, field, name);
        this.min = min;
        this.max = max;
    }

    public double get() {
        try {
            return container.getField(field).getDouble(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new ConstantSettingException(this, e);
        }
    }

    public void set(double value) {
        try {
            container.getField(field).setDouble(null, value);
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
            double doubleValue = Double.parseDouble(strValue);
            set(doubleValue);
        } catch (NumberFormatException nfe) {
        }
    }

    @Override
    public void saveToProperties(Properties props) {
        String strValue = String.valueOf(get());
        props.setProperty(container.getSimpleName() + "." + name, strValue);
    }
}
