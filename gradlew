#!/usr/bin/env bash

JAVA_HOME="C:/Program Files/Java/jdk8/jdk8u504-b01"
GRADLE_HOME="D:/git-projects/flexygames/gradle-3.5"
GRADLEGOPTS=""

export JAVA_HOME

exec "$JAVA_HOME/bin/java" $GRADLEGOPTS -cp "$GRADLE_HOME/lib/*" org.gradle.launcher.GradleMain "$@"
