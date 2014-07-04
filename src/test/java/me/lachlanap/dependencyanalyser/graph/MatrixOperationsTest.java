package me.lachlanap.dependencyanalyser.graph;

import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author lachlan
 */
public class MatrixOperationsTest extends TestCase {

    public void testPathMatrix() {
        MutableMatrix m = new MutableMatrix();
        m.put("A", "B");
        m.put("B", "C");

        MutableMatrix exp = new MutableMatrix();
        exp.put("A", "B");
        exp.put("A", "C");
        exp.put("B", "C");

        Matrix result = MatrixOperations.pathMatrix(m);

        assertEquals(exp, result);
    }

    public void testTransitiveReduction() {
        MutableMatrix m = new MutableMatrix();
        m.put("A", "B");
        m.put("A", "C");
        m.put("B", "C");

        MutableMatrix exp = new MutableMatrix();
        exp.put("A", "B");
        exp.put("B", "C");

        Matrix result = MatrixOperations.transitiveReduction(m);

        assertEquals(exp, result);
    }

    public void testTransitiveReduction2() {
        MutableMatrix m = new MutableMatrix();
        m.put("A", "B");
        m.put("A", "D");
        m.put("B", "C");
        m.put("C", "D");

        MutableMatrix exp = new MutableMatrix();
        exp.put("A", "B");
        exp.put("B", "C");
        exp.put("C", "D");

        Matrix result = MatrixOperations.transitiveReduction(m);

        assertEquals(exp, result);
    }

    public void testClass2PackageMatrix() {
        MutableMatrix m = new MutableMatrix();
        m.put("m1.a", "m2.b");
        m.put("m1.a", "m2.c");
        m.put("m2.b", "m2.c");

        MutableMatrix exp = new MutableMatrix();
        exp.put("m1", "m2");

        Matrix result = MatrixOperations.class2PackageMatrix(m);

        assertEquals(exp, result);
    }
}
