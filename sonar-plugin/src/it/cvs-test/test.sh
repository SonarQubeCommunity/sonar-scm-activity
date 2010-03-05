#!/bin/sh

mkdir target
cd target
cvs -z3 -d:pserver:anonymous@javacaltools.cvs.sourceforge.net:/cvsroot/javacaltools co -P javacaltools
cp ../pom.xml javacaltools
cd javacaltools
mvn sonar:sonar
