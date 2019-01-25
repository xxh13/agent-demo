#!/bin/bash

ETCD_HOST=etcd
ETCD_PORT=2379
ETCD_URL=http://$ETCD_HOST:$ETCD_PORT

echo ETCD_URL = $ETCD_URL

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer agent..."
  java -jar \
       -Xms3072M \
       -Xmx3072M \
       -XX:NewRatio=1\
       -XX:+PrintGCDetails\
       -Dtype=consumer \
       -Dserver.port=20001 \
       -Dnetty.port=8088 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider agent..."
  java -jar \
       -Xms1024M \
       -Xmx1024M \
       -XX:+PrintGCDetails\
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dnetty.port=8088 \
       -Detcd.url=$ETCD_URL \
       -Dserver.weight=2 \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider agent..."
  java -jar \
       -Xms2048M \
       -Xmx2048M \
       -XX:+PrintGCDetails\
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dnetty.port=8088 \
       -Detcd.url=$ETCD_URL \
       -Dserver.weight=3 \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider agent..."
  java -jar \
       -Xms3072M \
       -Xmx3072M \
       -XX:+PrintGCDetails\
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dnetty.port=8088 \
       -Dserver.weight=3 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
else
  echo "Unrecognized arguments, exit."
  exit 1
fi
