package com.ftn.sbnz.model.models;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Move {
    private Long id;
    private Long factoryId;
    private List<Tile> takenTiles;
    private int targetRow;
    private int score;
    private List<Map<Boolean, String>> appliedRules;

    public Move() {
        this.appliedRules = new ArrayList<Map<Boolean, String>>();
    }

    public Move(Long id, Long factoryId, List<Tile> takenTiles, int targetRow) {
        this.id = id;
        this.factoryId = factoryId;
        this.takenTiles = takenTiles;
        this.targetRow = targetRow;
        this.appliedRules = new ArrayList<Map<Boolean, String>>();
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
    public List<Map<Boolean, String>> getAppliedRules() {
        return appliedRules;
    }
    public void setAppliedRules(List<Map<Boolean, String>> appliedRules) {
        this.appliedRules = appliedRules;
    }

    public void addAppliedRule(boolean isPositive, String ruleName) {
        if (this.appliedRules == null) {
            this.appliedRules = new ArrayList<Map<Boolean, String>>();
        }
        Map<Boolean, String> ruleEntry = new HashMap<>();
        ruleEntry.put(isPositive, ruleName);
        this.appliedRules.add(ruleEntry);
    }

    @Override
    public String toString() {
        int amount = takenTiles.size();
        boolean hasLimeTile = takenTiles.stream().anyMatch(tile -> "lime".equals(tile.getColor()));
        if (hasLimeTile) {
            amount = takenTiles.size() - 1;
        }
        
        return "Take " + amount + " " + takenTiles.get(0).toString() + " tiles from factory " + factoryId + " and place in row " + targetRow;
    }
}
