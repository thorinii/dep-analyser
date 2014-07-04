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
public interface ProcessingFilter {

    boolean shouldProcess(JavaClass jc);
}
