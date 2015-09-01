# (c) Copyright 2015 Cloudera, Inc.
#!/bin/bash

set -e

usage() {
  cat << __EOF
Usage: $(basename "${BASH_SOURCE}")
    [ ./startClient.sh [-h SERVER_HOST] -u CLIENT_ID -d HOME_DIR]
    -h | SERVER_HOST
    -u | CLIENT_ID
    -d | HOME_DIR
__EOF
}

SCRIPT_DIRECTORY=$(dirname "${BASH_SOURCE}")
POM=${SCRIPT_DIRECTORY}/DropboxClient/pom.xml

SERVER_HOST=
CLIENT_ID=
HOME_DIR=
while getopts ":h:u:d:" o; do
  case "${o}" in
    h)
      SERVER_HOST=${OPTARG}
      ;;
    u)
      CLIENT_ID=${OPTARG}
      ;;
    d)
      HOME_DIR=${OPTARG}
      ;;
    *)
      usage 1>&2
      exit 1
  esac
done

if [ -z "$SERVER_HOST" ]; then
  SERVER_HOST="localhost"
fi

if [ -z "$CLIENT_ID" ]; then
  usage 1>&2
  exit 1
fi

if [ -z "$HOME_DIR" ]; then
  usage 1>&2
  exit 1
fi

mvn -f "$POM" exec:java -Dexec.mainClass="com.dropbox.client.DropboxClient" \
-Dserver="$SERVER_HOST" -Dclient.id="$CLIENT_ID" -Dclient.home="$HOME_DIR"
