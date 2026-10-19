package com.ftn.sbnz.service.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.dto.MoveDTO;
import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Factory;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.models.Tile;
import com.ftn.sbnz.model.utils.BoardUtils;
import com.ftn.sbnz.service.SampleAppService;

/**
 * Verifies the three long-term goals (wall row, wall column, colour set) that are
 * implemented as recursive queries in rules/backward, plus one end-to-end scenario.
 * Writes target/reports/long-term-goals-report.md.
 */
class LongTermGoalsTest {

    private static final String ROW_GOAL = "Row goal";
    private static final String COLUMN_GOAL = "Column goal";
    private static final String COLOR_GOAL = "Colour goal";
    private static final String END_TO_END = "End-to-end scenario C1";

    // Every goal scenario targets pattern line 3 (index 2) with a blue tile
    private static final String COLOR = "blue";
    private static final int TARGET_ROW = 2;
    private static final int TARGET_COLUMN = BoardUtils.getWallColumnForColor(TARGET_ROW, COLOR);
    private static final String[] COLORS = {"blue", "yellow", "red", "black", "white"};

    private static final Path REPORT_PATH = Paths.get("target", "reports", "long-term-goals-report.md");
    private static final LongTermGoalsReport report =
        new LongTermGoalsReport(ROW_GOAL, COLUMN_GOAL, COLOR_GOAL, END_TO_END);

    private static KieContainer container;
    private static long nextTileId = 1;

    @BeforeAll
    static void loadRules() {
        KieServices services = KieServices.Factory.get();
        container = services.newKieContainer(
            services.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));
    }

    @AfterAll
    static void writeReport() throws IOException {
        System.out.println(report.toMarkdown());
        report.write(REPORT_PATH);
    }

    // ---------- Goal scenarios ----------

    static Stream<GoalScenario> rowGoalScenarios() {
        return Stream.of(
            new GoalScenario("R1", ROW_GOAL, fillWallRow(startedLine(2), 4), takeOneTile(TARGET_ROW), 20, rowReason(1)),
            new GoalScenario("R2", ROW_GOAL, fillWallRow(startedLine(2), 3), takeOneTile(TARGET_ROW), 15, rowReason(2)),
            new GoalScenario("R3", ROW_GOAL, fillWallRow(startedLine(2), 2), takeOneTile(TARGET_ROW), 10, rowReason(3)),
            new GoalScenario("R4", ROW_GOAL, fillWallRow(startedLine(2), 1), takeOneTile(TARGET_ROW), 0, null),
            new GoalScenario("R5", ROW_GOAL, fillWallRow(startedLine(0), 4), takeOneTile(TARGET_ROW), 0, null),
            new GoalScenario("R6", ROW_GOAL, fillWallRow(startedLine(2), 4), takeOneTile(-1), 0, null));
    }

    static Stream<GoalScenario> columnGoalScenarios() {
        return Stream.of(
            new GoalScenario("K1", COLUMN_GOAL, fillWallColumn(startedLine(2), 4), takeOneTile(TARGET_ROW), 25, columnReason(1)),
            new GoalScenario("K2", COLUMN_GOAL, fillWallColumn(startedLine(2), 3), takeOneTile(TARGET_ROW), 20, columnReason(2)),
            new GoalScenario("K3", COLUMN_GOAL, fillWallColumn(startedLine(2), 2), takeOneTile(TARGET_ROW), 15, columnReason(3)),
            new GoalScenario("K4", COLUMN_GOAL, fillWallColumn(startedLine(2), 1), takeOneTile(TARGET_ROW), 0, null),
            new GoalScenario("K5", COLUMN_GOAL, fillWallColumn(startedLine(0), 4), takeOneTile(TARGET_ROW), 0, null));
    }

    static Stream<GoalScenario> colorGoalScenarios() {
        return Stream.of(
            new GoalScenario("B1", COLOR_GOAL, placeColorOnWall(startedLine(2), 4), takeOneTile(TARGET_ROW), 25, colorReason(1)),
            new GoalScenario("B2", COLOR_GOAL, placeColorOnWall(startedLine(2), 3), takeOneTile(TARGET_ROW), 20, colorReason(2)),
            new GoalScenario("B3", COLOR_GOAL, placeColorOnWall(startedLine(2), 2), takeOneTile(TARGET_ROW), 15, colorReason(3)),
            new GoalScenario("B4", COLOR_GOAL, placeColorOnWall(startedLine(2), 1), takeOneTile(TARGET_ROW), 10, colorReason(4)),
            new GoalScenario("B5", COLOR_GOAL, placeColorOnWall(startedLine(2), 0), takeOneTile(TARGET_ROW), 0, null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource({"rowGoalScenarios", "columnGoalScenarios", "colorGoalScenarios"})
    void longTermGoalAddsOnlyItsOwnBonus(GoalScenario scenario) {
        String input = describeInput(scenario.board, scenario.move);
        Move move = runBackwardSession(scenario.board, scenario.move);
        List<String> reasons = reasonsOf(move);

        boolean pass = move.getScore() == scenario.expectedScore
            && reasons.equals(scenario.expectedReasons());
        report.addRow(scenario.goal, scenario.id, input,
            summarize(scenario.expectedScore, scenario.expectedReasons()),
            summarize(move.getScore(), reasons), pass);

        assertEquals(scenario.expectedScore, move.getScore(), scenario.id + " score");
        assertEquals(scenario.expectedReasons(), reasons, scenario.id + " reasons");
    }

    // ---------- End-to-end scenario ----------

    @Test
    void endToEndRecommendationCombinesAllRuleLayers() throws Exception {
        SampleAppService service = new SampleAppService(container);
        List<Move> ranked = service.rankMoves(endToEndState());
        MoveDTO recommended = service.getBestMove(endToEndState());

        Move best = ranked.get(0);
        String expectedMove = "Take 3 red tiles from factory 1 and place on row 5.";
        List<String> expectedReasons = Arrays.asList(
            "You place tiles on pattern line 5; longer pattern lines have greater heuristic value.",
            "You complete pattern line 5 with this move.",
            "You continue a pattern line you have already started.",
            "Including this wall-tiling phase, wall column 2 is 1 placements from completion.");
        List<String> bestReasons = reasonsOf(best);

        String input = "2 players, F1-F5 + centre (first-player token only), lines 1/3/5 started, wall column 2 holds 4 tiles";
        addEndToEndRow("C1 move", input, expectedMove, recommended.getDescription());
        input = "same state";
        addEndToEndRow("C1 score", input, "48", String.valueOf(best.getScore()));
        addEndToEndRow("C1 reasons", input, String.join("<br>", expectedReasons), String.join("<br>", bestReasons),
            new HashSet<>(expectedReasons).equals(new HashSet<>(bestReasons)) && bestReasons.size() == 4);
        addEndToEndRow("C1 candidates", input, "41", String.valueOf(ranked.size()));
        addEndToEndRow("C1 second-best score", input, "27", String.valueOf(ranked.get(1).getScore()));
        report.addSection(topMovesTable(ranked, 5));

        assertEquals(expectedMove, recommended.getDescription());
        assertEquals(48, best.getScore());
        assertEquals(new HashSet<>(expectedReasons), new HashSet<>(bestReasons));
        assertEquals(4, bestReasons.size());
        assertEquals(41, ranked.size());
        assertEquals(27, ranked.get(1).getScore());
    }

    private static GameState endToEndState() {
        Board board = emptyBoard();
        board.getRows().get(0).set(0, tile("blue"));
        board.getRows().get(2).set(0, tile("blue"));
        board.getRows().get(2).set(1, tile("blue"));
        board.getRows().get(4).set(0, tile("red"));
        board.getRows().get(4).set(1, tile("red"));
        board.getWall()[0][1] = tile("yellow");
        board.getWall()[1][1] = tile("blue");
        board.getWall()[2][1] = tile("white");
        board.getWall()[3][1] = tile("black");

        List<Player> players = Arrays.asList(
            new Player(1L, board, 0),
            new Player(2L, emptyBoard(), 0));
        List<Factory> factories = Arrays.asList(
            factory(1L, "red", "red", "red", "blue"),
            factory(2L, "white", "white", "white", "yellow"),
            factory(3L, "black", "blue", "red", "red"),
            factory(4L, "blue", "yellow", "yellow", "red"),
            factory(5L, "white", "white", "black", "red"),
            new Factory(-1L, tiles("lime"), true));
        return new GameState(1L, players, factories, new ArrayList<>(), 1);
    }

    // ---------- Running the rules ----------

    private static Move runBackwardSession(Board board, Move move) {
        KieSession session = container.newKieSession("combinedBackwardSession");
        session.insert(board);
        session.insert(move);
        session.fireAllRules();
        session.dispose();
        return move;
    }

    private static List<String> reasonsOf(Move move) {
        List<String> reasons = new ArrayList<>();
        for (Map<Boolean, String> rule : move.getAppliedRules()) {
            reasons.addAll(rule.values());
        }
        return reasons;
    }

    // ---------- Expected messages, exactly as the DRL files emit them ----------

    private static String rowReason(int depth) {
        return "Including this wall-tiling phase, wall row " + (TARGET_ROW + 1) + " is " + depth
            + " placements from completion.";
    }

    private static String columnReason(int depth) {
        return "Including this wall-tiling phase, wall column " + (TARGET_COLUMN + 1) + " is " + depth
            + " placements from completion.";
    }

    private static String colorReason(int depth) {
        return "Including this wall-tiling phase, this color set is " + depth + " placements from completion.";
    }

    // ---------- Board builders ----------

    /** Empty board whose pattern line 3 already holds the given number of blue tiles. */
    private static Board startedLine(int tilesInLine) {
        Board board = emptyBoard();
        for (int i = 0; i < tilesInLine; i++) {
            board.getRows().get(TARGET_ROW).set(i, tile(COLOR));
        }
        return board;
    }

    /** Fills cells of the target wall row, never the target column. */
    private static Board fillWallRow(Board board, int filled) {
        int placed = 0;
        for (int column = 0; column < 5 && placed < filled; column++) {
            if (column != TARGET_COLUMN) {
                placeOnWall(board, TARGET_ROW, column);
                placed++;
            }
        }
        return board;
    }

    /** Fills cells of the target wall column, never the target row. */
    private static Board fillWallColumn(Board board, int filled) {
        int placed = 0;
        for (int row = 0; row < 5 && placed < filled; row++) {
            if (row != TARGET_ROW) {
                placeOnWall(board, row, TARGET_COLUMN);
                placed++;
            }
        }
        return board;
    }

    /** Puts blue tiles on the wall in other rows, each at the column the wall pattern gives. */
    private static Board placeColorOnWall(Board board, int count) {
        int placed = 0;
        for (int row = 0; row < 5 && placed < count; row++) {
            if (row != TARGET_ROW) {
                board.getWall()[row][BoardUtils.getWallColumnForColor(row, COLOR)] = tile(COLOR);
                placed++;
            }
        }
        return board;
    }

    /** Places the tile that belongs to this wall cell according to the wall pattern. */
    private static void placeOnWall(Board board, int row, int column) {
        for (String color : COLORS) {
            if (BoardUtils.getWallColumnForColor(row, color) == column) {
                board.getWall()[row][column] = tile(color);
            }
        }
    }

    private static Board emptyBoard() {
        List<List<Tile>> rows = new ArrayList<>();
        for (int size = 1; size <= 5; size++) {
            rows.add(new ArrayList<>(Arrays.asList(new Tile[size])));
        }
        return new Board(1L, rows, new Tile[5][5], new ArrayList<>(Arrays.asList(new Tile[7])));
    }

    private static Move takeOneTile(int targetRow) {
        return new Move(1L, 1L, tiles(COLOR), targetRow);
    }

    private static Factory factory(long id, String... colors) {
        return new Factory(id, tiles(colors), false);
    }

    private static List<Tile> tiles(String... colors) {
        List<Tile> tiles = new ArrayList<>();
        for (String color : colors) {
            tiles.add(tile(color));
        }
        return tiles;
    }

    private static Tile tile(String color) {
        return new Tile(nextTileId++, color);
    }

    // ---------- Report formatting ----------

    private static String describeInput(Board board, Move move) {
        Tile[][] wall = board.getWall();
        long tilesInLine = board.getRows().get(TARGET_ROW).stream().filter(tile -> tile != null).count();
        String target = move.getTargetRow() == -1 ? "the floor" : "pattern line " + (move.getTargetRow() + 1);
        return "wall row " + (TARGET_ROW + 1) + ": " + BoardUtils.countFilledWallRow(wall, TARGET_ROW) + "/5, "
            + "wall column " + (TARGET_COLUMN + 1) + ": " + BoardUtils.countFilledWallColumn(wall, TARGET_COLUMN) + "/5, "
            + COLOR + " on wall: " + BoardUtils.countWallColor(wall, COLOR) + "; "
            + "pattern line " + (TARGET_ROW + 1) + ": " + tilesInLine + "/" + (TARGET_ROW + 1) + " " + COLOR + "; "
            + "move: 1 " + COLOR + " to " + target;
    }

    private static final Pattern WALL_REASON = Pattern.compile("wall (row|column) \\d+ is (\\d+) placements");
    private static final Pattern COLOR_REASON = Pattern.compile("this color set is (\\d+) placements");

    /** Short form of a score and its reasons, e.g. "+20, row depth 1". */
    private static String summarize(int score, List<String> reasons) {
        String points = score > 0 ? "+" + score : String.valueOf(score);
        if (reasons.isEmpty()) {
            return points + ", no goal fired";
        }
        return points + ", " + reasons.stream().map(LongTermGoalsTest::shortReason).collect(Collectors.joining("; "));
    }

    private static String shortReason(String reason) {
        Matcher wall = WALL_REASON.matcher(reason);
        if (wall.find()) {
            return wall.group(1) + " depth " + wall.group(2);
        }
        Matcher color = COLOR_REASON.matcher(reason);
        if (color.find()) {
            return "colour depth " + color.group(1);
        }
        return reason;
    }

    private static void addEndToEndRow(String id, String input, String expected, String obtained) {
        addEndToEndRow(id, input, expected, obtained, expected.equals(obtained));
    }

    private static void addEndToEndRow(String id, String input, String expected, String obtained, boolean pass) {
        report.addRow(END_TO_END, id, input, expected, obtained, pass);
    }

    private static String topMovesTable(List<Move> ranked, int limit) {
        StringBuilder sb = new StringBuilder("### C1 - top " + limit + " of " + ranked.size() + " candidate moves\n\n");
        sb.append("| Rank | Move | Score | Reasons |\n|---|---|---|---|\n");
        for (int i = 0; i < Math.min(limit, ranked.size()); i++) {
            Move move = ranked.get(i);
            sb.append("| ").append(i + 1).append(" | ").append(move).append(" | ").append(move.getScore())
                .append(" | ").append(String.join("<br>", reasonsOf(move))).append(" |\n");
        }
        return sb.toString();
    }

    /** One goal scenario: a board, a move, and the bonus the backward session should add. */
    static class GoalScenario {
        final String id;
        final String goal;
        final Board board;
        final Move move;
        final int expectedScore;
        final String expectedReason;

        GoalScenario(String id, String goal, Board board, Move move, int expectedScore, String expectedReason) {
            this.id = id;
            this.goal = goal;
            this.board = board;
            this.move = move;
            this.expectedScore = expectedScore;
            this.expectedReason = expectedReason;
        }

        List<String> expectedReasons() {
            return expectedReason == null ? Collections.emptyList() : Collections.singletonList(expectedReason);
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
