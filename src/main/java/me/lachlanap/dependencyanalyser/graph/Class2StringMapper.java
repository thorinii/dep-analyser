package me.lachlanap.dependencyanalyser.graph;

import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public interface Class2StringMapper {

    public String map(JavaClass klass);
}
