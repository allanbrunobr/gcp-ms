package com.br.multicloudecore.gcpmodule.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;


@Getter
@Setter
public class LocationUpdate implements Serializable {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUpdate.class);
    private double latitude;
    private double longitude;


    public static LocationUpdate fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, LocationUpdate.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Falha ao parsing JSON para LocationUpdate: {}", json, e);            return null;
        }
    }

    public static TableSchema getSchema() {
        TableFieldSchema latitudeField = new TableFieldSchema().setName("latitude").setType("FLOAT");
        TableFieldSchema longitudeField = new TableFieldSchema().setName("longitude").setType("FLOAT");

        return new TableSchema().setFields(Arrays.asList(latitudeField, longitudeField));
    }
}