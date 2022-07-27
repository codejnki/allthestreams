package org.example;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.SimpleEvent;
import com.hazelcast.jet.pipeline.test.TestSources;
import com.hazelcast.map.IMap;
import java.util.Map;
import java.util.Map.Entry;

public class App 
{
    public static void main( String[] args )
    {
        var clientConfig = new ClientConfig();
        clientConfig.setClusterName("hello-world");
        var hz = HazelcastClient.newHazelcastClient(clientConfig);
//        IMap<String, String> map = hz.getMap("my-distributed-map");
//
//        map.put("key", "value");
//        map.get("key");
//
//        map.putIfAbsent("somekey", "somevalue");
//        map.replace("key", "value", "newvalue");
//
//        hz.shutdown();

//        for (Entry<String, String> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }



//        hz.shutdown();

        // Pipeline Example
        var pipeline = Pipeline.create();
        pipeline.readFrom(TestSources.itemStream(10))
            .withoutTimestamps()
            .filter(event -> event.sequence() % 2 == 0)
            .setName("filter out odd numbers")
            .map(x -> multiplyMe(x))
            .writeTo(Sinks.logger());

        //ar hz = Hazelcast.bootstrappedInstance(clientConfig);
        hz.getJet().newJob(pipeline, new JobConfig().addClass(App.class));

    }

    private static Object multiplyMe(SimpleEvent x) {
        System.out.println("Hello world " + x.sequence());
        return x;
    }
}
