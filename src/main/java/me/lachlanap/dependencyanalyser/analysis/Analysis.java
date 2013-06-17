/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class Analysis implements Iterable<ClassResult> {

    private final List<ClassResult> results;

    public Analysis() {
        results = new ArrayList<>();
    }

    public void add(ClassResult output) {
        results.add(output);
    }

    public List<ClassResult> getResults() {
        return results;
    }

    @Override
    public Iterator<ClassResult> iterator() {
        return results.iterator();
    }
}
