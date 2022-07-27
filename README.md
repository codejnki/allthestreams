# All The Streams

A little demo app I threw together to test out some Hazelcast processing.

## Getting Started

`docker-compose up` will start a stack that contains `zookeeper`, `kafka`, and `hazelcast`.

Run the java app to submit the pipeline to the Hazelcast instance.

In another window run

```shell
docker exec --interactive --tty broker \
kafka-console-producer --bootstrap-server broker:9092 \
                       --topic demo-topic
```

In the command line start enterining any strings, and you should start seeing them emitted in the Hazelcast logs in all caps.