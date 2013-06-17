/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.diagram;

import java.io.PrintStream;
import me.lachlanap.dependencyanalyser.analysis.Analysis;

/**
 *
 * @author lachlan
 */
public interface Diagram {

    public void generate(PrintStream ps, Analysis analysis);
}
