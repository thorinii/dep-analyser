/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser.diagram;

import java.io.PrintStream;
import java.util.List;
import me.lachlanap.dependencyanalyser.analysis.DependencyType;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Executable;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Genealogical;
import static me.lachlanap.dependencyanalyser.analysis.DependencyType.Static;
import me.lachlanap.dependencyanalyser.graph.Matrix;

/**
 *
 * @author lachlan
 */
public class PackageDiagram implements Diagram {

    @Override
    public void generate(PrintStream ps, Matrix matrix) {
        ps.println("digraph {");
        //ps.println("  rankdir=LR;");

        doPackages(ps, matrix.getClasses());

        ps.println();
        ps.println();

        doConnections(ps, matrix);

        ps.println("}");
    }

    private void doPackages(PrintStream ps, List<String> packages) {
        for (String pack : packages) {
            ps.println("  "
                    + "\"" + pack + "\""
                    + "[shape=box];");
        }
    }

    private void doConnections(PrintStream ps, Matrix matrix) {
        for (String klass : matrix.getClasses()) {
            for (String dependency : matrix.getFor(klass)) {
                if (matrix.get(klass, dependency))
                    ps.println("  \"" + klass + "\" -> " + "\"" + dependency + "\""
                            + " [color=green,weight=1];");
            }

            ps.println();
        }
    }

    private String getStyle(String klass, DependencyType type) {
        switch (type) {
            case Genealogical:
                //if (klass.isInterface())
                //    return "style=bold,color=grey30,weight=4";
                //else
                return "style=bold,color=grey30,weight=4";
            case Static:
                return "color=green,weight=1";
            case Executable:
            default:
                return "color=red,weight=0,style=dashed";
        }
    }
}
