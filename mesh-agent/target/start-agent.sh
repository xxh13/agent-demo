#!/bin/bash

ulimit -n 10240

ETCD_HOST=127.0.0.1
ETCD_PORT=2379
ETCD_URL=http://$ETCD_HOST:$ETCD_PORT

echo ETCD_URL = $ETCD_URL

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer agent..."
  java -jar \
       -Xms1536M \
       -Xmx1536M \
       -Dtype=consumer \
       -Dserver.port=20001 \
       -Dnetty.port=8088 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=logs \
       mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider agent..."
  java -jar \
       -Xms512M \
       -Xmx512M \
       -XX:+PrintGCDetails\
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dnetty.port=8088 \
       -Detcd.url=$ETCD_URL \
       -Dserver.weight=1 \
       -Dlogs.dir=logs \
       mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider agent..."
  java -jar \
       -Xms1536M \
       -Xmx1536M \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30001 \
       -Dnetty.port=8089 \
       -Detcd.url=$ETCD_URL \
       -Dserver.weight=3 \
       -Dlogs.dir=logs \
       mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider agent..."
  java -jar \
       -Xms2560M \
       -Xmx2560M \
       -XX:+PrintGCDetails\
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30002 \
       -Dnetty.port=8090 \
       -Dserver.weight=3 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=logs \
       mesh-agent-1.0-SNAPSHOT.jar
else
  echo "Unrecognized arguments, exit."
  exit 1
fi
