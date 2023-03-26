#!/bin/bash
############################################################
# Deploy application
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

# init app

# custom scp home
SCP_HOME="/Users/mayanjun/Desktop/dddddddddd/scp"

BIN_DIR="$(cd `dirname $0`; pwd)"
APP_DIR="`dirname $BIN_DIR`"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"
NEW_FILE="$SCP_HOME/$APP_JAR_FILE"

if [ ! -f $NEW_FILE ]; then
  echo "App jar file not found: $NEW_FILE"
  exit 1
fi

/bin/bash $BIN_DIR/backup.sh

rm -rf $APP_DIR/$APP_JAR_FILE
rm -rf $APP_DIR/unpack

cp $NEW_FILE $APP_DIR

/bin/bash $BIN_DIR/stop.sh
sleep 2
/bin/bash $BIN_DIR/start.sh

exit 0