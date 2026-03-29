package com.example.indexer.index;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InMemoryIndexStorageTest {

    @InjectMocks
    private InMemoryIndexStorage storage;

    @Test
    void shouldAddAndSearch() {
        Path file = Path.of("file.txt");

        storage.addWord("java", file);

        var result = storage.search("java");

        assertTrue(result.contains(file));
    }

    @Test
    void shouldRemoveFile() {
        Path file = Path.of("file.txt");

        storage.addWord("java", file);
        storage.removeFile(file);

        var result = storage.search("java");

        assertTrue(result.isEmpty());
    }
}
