package com.cleanroom.common.telemetry;

import java.util.ArrayList;
import java.util.List;

public final class CleanRoomTelemetry {
    public record TelemetryRecord(long timestampEpochMs, String category, String message) {}

    private static final int BUFFER_CAPACITY = 1000;
    private static final List<TelemetryRecord> RING_BUFFER = new ArrayList<>(BUFFER_CAPACITY);

    private CleanRoomTelemetry() {}

    public static synchronized void record(String category, String message) {
        if (RING_BUFFER.size() >= BUFFER_CAPACITY) {
            RING_BUFFER.remove(0);
        }
        RING_BUFFER.add(new TelemetryRecord(System.currentTimeMillis(), category, message));
    }

    public static synchronized List<TelemetryRecord> getSnapshot() {
        return new ArrayList<>(RING_BUFFER);
    }

    public static synchronized void clear() {
        RING_BUFFER.clear();
    }
}
