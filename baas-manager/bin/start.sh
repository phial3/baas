#!/bin/bash
############################################################
# Start application
# @author mayanjun
# @Email mayanjun@jd.com
############################################################

# init app
BIN_DIR="$(
  cd $(dirname $0)
  pwd
)"
APP_DIR="$(dirname $BIN_DIR)"
PROFILE_NAME="@maven.profile.name@"
APP_JAR_FILE="@maven.project.name@-@maven.project.version@.jar"
JAVA_BIN="@maven.profile.javabin@"
JVM_OPTS="@maven.profile.javaopts@"
PID_FILE="$BIN_DIR/app.pid"
JAR_BIN="$(dirname $JAVA_BIN)/jar"
DEPLOY_ID_FILE="$BIN_DIR/deploy.id"

echo "============================ APP START =============================="

# check deploy id
if [ ! -f $DEPLOY_ID_FILE ]; then
    DEPLOY_ID="`date "+%Y%m%d%H%M%S"`$RANDOM"
    echo $DEPLOY_ID > $DEPLOY_ID_FILE
    echo "Deploy ID file generated"
  else
    DEPLOY_ID="`cat $DEPLOY_ID_FILE`"
fi

echo "Using deploy id: $DEPLOY_ID"

if [ -z "$DEPLOY_ID" ]; then
  echo "Can't read deploy id from file: $DEPLOY_ID_FILE"
  exit 1
fi

if [ ! -f "$JAVA_BIN" ]; then
  echo "JDK not found: $JAVA_BIN"
  exit 1
fi

if [ -f "$APP_DIR/$APP_JAR_FILE" ]; then
  echo "Starting application using JVM_OPTS:$JVM_OPTS"
  if [ "$1" = "unpack" ]; then
    echo "Starting(unpack):$APP_DIR/unpack..."
    if [ ! -d "$APP_DIR/unpack" ]; then
      mkdir $APP_DIR/unpack
      cd $APP_DIR/unpack
      echo "Unpacking jar file: $APP_DIR/$APP_JAR_FILE"
      $JAR_BIN -xf $APP_DIR/$APP_JAR_FILE
    fi
    ${JAVA_BIN} -cp $APP_DIR/unpack/BOOT-INF/classes:$APP_DIR/unpack/BOOT-INF/lib/* ${JVM_OPTS} -Ddeploy.id=$DEPLOY_ID com.jd.eco.Application --spring.profiles.active=${PROFILE_NAME} > ${BIN_DIR}/stdio.out 2>&1 &
    PID=$!
  else
    echo "Starting(jar)..."
    ${JAVA_BIN} ${JVM_OPTS} -Ddeploy.id=$DEPLOY_ID -jar ${APP_DIR}/${APP_JAR_FILE} --spring.profiles.active=${PROFILE_NAME} > ${BIN_DIR}/stdio.out 2>&1 &
    PID=$!
  fi
  echo "Application started with PID $PID"
  echo "$PID" >${PID_FILE}
else
  echo "No app archive file found: ${APP_DIR}/${APP_JAR_FILE}"
  exit 1
fi

exit 0