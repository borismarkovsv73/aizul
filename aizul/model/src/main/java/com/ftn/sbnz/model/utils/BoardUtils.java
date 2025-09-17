package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Tile;

import java.util.List;

public class BoardUtils {

    public static boolean completedRowContainsMoveTile(Board board, Move move) {
        if (board == null || move == null) return false;

        for (List<Tile> row : board.getRows()) {
            if (!row.isEmpty() && row.stream().allMatch(tile -> tile != null)) {
                boolean containsFromMove = row.stream().anyMatch(
                        rowTile -> move.getTakenTiles().stream()
                                .anyMatch(moveTile -> moveTile.getId().equals(rowTile.getId()))
                );
                if (containsFromMove) {
                    return true;
                }
            }
        }

        return false;
    }
}
