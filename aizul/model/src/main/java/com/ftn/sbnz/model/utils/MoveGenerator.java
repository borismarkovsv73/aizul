package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Factory;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.models.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating valid moves in the Azul board game.
 * 
 * Azul move rules:
 * - Players can pick all tiles of the same color from any factory
 * - Tiles can be placed on pattern lines (rows) if:
 *   1. The row is empty, OR
 *   2. The row contains tiles of the same color and is not full
 *   3. The color is not already on the wall in that row
 * - Excess tiles go to the floor line (penalty)
 */
public class MoveGenerator {
    
    /**
     * Main method to generate all valid moves for a player given the current game state
     */
    public static List<Move> generateAllValidMoves(GameState gameState, Player player) {
        if (gameState == null || gameState.getFactories() == null || player == null || player.getBoard() == null) {
            return new ArrayList<>();
        }
        
        return generateValidMovesFromFactories(gameState.getFactories(), player.getBoard());
    }
    
    /**
     * Generates all valid moves from the available factories for a given board
     */
    public static List<Move> generateValidMovesFromFactories(List<Factory> factories, Board board) {
        List<Move> validMoves = new ArrayList<>();
        
        if (factories == null || board == null || board.getRows() == null) {
            return validMoves;
        }
        
        // For each factory
        for (Factory factory : factories) {
            if (factory.getTiles() == null || factory.getTiles().isEmpty()) {
                continue;
            }
            
            // Check if this factory is center and has first player token
            boolean centerHasLimeToken = factory.isCenter() && 
                factory.getTiles().stream().anyMatch(tile -> tile != null && "lime".equals(tile.getColor()));
            // Group tiles by color in this factory
            Map<String, List<Tile>> tilesByColor = groupTilesByColor(factory.getTiles());
            
            // For each color available in this factory
            for (Map.Entry<String, List<Tile>> colorEntry : tilesByColor.entrySet()) {
                String color = colorEntry.getKey();
                List<Tile> tilesOfColor = colorEntry.getValue();
                
                // Skip lime tiles as they are first player tokens, not placeable tiles
                if ("lime".equals(color)) {
                    continue;
                }
                
                // Generate valid moves for each target row
                List<Move> movesForColor = generateValidMovesForTileColor(factory, tilesOfColor, board, centerHasLimeToken);
                validMoves.addAll(movesForColor);
            }
        }
        
        return validMoves;
    }
    
    /**
     * Generates valid moves for a specific tile color from a factory
     */
    private static List<Move> generateValidMovesForTileColor(Factory factory, List<Tile> tilesOfColor, Board board, boolean centerHasLimeToken) {
        List<Move> moves = new ArrayList<>();
        String color = tilesOfColor.get(0).getColor();
        
        // Check all possible target rows - only add if valid
        for (int rowIndex = 0; rowIndex < board.getRows().size(); rowIndex++) {
            if (isValidPlacement(board, rowIndex, color, tilesOfColor.size())) {
                Move move = createMove(factory.getId(), tilesOfColor, rowIndex, centerHasLimeToken, factory);
                moves.add(move);
            }
        }
        
        // Always add floor line option (row -1) as it's always valid
        Move floorMove = createMove(factory.getId(), tilesOfColor, -1, centerHasLimeToken, factory);
        moves.add(floorMove);
        
        return moves;
    }
    
    /**
     * Creates a Move object with the given parameters. If the move is from center factory
     * and lime token exists, includes the lime token in the taken tiles.
     */
    private static Move createMove(Long factoryId, List<Tile> tiles, int targetRow, boolean centerHasLimeToken, Factory factory) {
        Move move = new Move();
        move.setFactoryId(factoryId);
        
        // Copy the tiles
        List<Tile> allTiles = new ArrayList<>(tiles);
        
        // If this is from center factory and lime token exists, add it to the move
        if (centerHasLimeToken) {
            Tile limeToken = factory.getTiles().stream()
                .filter(tile -> tile != null && "lime".equals(tile.getColor()))
                .findFirst()
                .orElse(null);
                
            if (limeToken != null) {
                allTiles.add(limeToken);
            }
        }
        
        move.setTakenTiles(allTiles);
        move.setTargetRow(targetRow);
        move.setScore(0); // Initialize score to 0
        return move;
    }
    
    /**
     * Groups tiles by their color
     */
    private static Map<String, List<Tile>> groupTilesByColor(List<Tile> tiles) {
        Map<String, List<Tile>> tilesByColor = new HashMap<>();
        
        for (Tile tile : tiles) {
            if (tile != null && tile.getColor() != null) {
                tilesByColor.computeIfAbsent(tile.getColor(), k -> new ArrayList<>()).add(tile);
            }
        }
        
        return tilesByColor;
    }
    
    /**
     * Validates if tiles can be placed on a specific row
     */
    public static boolean isValidPlacement(Board board, int rowIndex, String tileColor, int numberOfTiles) {
        return canPlaceTilesOnRow(board, rowIndex, tileColor) && 
               !isTileColorOnWallInRow(board, rowIndex, tileColor);
    }
    
    /**
     * Checks if tiles can be placed on a specific row based on capacity and color constraints
     */
    private static boolean canPlaceTilesOnRow(Board board, int rowIndex, String tileColor) {
        if (board.getRows() == null || rowIndex < 0 || rowIndex >= board.getRows().size()) {
            return false;
        }
        
        List<Tile> row = board.getRows().get(rowIndex);
        if (row == null) {
            return false;
        }
        
        // In Azul, row capacity follows pattern: row 0 = 1 tile, row 1 = 2 tiles, etc.
        int maxRowCapacity = rowIndex + 1;
        int currentTilesInRow = (int) row.stream().filter(tile -> tile != null).count();
        
        // Check if there's space for at least one tile
        if (currentTilesInRow >= maxRowCapacity) {
            return false;
        }
        
        // If row is empty, any color can be placed
        if (currentTilesInRow == 0) {
            return true;
        }
        
        // If row has tiles, they must be the same color
        String existingRowColor = row.stream()
                .filter(tile -> tile != null)
                .map(Tile::getColor)
                .findFirst()
                .orElse(null);
                
        return existingRowColor != null && existingRowColor.equals(tileColor);
    }
    
    /**
     * Checks if a tile color is already on the wall in the corresponding row
     * In Azul, each color can only appear once per row on the wall
     */
    private static boolean isTileColorOnWallInRow(Board board, int rowIndex, String tileColor) {
        if (board.getWall() == null || rowIndex < 0 || rowIndex >= board.getWall().length) {
            return false;
        }
        
        Tile[] wallRow = board.getWall()[rowIndex];
        if (wallRow == null) {
            return false;
        }
        
        for (Tile tile : wallRow) {
            if (tile != null && tile.getColor() != null && tile.getColor().equals(tileColor)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculates the number of tiles that would go to the floor line for a given move.
     * This includes the first player token (lime) if present in taken tiles.
     * 
     * Note: This method calculates the total tiles going to floor regardless of current
     * floor capacity. All moves are legal in Azul even if the floor is full - excess
     * tiles beyond floor capacity are simply discarded without additional penalty.
     * 
     * @param board The current board state
     * @param move The move to calculate floor tiles for
     * @return Total number of tiles that would go to the floor (may exceed floor capacity)
     */
    public static int calculateFloorTiles(Board board, Move move) {
        if (move.getTargetRow() == -1) {
            // All tiles go to floor
            return move.getTakenTiles().size();
        }
        
        // Count regular tiles (non-lime) that can be placed
        int regularTiles = (int) move.getTakenTiles().stream()
            .filter(tile -> tile != null && !"lime".equals(tile.getColor()))
            .count();
            
        // Count lime tiles (they always go to floor)
        int limeTiles = (int) move.getTakenTiles().stream()
            .filter(tile -> tile != null && "lime".equals(tile.getColor()))
            .count();
        
        List<Tile> targetRow = board.getRows().get(move.getTargetRow());
        int maxCapacity = move.getTargetRow() + 1;
        int currentTiles = (int) targetRow.stream().filter(tile -> tile != null).count();
        int availableSpace = maxCapacity - currentTiles;
        
        // Regular tiles that don't fit + all lime tiles go to floor
        int regularTilesToFloor = Math.max(0, regularTiles - availableSpace);
        
        return regularTilesToFloor + limeTiles;
    }
}