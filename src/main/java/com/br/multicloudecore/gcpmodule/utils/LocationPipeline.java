package com.br.multicloudecore.gcpmodule.utils;

import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;


public class LocationPipeline {
    public static void main(String[] args) {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(org.apache.beam.runners.direct.DirectRunner.class);  // Ensure you have the DirectRunner in your classpath
        Pipeline pipeline = Pipeline.create(options);

        // Register the custom coder
        CoderRegistry coderRegistry = pipeline.getCoderRegistry();
        coderRegistry.registerCoderForClass(LocationUpdate.class, LocationUpdateCoder.of());

        String projectId = "app-springboot-project";
        String topicId = "projects/" + projectId + "/topics/simulation-data-topic";
        String tableSpec = projectId + ":dataset.table-ecore";

        pipeline
                .apply("ReadMessages", PubsubIO.readStrings().fromTopic(topicId))
                .apply("ParseMessages", ParDo.of(new DoFn<String, LocationUpdate>() {
                    @ProcessElement
                    public void processElement(@Element String message, OutputReceiver<LocationUpdate> out) {
                        out.output(LocationUpdate.fromJson(message));
                    }
                }))
                .apply("ConvertToTableRow", ParDo.of(new DoFn<LocationUpdate, TableRow>() {
                    @ProcessElement
                    public void processElement(@Element LocationUpdate locationUpdate, OutputReceiver<TableRow> out) {
                        TableRow row = new TableRow()
                                .set("latitude", locationUpdate.getLatitude())
                                .set("longitude", locationUpdate.getLongitude());
                        out.output(row);
                    }
                })).apply("WriteToBigQuery", BigQueryIO.writeTableRows()
                        .to(tableSpec)
                        .withSchema(LocationUpdate.getSchema()) // Convert schema to string
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED));

        pipeline.run().waitUntilFinish();
    }
}