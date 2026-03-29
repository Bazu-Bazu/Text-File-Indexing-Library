package com.example.indexer.tokenizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TokenizerTest {

    @InjectMocks
    private TokenizerImpl tokenizer;

    @Test
    void shouldSplitTextIntoWords() {
        var result = tokenizer.tokenize("Hello, world! Java.");

        assertEquals(List.of("hello", "world", "java"), result);
    }
}
