package com.example.cli;

import com.example.indexer.api.TextIndexer;
import com.example.indexer.api.TextIndexerImpl;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        TextIndexer indexer = new TextIndexerImpl();
        indexer.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("TextIndexer CLI");
        System.out.println("Commands: addFile <path>, addDir <path>, search <word>, exit");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            String[] parts = line.trim().split("\\s+", 2);

            if (parts.length == 0) continue;

            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1].trim() : null;

            try {
                switch (cmd) {
                    case "addfile" -> {
                        if (arg != null) {
                            if (indexer.addFile(Path.of(arg))) {
                                System.out.println("File added to index: " + arg);
                            } else {
                                System.err.println("Fail: " + arg + " not found");
                            }
                        }
                    }
                    case "adddir" -> {
                        if (arg != null) {
                            if (indexer.addDirectory(Path.of(arg))) {
                                System.out.println("Directory added to index: " + arg);
                            } else {
                                System.err.println("Directory: " + arg + " not found");
                            }
                        }
                    }
                    case "search" -> {
                        if (arg != null) {
                            Set<Path> results = indexer.search(arg);
                            System.out.println("Found in files:");
                            if (results.isEmpty()) {
                                System.out.println("  (none)");
                            } else {
                                results.forEach(p -> System.out.println("  " + p));
                            }
                        }
                    }
                    case "exit" -> {
                        indexer.stop();
                        System.out.println("Exiting CLI...");
                        return;
                    }
                    default -> System.out.println("Unknown command: " + cmd);
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
