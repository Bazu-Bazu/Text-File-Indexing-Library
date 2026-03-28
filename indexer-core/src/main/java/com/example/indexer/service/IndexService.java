package com.example.indexer.service;

import com.example.indexer.index.IndexStorage;
import com.example.indexer.tokenizer.Tokenizer;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class IndexService {

    private final FileReaderService readerService;
    private final Tokenizer tokenizer;
    private final IndexStorage storage;

    public IndexService(FileReaderService readerService, Tokenizer tokenizer, IndexStorage storage) {
        this.readerService = readerService;
        this.tokenizer = tokenizer;
        this.storage = storage;
    }

    public void indexFile(Path path) {
        String content = readerService.read(path);

        Set<String> uniqueWords = new HashSet<>(tokenizer.tokenize(content));

        for (String word : uniqueWords) {
            storage.addWord(word, path);
        }
    }

    public void removeFile(Path path) {
        storage.removeFile(path);
    }

    public void reindexFile(Path path) {
        removeFile(path);
        indexFile(path);
    }
}
