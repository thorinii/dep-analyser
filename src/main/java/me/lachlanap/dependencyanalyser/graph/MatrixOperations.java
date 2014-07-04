package me.lachlanap.dependencyanalyser.graph;

/**
 *
 * @author lachlan
 */
public class MatrixOperations {

    public static Matrix pathMatrix(Matrix adjacency) {
        MutableMatrix path = new MutableMatrix(adjacency);
        final int N = adjacency.size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == j)
                    continue;
                if (path.get(j, i))
                    for (int k = 0; k < N; k++)
                        if (!path.get(j, k))
                            path.set(j, k, path.get(i, k));

            }
        }

        return path;
    }

    public static Matrix transitiveReduction(Matrix adjacency) {
        MutableMatrix tr = (MutableMatrix) pathMatrix(adjacency);
        final int N = adjacency.size();

        for (int i = N - 1; i >= 0; i--) {
            for (int j = 0; j < N; j++) {
                if (tr.get(i, j)) {
                    for (int k = 0; k < N; k++) {
                        if (tr.get(j, k))
                            tr.unset(i, k);
                    }
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tr.get(i, j)) {
                    for (int k = 0; k < N; k++) {
                        if (tr.get(j, k))
                            tr.unset(i, k);
                    }
                }
            }
        }

        return tr;
    }

    public static Matrix class2PackageMatrix(Matrix klassMatrix) {
        MutableMatrix packageMatrix = new MutableMatrix();
        final int N = klassMatrix.size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                String from = klassMatrix.map(i);
                from = from.substring(0, from.lastIndexOf('.'));

                String to = klassMatrix.map(j);
                to = to.substring(0, to.lastIndexOf('.'));

                if (klassMatrix.get(i, j))
                    packageMatrix.put(from, to);
            }
        }

        return packageMatrix;
    }

    private MatrixOperations() {
    }
}
