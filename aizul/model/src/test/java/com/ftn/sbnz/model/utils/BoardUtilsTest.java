package com.ftn.sbnz.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Tile;

class BoardUtilsTest {

    @Test
    void completionIsCheckedOnlyForTheMovesTargetPatternLine() {
        Board board = emptyBoard();
        board.getRows().get(1).set(0, tile(1, "red"));
        board.getRows().get(1).set(1, tile(2, "red"));
        board.getRows().get(2).set(0, tile(3, "blue"));
        board.getRows().get(2).set(1, tile(4, "blue"));

        Move completesThirdLine = move(2, tile(5, "blue"));
        Move doesNotCompleteFifthLine = move(4, tile(6, "yellow"));

        assertTrue(BoardUtils.completesTargetPatternLine(board, completesThirdLine));
        assertFalse(BoardUtils.completesTargetPatternLine(board, doesNotCompleteFifthLine));
    }

    @Test
    void wallColumnUsesTheExactAzulCoordinateForTheMovesColor() {
        Board board = emptyBoard();
        board.getRows().get(2).set(0, tile(10, "blue"));
        board.getRows().get(2).set(1, tile(11, "blue"));

        board.getWall()[0][0] = tile(12, "blue");
        board.getWall()[1][2] = tile(13, "yellow");
        board.getWall()[3][0] = tile(14, "red");
        board.getWall()[4][0] = tile(15, "yellow");
        board.getWall()[0][2] = tile(17, "red");
        board.getWall()[1][0] = tile(18, "white");

        Move move = move(2, tile(16, "blue"));

        assertEquals(2, BoardUtils.getWallColumnForColor(2, "blue"));
        assertTrue(BoardUtils.willAdvanceWallColumnAtDepth(board, move, 3));
        assertFalse(BoardUtils.willAdvanceWallColumnAtDepth(board, move, 1));
    }

    @Test
    void wallGoalsRequireThePatternLineToCompleteThisRound() {
        Board board = emptyBoard();
        for (int column = 0; column < 4; column++) {
            board.getWall()[4][column] = tile(20 + column, "filled-" + column);
        }

        Move oneTileIntoEmptyFifthLine = move(4, tile(30, "blue"));

        assertFalse(BoardUtils.willPlaceTileOnWallAfterMove(board, oneTileIntoEmptyFifthLine));
        assertFalse(BoardUtils.willCompleteWallRowAfterMove(board, oneTileIntoEmptyFifthLine));
    }

    @Test
    void floorCountIncludesOverflowAndFirstPlayerMarker() {
        Board board = emptyBoard();
        board.getRows().get(2).set(0, tile(40, "blue"));
        board.getRows().get(2).set(1, tile(41, "blue"));

        Move move = move(2,
            tile(42, "blue"), tile(43, "blue"), tile(44, "blue"), tile(45, "lime"));

        assertEquals(3, BoardUtils.countNewFloorTiles(board, move));
    }

    @Test
    void enemyThreatRequiresEnoughTilesToCompleteTheRelevantPatternLine() {
        Board enemyBoard = emptyBoard();
        for (int column = 1; column < 5; column++) {
            enemyBoard.getWall()[3][column] = tile(50 + column, "filled-" + column);
        }

        Move redTile = move(-1, tile(60, "red"));
        assertFalse(BoardUtils.canEnemyAdvanceWallRow(enemyBoard, redTile, 4));

        enemyBoard.getRows().get(3).set(0, tile(61, "red"));
        enemyBoard.getRows().get(3).set(1, tile(62, "red"));
        enemyBoard.getRows().get(3).set(2, tile(63, "red"));
        assertTrue(BoardUtils.canEnemyAdvanceWallRow(enemyBoard, redTile, 4));
    }

    @Test
    void identicalExplanationsAreNotAddedTwice() {
        Move move = move(0, tile(70, "blue"));

        move.addAppliedRule(true, "same explanation");
        move.addAppliedRule(true, "same explanation");

        assertEquals(1, move.getAppliedRules().size());
    }

    private static Board emptyBoard() {
        List<List<Tile>> rows = new ArrayList<>();
        for (int size = 1; size <= 5; size++) {
            rows.add(new ArrayList<>(Arrays.asList(new Tile[size])));
        }
        return new Board(1L, rows, new Tile[5][5],
            new ArrayList<>(Arrays.asList(new Tile[7])));
    }

    private static Move move(int targetRow, Tile... tiles) {
        return new Move(1L, 1L, new ArrayList<>(Arrays.asList(tiles)), targetRow);
    }

    private static Tile tile(long id, String color) {
        return new Tile(id, color);
    }
}
