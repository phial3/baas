#!/bin/bash
############################################################
# Deploy app automatically from client terminal
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"

BIN_DIR="$(
  cd $(dirname $0)
  pwd
)"
APP_DIR="$(dirname $BIN_DIR)"

md5 $APP_DIR/$APP_JAR_FILE
echo "File size: `du -sh $APP_DIR/$APP_JAR_FILE`"
scp $APP_DIR/$APP_JAR_FILE root@tianwu.jdd-iot.com:~/

# remote deploy
ssh root@tianwu.jdd-iot.com "/export/App/tianwu/service/bin/deployjar.sh"

echo "Client deploy done"