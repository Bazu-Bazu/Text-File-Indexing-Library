package com.example.indexer.service;

import com.example.indexer.index.IndexStorage;

import java.nio.file.Path;
import java.util.Set;

public class SearchService {

    private final IndexStorage storage;

    public SearchService(IndexStorage storage) {
        this.storage = storage;
    }

    public Set<Path> search(String word) {
        return storage.search(word.toLowerCase());
    }
}
