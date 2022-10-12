package org.example.kafka;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import java.util.Properties;
import org.apache.kafka.common.serialization.StringDeserializer;

public class App 
{
    public static void main( String[] args )
    {
        var clientConfig = new ClientConfig();
        clientConfig.setClusterName("hello-world");
        var hz = HazelcastClient.newHazelcastClient(clientConfig);

        var props = new Properties();
        props.setProperty("bootstrap.servers", "broker:29092");
        props.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("auto.offset.reset", "earliest");

        var pipeline = Pipeline.create();
        pipeline.readFrom(KafkaSources.kafka(props, "demo-topic"))
            .withNativeTimestamps(0)
            .map(x -> x.toString().toUpperCase())
            .writeTo(Sinks.logger());

        hz.getJet()
            .newJob(pipeline, new JobConfig()
                .setName("demo-topic-capitalizor")
                .addClass(App.class));
    }
}
