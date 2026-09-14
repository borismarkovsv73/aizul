package com.ftn.sbnz.model.utils;

import com.ftn.sbnz.model.models.Board;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Player;
import com.ftn.sbnz.model.models.Tile;

import java.util.List;
import java.util.ArrayList;

public class BoardUtils {

    private static final String[][] WALL_PATTERN = {
        {"blue", "yellow", "red", "black", "white"},
        {"white", "blue", "yellow", "red", "black"},
        {"black", "white", "blue", "yellow", "red"},
        {"red", "black", "white", "blue", "yellow"},
        {"yellow", "red", "black", "white", "blue"}
    };

    private static final int[] FLOOR_PENALTIES = {-1, -1, -2, -2, -2, -3, -3};
    
    public static void applyMoveToBoard(Board board, Move move) {
        List<Tile> takenTiles = move.getTakenTiles();
        int targetRow = move.getTargetRow();
        if (targetRow == -1) {

            placeTilesOnFloor(board, takenTiles);
            return;
        }
        
        List<Tile> regularTiles = new ArrayList<>();
        List<Tile> limeTokens = new ArrayList<>();
        
        for (Tile tile : takenTiles) {
            if (tile != null && "lime".equals(tile.getColor())) {
                limeTokens.add(tile);
            } else {
                regularTiles.add(tile);
            }
        }
        
        if (!limeTokens.isEmpty()) {
            placeTilesOnFloor(board, limeTokens);
        }
        
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

    public static boolean completesTargetPatternLine(Board boardBeforeMove, Move move) {
        if (!hasValidTargetPatternLine(boardBeforeMove, move)) {
            return false;
        }

        List<Tile> targetLine = boardBeforeMove.getRows().get(move.getTargetRow());
        int currentTiles = countTiles(targetLine);
        int capacity = move.getTargetRow() + 1;
        String moveColor = getMoveColor(move);
        String existingColor = firstTileColor(targetLine);
        return moveColor != null
            && (existingColor == null || moveColor.equals(existingColor))
            && !wallRowContainsColor(boardBeforeMove.getWall(), move.getTargetRow(), moveColor)
            && currentTiles < capacity
            && currentTiles + countRegularTakenTiles(move) >= capacity;
    }

    public static boolean hasStartedTargetPatternLine(Board boardBeforeMove, Move move) {
        if (!hasValidTargetPatternLine(boardBeforeMove, move)) {
            return false;
        }
        return countTiles(boardBeforeMove.getRows().get(move.getTargetRow())) > 0;
    }

    public static int countRegularTakenTiles(Move move) {
        if (move == null || move.getTakenTiles() == null) {
            return 0;
        }
        int count = 0;
        for (Tile tile : move.getTakenTiles()) {
            if (tile != null && !"lime".equals(tile.getColor())) {
                count++;
            }
        }
        return count;
    }

    public static String getMoveColor(Move move) {
        if (move == null || move.getTakenTiles() == null) {
            return null;
        }
        for (Tile tile : move.getTakenTiles()) {
            if (tile != null && tile.getColor() != null && !"lime".equals(tile.getColor())) {
                return tile.getColor();
            }
        }
        return null;
    }

    public static int countNewFloorTiles(Board boardBeforeMove, Move move) {
        if (boardBeforeMove == null || boardBeforeMove.getFloor() == null || move == null) {
            return 0;
        }

        int fallingTiles;
        if (move.getTargetRow() == -1) {
            fallingTiles = move.getTakenTiles() == null ? 0 : move.getTakenTiles().size();
        } else if (hasValidTargetPatternLine(boardBeforeMove, move)) {
            int currentTiles = countTiles(boardBeforeMove.getRows().get(move.getTargetRow()));
            int available = move.getTargetRow() + 1 - currentTiles;
            int overflow = Math.max(0, countRegularTakenTiles(move) - available);
            int firstPlayerMarkers = countFirstPlayerMarkers(move);
            fallingTiles = overflow + firstPlayerMarkers;
        } else {
            fallingTiles = 0;
        }

        int freeFloorSpaces = boardBeforeMove.getFloor().size() - countTiles(boardBeforeMove.getFloor());
        return Math.min(fallingTiles, Math.max(0, freeFloorSpaces));
    }

    public static boolean willPlaceTileOnWallAfterMove(Board boardBeforeMove, Move move) {
        if (!completesTargetPatternLine(boardBeforeMove, move)) {
            return false;
        }
        String color = getMoveColor(move);
        int targetRow = move.getTargetRow();
        int targetColumn = getWallColumnForColor(targetRow, color);
        return targetColumn >= 0
            && boardBeforeMove.getWall() != null
            && boardBeforeMove.getWall()[targetRow][targetColumn] == null;
    }

    public static boolean willCompleteWallRowAfterMove(Board boardBeforeMove, Move move) {
        return willPlaceTileOnWallAfterMove(boardBeforeMove, move)
            && countFilledWallRow(boardBeforeMove.getWall(), move.getTargetRow()) == 4;
    }

    public static boolean willAdvanceWallRowAtDepth(Board boardBeforeMove, Move move, int missingBeforeMove) {
        if (!willPlaceTileOnWallAfterMove(boardBeforeMove, move)) {
            return false;
        }
        return 5 - countFilledWallRow(boardBeforeMove.getWall(), move.getTargetRow()) == missingBeforeMove;
    }

    public static boolean willAdvanceWallColumnAtDepth(Board boardBeforeMove, Move move, int missingBeforeMove) {
        if (!willPlaceTileOnWallAfterMove(boardBeforeMove, move)) {
            return false;
        }
        int column = getWallColumnForColor(move.getTargetRow(), getMoveColor(move));
        return column >= 0 && 5 - countFilledWallColumn(boardBeforeMove.getWall(), column) == missingBeforeMove;
    }

    public static boolean willAdvanceColorSetAtCount(Board boardBeforeMove, Move move, int existingCount) {
        if (!willPlaceTileOnWallAfterMove(boardBeforeMove, move)) {
            return false;
        }
        return countWallColor(boardBeforeMove.getWall(), getMoveColor(move)) == existingCount;
    }

    public static boolean canBlockEnemyPatternLineCompletion(Board enemyBoard, Move move) {
        if (enemyBoard == null || enemyBoard.getRows() == null) {
            return false;
        }
        String color = getMoveColor(move);
        int availableTiles = countRegularTakenTiles(move);
        if (color == null || availableTiles == 0) {
            return false;
        }

        for (int rowIndex = 0; rowIndex < enemyBoard.getRows().size(); rowIndex++) {
            List<Tile> line = enemyBoard.getRows().get(rowIndex);
            int currentTiles = countTiles(line);
            int capacity = rowIndex + 1;
            if (currentTiles == 0 || currentTiles >= capacity) {
                continue;
            }
            String existingColor = firstTileColor(line);
            if (color.equals(existingColor)
                    && !wallRowContainsColor(enemyBoard.getWall(), rowIndex, color)
                    && availableTiles >= capacity - currentTiles) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEnemyAdvanceColorSet(Board enemyBoard, Move move, int existingColorCount) {
        String color = getMoveColor(move);
        return color != null
            && countWallColor(enemyBoard.getWall(), color) == existingColorCount
            && canEnemyCompleteAnyPatternLine(enemyBoard, color, countRegularTakenTiles(move));
    }

    public static boolean canEnemyAdvanceWallRow(Board enemyBoard, Move move, int filledTiles) {
        String color = getMoveColor(move);
        if (enemyBoard == null || enemyBoard.getWall() == null || color == null) {
            return false;
        }
        for (int row = 0; row < enemyBoard.getWall().length; row++) {
            int column = getWallColumnForColor(row, color);
            if (column >= 0
                    && enemyBoard.getWall()[row][column] == null
                    && countFilledWallRow(enemyBoard.getWall(), row) == filledTiles
                    && canEnemyCompletePatternLine(enemyBoard, color, row, countRegularTakenTiles(move))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEnemyAdvanceWallColumn(Board enemyBoard, Move move, int filledTiles) {
        String color = getMoveColor(move);
        if (enemyBoard == null || enemyBoard.getWall() == null || color == null) {
            return false;
        }
        for (int row = 0; row < enemyBoard.getWall().length; row++) {
            int column = getWallColumnForColor(row, color);
            if (column >= 0
                    && enemyBoard.getWall()[row][column] == null
                    && countFilledWallColumn(enemyBoard.getWall(), column) == filledTiles
                    && canEnemyCompletePatternLine(enemyBoard, color, row, countRegularTakenTiles(move))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProjectedAheadAfterMove(Player currentPlayer, List<Player> players, Move move) {
        if (currentPlayer == null || players == null) {
            return false;
        }
        int currentProjectedScore = projectFinalScoreAfterRound(currentPlayer, move);
        for (Player player : players) {
            if (player != currentPlayer && player != null
                    && projectFinalScoreAfterRound(player, null) >= currentProjectedScore) {
                return false;
            }
        }
        return true;
    }

    public static boolean isProjectedAheadIfEnemyTakesMove(
            Player currentPlayer, Player enemyPlayer, List<Player> players, Move availableMove) {
        if (currentPlayer == null || enemyPlayer == null || players == null || availableMove == null) {
            return false;
        }

        int enemyTargetRow = findBestEnemyWallRowTarget(enemyPlayer.getBoard(), availableMove);
        if (enemyTargetRow < 0) {
            return false;
        }

        Move simulatedEnemyMove = new Move(
            availableMove.getId(),
            availableMove.getFactoryId(),
            availableMove.getTakenTiles(),
            enemyTargetRow
        );
        int currentProjectedScore = projectFinalScoreAfterRound(currentPlayer, null);
        for (Player player : players) {
            if (player == currentPlayer || player == null) {
                continue;
            }
            int projectedScore = player == enemyPlayer
                ? projectFinalScoreAfterRound(player, simulatedEnemyMove)
                : projectFinalScoreAfterRound(player, null);
            if (projectedScore >= currentProjectedScore) {
                return false;
            }
        }
        return true;
    }

    public static int projectFinalScoreAfterRound(Player player, Move move) {
        if (player == null || player.getBoard() == null) {
            return 0;
        }
        Board projectedBoard = copyBoard(player.getBoard());
        if (move != null) {
            applyMoveToBoard(projectedBoard, move);
        }

        int score = player.getScore();
        for (int row = 0; row < projectedBoard.getRows().size(); row++) {
            List<Tile> patternLine = projectedBoard.getRows().get(row);
            if (!isPatternLineComplete(patternLine)) {
                continue;
            }
            String color = firstTileColor(patternLine);
            int column = getWallColumnForColor(row, color);
            if (column >= 0 && projectedBoard.getWall()[row][column] == null) {
                projectedBoard.getWall()[row][column] = firstTile(patternLine);
                score += scoreWallPlacement(projectedBoard.getWall(), row, column);
            }
        }

        score += calculateFloorPenalty(projectedBoard.getFloor());
        score = Math.max(0, score);
        score += calculateEndGameBonuses(projectedBoard.getWall());
        return score;
    }

    public static int getWallColumnForColor(int row, String color) {
        if (row < 0 || row >= WALL_PATTERN.length || color == null) {
            return -1;
        }
        for (int column = 0; column < WALL_PATTERN[row].length; column++) {
            if (color.equals(WALL_PATTERN[row][column])) {
                return column;
            }
        }
        return -1;
    }

    public static int countFilledWallRow(Tile[][] wall, int row) {
        if (wall == null || row < 0 || row >= wall.length || wall[row] == null) {
            return 0;
        }
        int count = 0;
        for (Tile tile : wall[row]) {
            if (tile != null) count++;
        }
        return count;
    }

    public static int countFilledWallColumn(Tile[][] wall, int column) {
        if (wall == null || wall.length == 0 || column < 0 || column >= wall[0].length) {
            return 0;
        }
        int count = 0;
        for (Tile[] row : wall) {
            if (row != null && row[column] != null) count++;
        }
        return count;
    }

    public static int countWallColor(Tile[][] wall, String color) {
        if (wall == null || color == null) {
            return 0;
        }
        int count = 0;
        for (Tile[] row : wall) {
            if (row == null) continue;
            for (Tile tile : row) {
                if (tile != null && color.equals(tile.getColor())) count++;
            }
        }
        return count;
    }

    private static boolean hasValidTargetPatternLine(Board board, Move move) {
        return board != null
            && board.getRows() != null
            && board.getWall() != null
            && move != null
            && move.getTargetRow() >= 0
            && move.getTargetRow() < board.getRows().size();
    }

    private static int countFirstPlayerMarkers(Move move) {
        int count = 0;
        if (move != null && move.getTakenTiles() != null) {
            for (Tile tile : move.getTakenTiles()) {
                if (tile != null && "lime".equals(tile.getColor())) count++;
            }
        }
        return count;
    }

    private static int countTiles(List<Tile> tiles) {
        if (tiles == null) return 0;
        int count = 0;
        for (Tile tile : tiles) {
            if (tile != null) count++;
        }
        return count;
    }

    private static boolean isPatternLineComplete(List<Tile> patternLine) {
        return patternLine != null && !patternLine.isEmpty() && countTiles(patternLine) == patternLine.size();
    }

    private static Tile firstTile(List<Tile> tiles) {
        if (tiles == null) return null;
        for (Tile tile : tiles) {
            if (tile != null) return tile;
        }
        return null;
    }

    private static String firstTileColor(List<Tile> tiles) {
        Tile tile = firstTile(tiles);
        return tile == null ? null : tile.getColor();
    }

    private static boolean wallRowContainsColor(Tile[][] wall, int row, String color) {
        if (wall == null || row < 0 || row >= wall.length || wall[row] == null) return false;
        for (Tile tile : wall[row]) {
            if (tile != null && color.equals(tile.getColor())) return true;
        }
        return false;
    }

    private static boolean canEnemyCompleteAnyPatternLine(Board board, String color, int availableTiles) {
        if (board == null || board.getRows() == null) return false;
        for (int row = 0; row < board.getRows().size(); row++) {
            if (canEnemyCompletePatternLine(board, color, row, availableTiles)) return true;
        }
        return false;
    }

    private static boolean canEnemyCompletePatternLine(Board board, String color, int row, int availableTiles) {
        if (board == null || board.getRows() == null || row < 0 || row >= board.getRows().size()) {
            return false;
        }
        List<Tile> patternLine = board.getRows().get(row);
        int currentTiles = countTiles(patternLine);
        int capacity = row + 1;
        if (availableTiles <= 0
                || currentTiles >= capacity
                || currentTiles + availableTiles < capacity
                || wallRowContainsColor(board.getWall(), row, color)) {
            return false;
        }
        String existingColor = firstTileColor(patternLine);
        return existingColor == null || color.equals(existingColor);
    }

    private static int findBestEnemyWallRowTarget(Board board, Move move) {
        if (board == null || board.getRows() == null || board.getWall() == null) {
            return -1;
        }
        String color = getMoveColor(move);
        int availableTiles = countRegularTakenTiles(move);
        int bestRow = -1;
        int mostFilledTiles = -1;
        for (int row = 0; row < board.getRows().size(); row++) {
            int column = getWallColumnForColor(row, color);
            if (column >= 0
                    && board.getWall()[row][column] == null
                    && canEnemyCompletePatternLine(board, color, row, availableTiles)) {
                int filledTiles = countFilledWallRow(board.getWall(), row);
                if (filledTiles > mostFilledTiles) {
                    mostFilledTiles = filledTiles;
                    bestRow = row;
                }
            }
        }
        return bestRow;
    }

    private static int scoreWallPlacement(Tile[][] wall, int row, int column) {
        int horizontal = 1 + countDirection(wall, row, column, 0, -1) + countDirection(wall, row, column, 0, 1);
        int vertical = 1 + countDirection(wall, row, column, -1, 0) + countDirection(wall, row, column, 1, 0);
        if (horizontal == 1 && vertical == 1) return 1;
        if (horizontal == 1) return vertical;
        if (vertical == 1) return horizontal;
        return horizontal + vertical;
    }

    private static int countDirection(Tile[][] wall, int row, int column, int rowDelta, int columnDelta) {
        int count = 0;
        int currentRow = row + rowDelta;
        int currentColumn = column + columnDelta;
        while (currentRow >= 0 && currentRow < wall.length
                && currentColumn >= 0 && currentColumn < wall[currentRow].length
                && wall[currentRow][currentColumn] != null) {
            count++;
            currentRow += rowDelta;
            currentColumn += columnDelta;
        }
        return count;
    }

    private static int calculateFloorPenalty(List<Tile> floor) {
        if (floor == null) return 0;
        int penalty = 0;
        for (int i = 0; i < floor.size() && i < FLOOR_PENALTIES.length; i++) {
            if (floor.get(i) != null) penalty += FLOOR_PENALTIES[i];
        }
        return penalty;
    }

    private static int calculateEndGameBonuses(Tile[][] wall) {
        if (wall == null) return 0;
        int bonus = 0;
        for (int row = 0; row < wall.length; row++) {
            if (countFilledWallRow(wall, row) == 5) bonus += 2;
        }
        for (int column = 0; column < wall[0].length; column++) {
            if (countFilledWallColumn(wall, column) == 5) bonus += 7;
        }
        String[] colors = {"blue", "yellow", "red", "black", "white"};
        for (String color : colors) {
            if (countWallColor(wall, color) == 5) bonus += 10;
        }
        return bonus;
    }
}
