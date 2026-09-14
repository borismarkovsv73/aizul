package com.ftn.sbnz.service.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Tile;
import com.ftn.sbnz.service.templates.RowPriorityTemplateProcessor;

class RowPriorityTemplateProcessorTest {

    @Test
    void eachMoveIsEvaluatedOnceAgainstTheOriginalBoard() {
        Board board = emptyBoard();
        Move completesFirstLine = move(0, tile(1, "blue"));
        Move doesNotCompleteThirdLine = move(2, tile(2, "yellow"));

        KieSession session = new RowPriorityTemplateProcessor().createDynamicTemplateSession();
        assertNotNull(session);
        session.insert(board);

        FactHandle firstHandle = session.insert(completesFirstLine);
        assertEquals(2, session.fireAllRules());
        session.delete(firstHandle);

        FactHandle secondHandle = session.insert(doesNotCompleteThirdLine);
        assertEquals(1, session.fireAllRules());
        session.delete(secondHandle);
        session.dispose();

        assertEquals(8, completesFirstLine.getScore());
        assertEquals(2, completesFirstLine.getAppliedRules().size());
        assertEquals(3, doesNotCompleteThirdLine.getScore());
        assertEquals(1, doesNotCompleteThirdLine.getAppliedRules().size());
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
