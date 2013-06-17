package me.lachlanap.lct.data;

import java.util.Properties;

/**
 *
 * @author lachlan
 */
public abstract class ConstantField {

    public final Class<?> container;
    public final String field;
    public final String name;

    public ConstantField(Class<?> container, String field, String name) {
        this.container = container;
        this.field = field;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        IntConstantField other = (IntConstantField) obj;
        if (this.container != other.container && (this.container == null || !this.container.equals(other.container)))
            return false;
        return this.field.equals(other.field) && this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.container != null ? this.container.hashCode() : 0);
        hash = 73 * hash + (this.field != null ? this.field.hashCode() : 0);
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Loads this constant's settings from the Properties. If they don't exist this will do nothing.
     */
    public abstract void loadFromProperties(Properties props);

    public abstract void saveToProperties(Properties props);

    @Override
    public String toString() {
        return "[" + name + " (" + container.getSimpleName() + "." + field + "]";
    }
}
