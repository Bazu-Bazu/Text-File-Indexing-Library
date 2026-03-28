package com.example.indexer.index;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryIndexStorage implements IndexStorage {

    private final ConcurrentMap<String, Set<Path>> wordToFiles = new ConcurrentHashMap<>();
    private final ConcurrentMap<Path, Set<String>> fileToWords = new ConcurrentHashMap<>();

    @Override
    public void addWord(String word, Path file) {
        wordToFiles.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet())
                .add(file);

        fileToWords.computeIfAbsent(file, k -> ConcurrentHashMap.newKeySet())
                .add(word);
    }

    @Override
    public void removeFile(Path file) {
        Set<String> words = fileToWords.remove(file);

        if (words == null) return;

        for (String word : words) {
            Set<Path> files = wordToFiles.get(word);

            if (files != null) {
                files.remove(file);

                if (files.isEmpty()) {
                    wordToFiles.remove(word, files);
                }
            }
        }
    }

    @Override
    public Set<Path> search(String word) {
        Set<Path> result = wordToFiles.get(word);
        return result != null ? Set.copyOf(result) : Set.of();
    }
}
