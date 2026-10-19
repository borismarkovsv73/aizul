package com.ftn.sbnz.service.tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects scenario results and renders them as markdown tables.
 * Every "obtained" value is passed in by the test after running the rules.
 */
class LongTermGoalsReport {

    private static final String HEADER =
        "| ID | Input | Expected | Obtained | Result |\n|---|---|---|---|---|\n";

    private final Map<String, List<String>> tables = new LinkedHashMap<>();
    private final List<String> sections = new ArrayList<>();
    private int passed;
    private int total;

    /** Tables appear in the report in the order given here. */
    LongTermGoalsReport(String... tableOrder) {
        for (String table : tableOrder) {
            tables.put(table, new ArrayList<>());
        }
    }

    void addRow(String table, String id, String input, String expected, String obtained, boolean pass) {
        String row = "| " + id + " | " + input + " | " + expected + " | " + obtained + " | "
            + (pass ? "PASS" : "FAIL") + " |\n";
        tables.computeIfAbsent(table, key -> new ArrayList<>()).add(row);
        total++;
        if (pass) {
            passed++;
        }
    }

    void addSection(String markdown) {
        sections.add(markdown);
    }

    String toMarkdown() {
        StringBuilder sb = new StringBuilder("# Long-term goal rules - test report\n\n");
        sb.append("Checks passed: ").append(passed).append(" / ").append(total).append("\n\n");
        for (Map.Entry<String, List<String>> table : tables.entrySet()) {
            sb.append("## ").append(table.getKey()).append("\n\n").append(HEADER);
            table.getValue().forEach(sb::append);
            sb.append("\n");
        }
        sections.forEach(section -> sb.append(section).append("\n"));
        return sb.toString();
    }

    void write(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, toMarkdown().getBytes(StandardCharsets.UTF_8));
    }
}
