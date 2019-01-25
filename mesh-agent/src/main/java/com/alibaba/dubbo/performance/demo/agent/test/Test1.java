package com.alibaba.dubbo.performance.demo.agent.test;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Test1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) throws Exception{

        Client client = Client.builder().endpoints("http://localhost:2379").build();

        ByteSequence key = ByteSequence.fromString("test_key");

        Watch.Watcher watcher = client.getWatchClient().watch(key, WatchOption.newBuilder().withPrefix(key).build());

        for (int i = 0; i < 6; i++) {
            WatchResponse watchResponse = watcher.listen();
            for (WatchEvent event : watchResponse.getEvents()) {
                LOGGER.info("type={}, key={}, value={}",
                        event.getEventType(),
                        Optional.ofNullable(event.getKeyValue().getKey())
                                .map(ByteSequence::toStringUtf8)
                                .orElse(""),
                        Optional.ofNullable(event.getKeyValue().getValue())
                                .map(ByteSequence::toStringUtf8)
                                .orElse("")
                );
            }
        }

        GetResponse getResponse = client.getKVClient().get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        for (com.coreos.jetcd.data.KeyValue kv : getResponse.getKvs()){
            System.out.println(kv.getKey().toStringUtf8());
            System.out.println(kv.getValue().toStringUtf8());
        }
    }
}
