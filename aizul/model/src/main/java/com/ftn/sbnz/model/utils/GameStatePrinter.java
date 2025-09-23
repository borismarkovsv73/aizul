package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Factory;
import com.ftn.sbnz.model.models.GameState;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.models.Tile;

import java.util.List;

public class GameStatePrinter {
    public static void printGameState(GameState gameState, Player player) {
        System.out.println("=== GAME STATE ===");
        System.out.println("Round: " + gameState.getRoundNumber());
        System.out.println();
        
        printFactories(gameState.getFactories());
        System.out.println();
        
        System.out.println("PLAYER BOARD:");
        printPlayerBoard(player.getBoard(), player);
    }
    
    public static void printFactories(List<Factory> factories) {
        System.out.println("FACTORIES:");
        for (Factory factory : factories) {
            if (factory.isCenter()) {
                System.out.print("Center Factory: ");
            } else {
                System.out.print("Factory " + factory.getId() + ": ");
            }
            
            if (factory.getTiles().isEmpty()) {
                System.out.println("EMPTY");
            } else {
                for (Tile tile : factory.getTiles()) {
                    System.out.print(tile.getColor() + " ");
                }
                System.out.println();
            }
        }
    }
    
    public static void printPlayerBoard(Board board, Player player) {
        System.out.println("Pattern Lines (left) | Wall (right)");
        System.out.println("-----------------------------------");
        
        for (int row = 0; row < 5; row++) {
            List<Tile> patternRow = board.getRows().get(row);
            int maxCapacity = row + 1;
            
            for (int i = 0; i < (4 - row); i++) {
                System.out.print("  ");
            }
            
            for (int i = 0; i < maxCapacity; i++) {
                if (i < patternRow.size() && patternRow.get(i) != null) {
                    System.out.print(getColorInitial(patternRow.get(i).getColor()) + " ");
                } else {
                    System.out.print("_ ");
                }
            }
            
            System.out.print("| ");
            
            Tile[] wallRow = board.getWall()[row];
            for (int i = 0; i < 5; i++) {
                if (wallRow[i] != null) {
                    System.out.print(getColorInitial(wallRow[i].getColor()) + " ");
                } else {
                    System.out.print("_ ");
                }
            }
            
            System.out.println();
        }
        
        System.out.println();
        System.out.print("Floor Line: ");
        if (board.getFloor().isEmpty()) {
            System.out.println("EMPTY");
        } else {
            for (Tile tile : board.getFloor()) {
                System.out.print(getColorInitial(tile.getColor()) + " ");
            }
            System.out.println();
        }
        
        System.out.println("Player Score: " + player.getScore());
    }
    
    public static void printMoves(List<Move> moves) {
        if (moves.isEmpty()) {
            System.out.println("No valid moves available!");
            return;
        }
        
        System.out.println("=== VALID MOVES ===");
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            String tileColor = move.getTakenTiles().get(0).getColor();
            int tileCount = move.getTakenTiles().size();
            String placement = move.getTargetRow() == -1 ? "floor line" : "pattern line " + (move.getTargetRow() + 1);
            
            System.out.printf("Move %d: Take %d %s tile%s from factory %d → place on %s\n",
                    i + 1,
                    tileCount,
                    tileColor,
                    tileCount > 1 ? "s" : "",
                    move.getFactoryId(),
                    placement
            );
        }
        
        System.out.println("\nTotal valid moves: " + moves.size());
    }
    
    public static void printGameStateAndMoves(GameState gameState, Player player, List<Move> moves) {
        printGameState(gameState, player);
        System.out.println();
        printMoves(moves);
    }

    private static String getColorInitial(String color) {
        if (color == null) return "_";
        switch (color.toUpperCase()) {
            case "RED": return "R";
            case "BLUE": return "B";
            case "YELLOW": return "Y";
            case "BLACK": return "K";
            case "WHITE": return "W";
            default: return color.substring(0, 1).toUpperCase();
        }
    }
    
    public static void printMove(Move move) {
        String tileColor = move.getTakenTiles().get(0).getColor();
        int tileCount = move.getTakenTiles().size();
        String placement = move.getTargetRow() == -1 ? "floor line" : "pattern line " + (move.getTargetRow() + 1);
        
        System.out.printf("Take %d %s tile%s from factory %d and place on %s\n",
                tileCount,
                tileColor,
                tileCount > 1 ? "s" : "",
                move.getFactoryId(),
                placement
        );
    }
}