#!/bin/bash
############################################################
# Stop application
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

# init app
BIN_DIR="$(cd `dirname $0`; pwd)"
APP_DIR="`dirname $BIN_DIR`"
PID_FILE="$BIN_DIR/app.pid"
DEPLOY_ID_FILE="$BIN_DIR/deploy.id"

# check deploy id
if [ -f $DEPLOY_ID_FILE ]; then
  DEPLOY_ID="`cat $DEPLOY_ID_FILE`"
fi
echo "Using deploy id: $DEPLOY_ID"

if [ -z "$DEPLOY_ID" ]; then
  echo "Can't read deploy id from file: $DEPLOY_ID_FILE"
  exit 1
fi

PIDS="`ps -ef|grep java|grep deploy.id=$DEPLOY_ID|awk '{print $2}'`"

for PID in ${PIDS}
do
    kill -9 ${PID}
    if [[ $? -eq 0 ]]; then
        echo "Stop application success: pid=$PID"
    else
        echo "Stop application fail: pid=$PID"
        exit 2
    fi
done
rm -rf $PID_FILE
exit 0