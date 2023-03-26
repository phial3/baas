#!/bin/bash
############################################################
# Rollback
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

BIN_DIR="$(cd `dirname $0`; pwd)"
APP_DIR="`dirname $BIN_DIR`"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"

BACKUP_NAME="$1"

if [[ -z "$BACKUP_NAME" ]]; then
	echo "Please specify backup name: rollback.sh 20200101120001"
	exit 1
fi

BACKUP_DIR="$APP_DIR/BACKUP/$1"

if [ ! -d $BACKUP_DIR ]; then
  echo "Backup not found: $BACKUP_DIR"
  exit 1
fi

echo "Rolling back from: $BACKUP_DIR"

/bin/bash $BIN_DIR/stop.sh

rm -rf $APP_DIR/$APP_JAR_FILE
rm -rf $APP_DIR/unpack

cp $BACKUP_DIR/$APP_JAR_FILE $APP_DIR
if [ -d $BACKUP_DIR/unpack ]; then
    cp -R $BACKUP_DIR/unpack $APP_DIR
fi

/bin/bash $BIN_DIR/start.sh
echo "Rollback success: $BACKUP_DIR"

exit 0