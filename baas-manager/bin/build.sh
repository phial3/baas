#!/bin/bash
############################################################
# Build application from git and start app
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

# init app

# custom scp home
GIT_URL="https://coding.jd.com/platform-and-ecosystem-app/springboot-template-web.git"

PROFILE_NAME="@maven.profile.name@"
BIN_DIR="$(cd `dirname $0`; pwd)"
APP_DIR="`dirname $BIN_DIR`"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"
NEW_FILE="$SCP_HOME/$APP_JAR_FILE"

# check git and mvn
GIT_BIN="`which git`"
MVN_BIN="`which mvn`"

if [ -z "$GIT_BIN" ]; then
  echo "git is not installed"
  exit 1
fi

if [ -z "$MVN_BIN" ]; then
  echo "mvn is not installed"
  exit 1
fi

BUILD_DIR="$BIN_DIR/build"

if [ ! -d $BUILD_DIR ]; then
  mkdir "$BUILD_DIR"
fi

rm -rf $BUILD_DIR/*
$GIT_BIN clone $GIT_URL $BUILD_DIR

cd $BUILD_DIR
$MVN_BIN -f$BUILD_DIR/pom.xml package -P$PROFILE_NAME -Dmaven.test.skip=true

# deploy
NEW_FILE="$BUILD_DIR/target/$APP_JAR_FILE"
if [ ! -f $NEW_FILE ]; then
  echo "App jar file not build: $NEW_FILE"
  exit 1
fi

/bin/bash $BIN_DIR/backup.sh
rm -rf $APP_DIR/$APP_JAR_FILE/
rm -rf $APP_DIR/unpack
cp $NEW_FILE $APP_DIR
/bin/bash $BIN_DIR/stop.sh
cd $BIN_DIR
rm -rf $BUILD_DIR
sleep 2
/bin/bash $BIN_DIR/start.sh
exit 0