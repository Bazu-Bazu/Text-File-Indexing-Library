package com.example.indexer.watcher;

import com.example.indexer.service.IndexService;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {

    private final WatchService watchService;
    private final IndexService indexService;

    private final Map<WatchKey, Path> keyToDir = new HashMap<>();

    private volatile boolean running = true;

    public FileWatcher(IndexService indexService) {
        this.indexService = indexService;

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to init WatchService", e);
        }
    }

    public void register(Path dir) {
        try {
            WatchKey key = dir.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY
            );
            keyToDir.put(key, dir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to register dir: " + dir, e);
        }
    }

    public void registerRecursive(Path start) {
        try {
            Files.walk(start)
                    .filter(Files::isDirectory)
                    .forEach(this::register);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (running) {
            WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            Path dir = keyToDir.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                Path relativePath = (Path) event.context();
                Path fullPath = dir.resolve(relativePath);

                handleEvent(kind, fullPath);
            }

            boolean valid = key.reset();
            if (!valid) {
                keyToDir.remove(key);
            }
        }
    }

    private void handleEvent(WatchEvent.Kind<?> kind, Path path) {

        if (Files.isDirectory(path)) {
            if (kind == ENTRY_CREATE) {
                registerRecursive(path);
            }
            return;
        }

        if (kind == ENTRY_CREATE) {
            indexService.indexFile(path);
        }

        if (kind == ENTRY_MODIFY) {
            indexService.reindexFile(path);
        }

        if (kind == ENTRY_DELETE) {
            indexService.removeFile(path);
        }
    }

    public void stop() {
        running = false;

        try {
            watchService.close();
        } catch (IOException ignored) {}
    }
}
