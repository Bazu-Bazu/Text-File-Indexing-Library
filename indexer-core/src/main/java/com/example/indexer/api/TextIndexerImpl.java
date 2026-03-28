package com.example.indexer.api;

import com.example.indexer.index.InMemoryIndexStorage;
import com.example.indexer.index.IndexStorage;
import com.example.indexer.service.FileReaderService;
import com.example.indexer.service.IndexService;
import com.example.indexer.service.SearchService;
import com.example.indexer.tokenizer.Tokenizer;
import com.example.indexer.tokenizer.TokenizerImpl;
import com.example.indexer.watcher.FileWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TextIndexerImpl implements TextIndexer {

    private final IndexService indexService;
    private final SearchService searchService;
    private final ExecutorService executor;
    private final FileWatcher fileWatcher;

    public TextIndexerImpl() {
        IndexStorage storage = new InMemoryIndexStorage();
        Tokenizer tokenizer = new TokenizerImpl();
        FileReaderService reader = new FileReaderService();

        this.indexService = new IndexService(reader, tokenizer, storage);
        this.searchService = new SearchService(storage);
        this.executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        this.fileWatcher = new FileWatcher(indexService);
    }

    @Override
    public boolean addFile(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return false;
        }

        executor.submit(() -> {
            try {
                indexService.reindexFile(file);
            } catch (Exception e) {
                System.err.println("Failed to index file: " + file + " " + e.getMessage());
            }
        });

        return true;
    }

    @Override
    public boolean addDirectory(Path dir) {
        if (dir == null || !Files.isDirectory(dir)) {
            return false;
        }

        try (var stream = Files.walk(dir)) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(this::addFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileWatcher.registerRecursive(dir);

        return true;
    }

    @Override
    public Set<Path> search(String word) {
        if (word == null || word.isBlank()) {
            return Set.of();
        }

        return searchService.search(word.toLowerCase());
    }

    @Override
    public void start() {
        executor.submit(fileWatcher);
    }

    @Override
    public void stop() {
        fileWatcher.stop();

        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
