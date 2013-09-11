package me.lachlanap.dependencyanalyser.graph;

import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author lachlan
 */
public class SyntheticClass2StringMapper implements Class2StringMapper {

    @Override
    public String map(JavaClass klass) {
        String name = klass.getClassName();
        int index = name.indexOf('$');

        if (index < 0)
            return name;
        else
            return name.substring(0, index);
    }
}
