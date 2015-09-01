# (c) Copyright 2015 Cloudera, Inc.
#!/bin/bash

set -e

SCRIPT_DIRECTORY=$(dirname "${BASH_SOURCE}")
POM=${SCRIPT_DIRECTORY}/DropboxServer/pom.xml

mvn -f "$POM" exec:java -Dexec.mainClass="com.dropbox.server.DropboxServer"
