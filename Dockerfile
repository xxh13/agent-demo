# Builder container
FROM registry.cn-hangzhou.aliyuncs.com/amn/basic-jdk AS builder

RUN export LD_LIBRARY_PATH=/usr/local/lib/ && protoc --version

COPY . /root/workspace/agent
WORKDIR /root/workspace/agent
RUN set -ex && mvn clean package

# Runner container
FROM registry.cn-hangzhou.aliyuncs.com/amn/runtime-image

COPY --from=builder /root/workspace/services/mesh-provider/target/mesh-provider-1.0-SNAPSHOT.jar /root/dists/mesh-provider.jar
COPY --from=builder /root/workspace/services/mesh-consumer/target/mesh-consumer-1.0-SNAPSHOT.jar /root/dists/mesh-consumer.jar
COPY --from=builder /root/workspace/agent/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar /root/dists/mesh-agent.jar

COPY --from=builder /usr/local/bin/docker-entrypoint.sh /usr/local/bin
COPY start-agent.sh /usr/local/bin

RUN set -ex \
 && chmod a+x /usr/local/bin/start-agent.sh \
 && mkdir -p /root/logs

RUN export LD_LIBRARY_PATH=/usr/local/lib/ && protoc --version \
    && ulimit -n 65535

EXPOSE 8087
EXPOSE 8088

ENTRYPOINT ["docker-entrypoint.sh"]
