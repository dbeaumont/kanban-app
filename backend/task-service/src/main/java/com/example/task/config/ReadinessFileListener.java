package com.example.task.config;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ReadinessFileListener implements ApplicationListener<AvailabilityChangeEvent<ReadinessState>> {
    private static final Path READY_FILE = Path.of("/tmp/ready");

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent<ReadinessState> event) {
        try {
            if (event.getState() == ReadinessState.ACCEPTING_TRAFFIC) {
                if (Files.notExists(READY_FILE)) {
                    Files.createFile(READY_FILE);
                }
            } else {
                Files.deleteIfExists(READY_FILE);
            }
        } catch (IOException ignored) {
            // Ignore: healthcheck will stay failing if file can't be written
        }
    }
}
