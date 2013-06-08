/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class Task {

    private final JavaClass javaClass;

    public Task(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    public JavaClass getJavaClass() {
        return javaClass;
    }

    public String getID() {
        return javaClass.getClassName();
    }

    @Override
    public String toString() {
        return "[Task " + getID() + "]";
    }
}
