#! /bin/sh

echo "inheritance"
cat diag/gen/package.dot | dot -Tpng -o diag/gen/package.png
cat diag/gen/package-transitive.dot | dot -Tpng -o diag/gen/package-transitive.png
cat diag/gen/class.dot | dot -Tpng -o diag/gen/class.png
cat diag/gen/class-transitive.dot | dot -Tpng -o diag/gen/class-transitive.png

echo "static"
cat diag/static/package.dot | dot -Tpng -o diag/static/package.png
cat diag/static/package-transitive.dot | dot -Tpng -o diag/static/package-transitive.png
cat diag/static/class.dot | dot -Tpng -o diag/static/class.png
cat diag/static/class-transitive.dot | dot -Tpng -o diag/static/class-transitive.png

echo "exec"
cat diag/exec/package.dot | dot -Tpng -o diag/exec/package.png
cat diag/exec/package-transitive.dot | dot -Tpng -o diag/exec/package-transitive.png
cat diag/exec/class.dot | dot -Tpng -o diag/exec/class.png
cat diag/exec/class-transitive.dot | dot -Tpng -o diag/exec/class-transitive.png

echo "Doing package"
#cat package.dot | dot -Tsvg -o package-1.svg


echo "Doing class - transitive"
#tred class.dot | dot -Tsvg -o class-2.svg

echo "Doing class"
#cat class.dot | dot -Tsvg -o class-1.svg

