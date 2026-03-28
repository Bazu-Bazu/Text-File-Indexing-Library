package com.example.indexer.api;

import java.nio.file.Path;
import java.util.Set;

public interface TextIndexer {

    boolean addFile(Path file);
    boolean addDirectory(Path dir);
    Set<Path> search(String word);
    void start();
    void stop();
}
