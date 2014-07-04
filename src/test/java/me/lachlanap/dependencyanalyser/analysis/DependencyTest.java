/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import static junit.framework.Assert.assertFalse;
import junit.framework.TestCase;

/**
 *
 * @author lachlan
 */
public class DependencyTest extends TestCase {

    public DependencyTest(String testName) {
        super(testName);
    }

    public void testIsStrongerThan() {
        Dependency d1 = new Dependency(DependencyType.Executable, null);
        Dependency d2 = new Dependency(DependencyType.Executable, null);

        assertFalse(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Static, null);
        assertFalse(d1.isStrongerThan(d2));
        assertTrue(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Genealogical, null);
        assertFalse(d1.isStrongerThan(d2));
        assertTrue(d2.isStrongerThan(d1));


        d1 = new Dependency(DependencyType.Static, null);
        d2 = new Dependency(DependencyType.Executable, null);

        assertTrue(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Static, null);
        assertFalse(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Genealogical, null);
        assertFalse(d1.isStrongerThan(d2));
        assertTrue(d2.isStrongerThan(d1));


        d1 = new Dependency(DependencyType.Genealogical, null);
        d2 = new Dependency(DependencyType.Executable, null);

        assertTrue(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Static, null);
        assertTrue(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));

        d2 = new Dependency(DependencyType.Genealogical, null);
        assertFalse(d1.isStrongerThan(d2));
        assertFalse(d2.isStrongerThan(d1));
    }
}
