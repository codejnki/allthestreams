package org.example.multiplesinks;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.function.PredicateEx;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.jet.pipeline.test.SimpleEvent;
import com.hazelcast.jet.pipeline.test.TestSources;

import java.util.Optional;

public class MultipleSinks {
    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.create();
        StreamStage<Optional<String>> stage = pipeline.readFrom(TestSources.itemStream(10))
                .withoutTimestamps()
                .map(getSimpleEventOptionalFunctionEx());

        Sink<Optional<String>> valueIsEmptySink = Sinks.logger(value -> "Value is empty");
        stage.filter(filterEmptyValues())
                .writeTo(valueIsEmptySink);

        Sink<Optional<String>> valueIsPresentLogger = Sinks.logger(value -> "Value is present " + value.get());
        stage.filter(filterPresentValues())
                .writeTo(valueIsPresentLogger);

        JobConfig cfg = new JobConfig().setName("multiple-sinks-example");
        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        hz.getJet().newJob(pipeline, cfg);
    }

    private static FunctionEx<SimpleEvent, Optional<String>> getSimpleEventOptionalFunctionEx() {
        return event -> {
            if (event.sequence() % 2 == 0) {
                return Optional.of(String.valueOf(event.sequence()));
            }
            return Optional.empty();
        };
    }

    private static PredicateEx<Optional<String>> filterPresentValues() {
        return Optional::isPresent;
    }

    private static PredicateEx<Optional<String>> filterEmptyValues() {
        return Optional::isEmpty;
    }
}
