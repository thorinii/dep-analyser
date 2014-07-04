/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.processing;

import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class NullProcessingFilter implements ProcessingFilter {

    @Override
    public boolean shouldProcess(JavaClass jc) {
        String name = jc.getClassName();

        if (name.startsWith("java") || name.startsWith("sun"))
            return false;
        else
            return true;
    }
}
