package com.example.indexer.service;

import com.example.indexer.index.InMemoryIndexStorage;
import com.example.indexer.index.IndexStorage;
import com.example.indexer.tokenizer.Tokenizer;
import com.example.indexer.tokenizer.TokenizerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class IndexServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldIndexFile() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "hello java");

        IndexStorage storage = new InMemoryIndexStorage();
        Tokenizer tokenizer = new TokenizerImpl();
        FileReaderService reader = new FileReaderService();

        IndexService service = new IndexService(reader, tokenizer, storage);

        service.indexFile(file);

        assertTrue(storage.search("hello").contains(file));
        assertTrue(storage.search("java").contains(file));
    }
}
