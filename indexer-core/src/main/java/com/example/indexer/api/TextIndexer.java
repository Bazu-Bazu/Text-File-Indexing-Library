package com.example.indexer.api;

import java.nio.file.Path;
import java.util.Set;

public interface TextIndexer {

    void addFile(Path file);
    void addDirectory(Path dir);
    Set<Path> search(String word);
    void start();
    void stop();
}
