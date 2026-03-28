package com.example.indexer.tokenizer;

import java.util.Arrays;
import java.util.List;

public class TokenizerImpl implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(s -> !s.isBlank())
                .toList();
    }
}
