package me.lachlanap.dependencyanalyser.graph;

import me.lachlanap.dependencyanalyser.analysis.DependencyType;

/**
 *
 * @author lachlan
 */
public class MatrixOperations {

    public static Matrix pathMatrix(Matrix adjacency) {
        Matrix path = new Matrix(adjacency);
        final int N = adjacency.size();

        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == j)
                        continue;
                    if (path.get(i, k) >= 0 && path.get(k, j) >= 0) {
                        int value = Math.min(path.get(i, k), path.get(k, j));

                        path.set(i, j, value);
                    }
                }
            }
        }

        return path;
    }

    public static Matrix transitiveReduction(Matrix adjacency) {
        Matrix tr = pathMatrix(adjacency);
        final int N = adjacency.size();

        System.out.println(tr);

        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == j)
                        continue;

                    if (i == 1)
                        System.out.println(adjacency.map(j) + " " + adjacency.map(k));
                    if (tr.get(i, j) >= 0) {
                        if (i == 1)
                            System.out.println(DependencyType.values()[tr.get(i, j)]);
                        if (tr.get(j, k) >= 0)
                            tr.unset(i, k);
                    }

                    if (i == 1)
                        System.out.println();
                }
            }
            //System.out.println(tr);
        }

        return tr;
    }

    public static Matrix class2PackageMatrix(Matrix klassMatrix) {
        Matrix packageMatrix = new Matrix();
        final int N = klassMatrix.size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                String from = klassMatrix.map(i);
                from = from.substring(0, from.lastIndexOf('.'));

                String to = klassMatrix.map(j);
                to = to.substring(0, to.lastIndexOf('.'));

                int value = klassMatrix.get(i, j);

                if (value >= 0)
                    packageMatrix.put(from, to, DependencyType.values()[klassMatrix.get(i, j)]);
            }
        }

        return packageMatrix;
    }

    private MatrixOperations() {
    }
}
