/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import java.util.Objects;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class Dependency {

    public enum Type {

        Genealogical, Static, Executable
    }
    private final Type type;
    private final JavaClass javaClass;

    public Dependency(Type type, JavaClass javaClass) {
        this.type = type;
        this.javaClass = javaClass;
    }

    public Type getType() {
        return type;
    }

    public JavaClass getJavaClass() {
        return javaClass;
    }

    public String getKey() {
        return javaClass.getClassName();
    }

    public boolean isStrongerThan(Dependency dep) {
        switch (dep.type) {
            case Genealogical:
                return false;
            case Static:
                return type == Type.Genealogical;
            case Executable:
            default:
                return type != Type.Executable;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.javaClass.getClassName());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Dependency other = (Dependency) obj;
        if (!Objects.equals(this.javaClass.getClassName(), other.javaClass.getClassName()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        switch (type) {
            case Genealogical:
                return "=> " + javaClass.getClassName();
            case Static:
                return "+> " + javaClass.getClassName();
            case Executable:
            default:
                return "-> " + javaClass.getClassName();
        }
    }
}
