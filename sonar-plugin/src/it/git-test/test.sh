#!/bin/sh

mkdir target
cd target
git clone http://github.com/talios/clojure-maven-plugin.git
cd clojure-maven-plugin
mvn clean install
mvn sonar:sonar -Dsonar.scm-activity.enabled=true
