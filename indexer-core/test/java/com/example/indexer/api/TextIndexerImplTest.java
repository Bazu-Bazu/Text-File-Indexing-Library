package com.example.indexer.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TextIndexerImplTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldIndexAndSearch() throws Exception {
        Path file = tempDir.resolve("a.txt");
        Files.writeString(file, "hello java");

        TextIndexer indexer = new TextIndexerImpl();

        indexer.start();
        indexer.addFile(file);

        Thread.sleep(500);

        var result = indexer.search("java");

        assertTrue(result.contains(file));

        indexer.stop();
    }

    @Test
    void shouldIndexDirectory() throws Exception {
        Path file1 = tempDir.resolve("a.txt");
        Path file2 = tempDir.resolve("b.txt");

        Files.writeString(file1, "hello");
        Files.writeString(file2, "java");

        TextIndexer indexer = new TextIndexerImpl();

        indexer.start();
        indexer.addDirectory(tempDir);

        Thread.sleep(500);

        assertTrue(indexer.search("hello").contains(file1));
        assertTrue(indexer.search("java").contains(file2));

        indexer.stop();
    }

    @Test
    void shouldReturnFalseIfFileNotExists() {
        TextIndexer indexer = new TextIndexerImpl();

        boolean result = indexer.addFile(Path.of("not_exist.txt"));

        assertFalse(result);
    }

    @Test
    void shouldReturnEmptyForUnknownWord() throws Exception {
        Path file = tempDir.resolve("a.txt");
        Files.writeString(file, "hello");

        TextIndexer indexer = new TextIndexerImpl();

        indexer.start();
        indexer.addFile(file);

        Thread.sleep(500);

        assertTrue(indexer.search("java").isEmpty());

        indexer.stop();
    }
}
