package com.example.indexer.index;

import java.nio.file.Path;
import java.util.Set;

public interface IndexStorage {

    void addWord(String word, Path file);
    void removeFile(Path file);
    Set<Path> search(String word);
}
