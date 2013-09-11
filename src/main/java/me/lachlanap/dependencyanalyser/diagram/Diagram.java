/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.diagram;

import java.io.PrintStream;
import me.lachlanap.dependencyanalyser.graph.Matrix;

/**
 *
 * @author lachlan
 */
public interface Diagram {

    public void generate(PrintStream ps, Matrix matrix);

    void setShowExecutable(boolean showExecutable);

    void setShowGenealogical(boolean showGenealogical);

    void setShowStatic(boolean showStatic);
}
