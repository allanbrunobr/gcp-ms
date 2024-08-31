package com.br.multicloudecore.gcpmodule.utils;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;

import java.util.ArrayList;
import java.util.List;

public class BigQuerySchemaUtil {

    private BigQuerySchemaUtil() {
    }

    public static Schema getSchema() {
        Field latitudeField = Field.of("latitude", StandardSQLTypeName.FLOAT64);
        Field longitudeField = Field.of("longitude", StandardSQLTypeName.FLOAT64);
        return Schema.of(latitudeField, longitudeField);
    }

    public static TableSchema toTableSchema(Schema schema) {
        List<TableFieldSchema> fields = new ArrayList<>();
        for (Field field : schema.getFields()) {
            fields.add(new TableFieldSchema().setName(field.getName()).setType(field.getType().toString()));
        }
        return new TableSchema().setFields(fields);
    }

}
