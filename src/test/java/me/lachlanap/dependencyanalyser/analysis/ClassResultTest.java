/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import junit.framework.TestCase;
import org.apache.bcel.classfile.JavaClass;
import static org.mockito.Mockito.*;

/**
 *
 * @author lachlan
 */
public class ClassResultTest extends TestCase {

    public ClassResultTest(String testName) {
        super(testName);
    }

    public void testStaticDependency() {
        ClassResult result = new ClassResult(null);
        JavaClass klass = mock(JavaClass.class);
        when(klass.getClassName()).thenReturn("org.mockito.Mock");

        
        Dependency d1 = new Dependency(Dependency.Type.Static, klass);
        result.addDependency(d1);

        Dependency d2 = new Dependency(Dependency.Type.Static, klass);
        result.addDependency(d2);

        assertEquals(1, result.getDependencies().size());
        assertTrue(result.getDependencies().get(0) == d1);
    }

    public void testGenealogicalDependency() {
        ClassResult result = new ClassResult(null);
        JavaClass klass = mock(JavaClass.class);
        when(klass.getClassName()).thenReturn("org.mockito.Mock");

        
        Dependency d1 = new Dependency(Dependency.Type.Genealogical, klass);
        result.addDependency(d1);

        Dependency d2 = new Dependency(Dependency.Type.Genealogical, klass);
        result.addDependency(d2);

        assertEquals(1, result.getDependencies().size());
        assertTrue(result.getDependencies().get(0) == d1);
    }
}
