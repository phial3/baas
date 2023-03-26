#!/bin/bash
############################################################
# Backup application
# Copy new jar file and backup jar file
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

readonly BIN_DIR="$(cd `dirname $0`; pwd)"
readonly APP_DIR="`dirname $BIN_DIR`"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"
BACKUP_DIR="$APP_DIR/BACKUP/`date "+%Y%m%d%H%M%S"`"

if [ -d $BACKUP_DIR ]; then
  sleep 1
  BACKUP_DIR="$APP_DIR/BACKUP/`date "+%Y%m%d%H%M%S"`"
fi

mkdir -p "$BACKUP_DIR"
echo "Backup directory created: $BACKUP_DIR"

cp $APP_DIR/$APP_JAR_FILE $BACKUP_DIR
if [ -d $APP_DIR/unpack ]; then
    cp -R $APP_DIR/unpack $BACKUP_DIR
fi

echo "Current app is backed up to dir: $BACKUP_DIR"

exit 0