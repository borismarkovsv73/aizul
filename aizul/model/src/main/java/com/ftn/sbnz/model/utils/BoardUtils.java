package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Tile;

import java.util.List;
import java.util.ArrayList;

public class BoardUtils {
    
    /**
     * Applies a move to the board. Lime tokens automatically go to the floor,
     * regular tiles go to the target row or floor if overflow.
     */
    public static void applyMoveToBoard(Board board, Move move) {
        List<Tile> takenTiles = move.getTakenTiles();
        int targetRow = move.getTargetRow();

        if (targetRow == -1) {
            // All tiles go to floor
            placeTilesOnFloor(board, takenTiles);
            return;
        }
        
        // Separate lime tokens (they always go to floor) from regular tiles
        List<Tile> regularTiles = new ArrayList<>();
        List<Tile> limeTokens = new ArrayList<>();
        
        for (Tile tile : takenTiles) {
            if (tile != null && "lime".equals(tile.getColor())) {
                limeTokens.add(tile);
            } else {
                regularTiles.add(tile);
            }
        }
        
        // Place lime tokens directly on floor
        if (!limeTokens.isEmpty()) {
            placeTilesOnFloor(board, limeTokens);
        }
        
        // Handle regular tiles placement on the target row
        List<Tile> row = board.getRows().get(targetRow);
        int maxRowCapacity = targetRow + 1;
        int currentTilesInRow = (int) row.stream().filter(tile -> tile != null).count();
        int availableSpace = maxRowCapacity - currentTilesInRow;

        int tilesPlacedOnRow = Math.min(availableSpace, regularTiles.size());
        for (int i = 0; i < tilesPlacedOnRow; i++) {
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == null) {
                    row.set(j, regularTiles.get(i));
                    break;
                }
            }
        }

        // Place overflow regular tiles on floor
        if (tilesPlacedOnRow < regularTiles.size()) {
            List<Tile> overflowTiles = regularTiles.subList(tilesPlacedOnRow, regularTiles.size());
            placeTilesOnFloor(board, overflowTiles);
        }
    }

    private static void placeTilesOnFloor(Board board, List<Tile> tiles) {
        List<Tile> floor = board.getFloor();
        for (Tile tile : tiles) {
            for (int i = 0; i < floor.size(); i++) {
                if (floor.get(i) == null) {
                    floor.set(i, tile);
                    break;
                }
            }
        }
    }

    public static Board copyBoard(Board original) {
        if (original == null) {
            return null;
        }
        
        Board copy = new Board();
        copy.setId(original.getId());

        if (original.getRows() != null) {
            List<List<Tile>> copyRows = new java.util.ArrayList<>();
            for (List<Tile> row : original.getRows()) {
                List<Tile> copyRow = new java.util.ArrayList<>();
                for (Tile tile : row) {
                    copyRow.add(tile);
                }
                copyRows.add(copyRow);
            }
            copy.setRows(copyRows);
        }
        
        if (original.getFloor() != null) {
            List<Tile> copyFloor = new java.util.ArrayList<>(original.getFloor());
            copy.setFloor(copyFloor);
        }
        
        if (original.getWall() != null) {
            Tile[][] copyWall = new Tile[original.getWall().length][];
            for (int i = 0; i < original.getWall().length; i++) {
                if (original.getWall()[i] != null) {
                    copyWall[i] = original.getWall()[i].clone();
                }
            }
            copy.setWall(copyWall);
        }
        
        return copy;
    }

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
