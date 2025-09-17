package com.ftn.sbnz.model.models;

import java.util.List;

public class Move {
    private Long id;
    private Long factoryId;
    private List<Tile> takenTiles;
    private int targetRow;
    private int score;


    public Move() {
    }

    public Move(Long id, Long factoryId, List<Tile> takenTiles, int targetRow) {
        this.id = id;
        this.factoryId = factoryId;
        this.takenTiles = takenTiles;
        this.targetRow = targetRow;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getFactoryId() {
        return factoryId;
    }
    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }
    public List<Tile> getTakenTiles() {
        return takenTiles;
    }
    public void setTakenTiles(List<Tile> takenTiles) {
        this.takenTiles = takenTiles;
    }
    public int getTargetRow() {
        return targetRow;
    }
    public void setTargetRow(int targetRow) {
        this.targetRow = targetRow;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
