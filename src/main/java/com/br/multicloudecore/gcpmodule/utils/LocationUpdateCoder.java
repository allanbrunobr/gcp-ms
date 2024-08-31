package com.br.multicloudecore.gcpmodule.utils;

import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.util.common.ElementByteSizeObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocationUpdateCoder extends AtomicCoder<LocationUpdate> {
    private static final LocationUpdateCoder INSTANCE = new LocationUpdateCoder();

    private LocationUpdateCoder() {}

    public static LocationUpdateCoder of() {
        return INSTANCE;
    }

    @Override
    public void encode(LocationUpdate value, OutputStream outStream) throws IOException {
        if (value == null) {
            throw new CoderException("The LocationUpdateCoder cannot encode a null object!");
        }
        outStream.write(Double.toString(value.getLatitude()).getBytes());
        outStream.write(",".getBytes());
        outStream.write(Double.toString(value.getLongitude()).getBytes());
    }

    @Override
    public LocationUpdate decode(InputStream inStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inStream.read(buffer);
        String[] data = new String(buffer, 0, bytesRead).split(",");
        double latitude = Double.parseDouble(data[0]);
        double longitude = Double.parseDouble(data[1]);
        LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.setLatitude(latitude);
        locationUpdate.setLongitude(longitude);
        return locationUpdate;
    }

    @Override
    public void verifyDeterministic() throws NonDeterministicException {
        // This coder is deterministic
    }

    @Override
    public boolean isRegisterByteSizeObserverCheap(LocationUpdate value) {
        return true;
    }

    @Override
    public void registerByteSizeObserver(LocationUpdate value, ElementByteSizeObserver observer)
            throws Exception {
        observer.update(Double.BYTES + Double.BYTES);
    }
}
