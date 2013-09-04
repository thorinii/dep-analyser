#! /bin/sh

echo "Doing package - transitive"
#tred package.dot | dot -Tsvg -o package-2.svg
tred package.dot | dot -Tpng -o package-2.png

echo "Doing package"
#cat package.dot | dot -Tsvg -o package-1.svg


echo "Doing class - transitive"
#tred class.dot | dot -Tsvg -o class-2.svg

echo "Doing class"
#cat class.dot | dot -Tsvg -o class-1.svg

