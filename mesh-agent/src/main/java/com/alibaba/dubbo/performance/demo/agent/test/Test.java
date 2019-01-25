package com.alibaba.dubbo.performance.demo.agent.test;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;

import java.util.concurrent.CompletableFuture;

public class Test {

    public static void main(String[] args) throws Exception{
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();

        Lease lease = client.getLeaseClient();
        Long leaseId = lease.grant(30).get().getID();

        ByteSequence key1 = ByteSequence.fromString("test_key1");
        ByteSequence key2 = ByteSequence.fromString("test_key2");
        ByteSequence value1 = ByteSequence.fromString("test_value1");
        ByteSequence value2 = ByteSequence.fromString("test_value2");

        // put the key-value
        kvClient.put(key1, value1, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        kvClient.put(key2, value2, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        // get the CompletableFuture
//        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
//        // get the value from CompletableFuture
//        GetResponse response = getFuture.get();


    }


}
