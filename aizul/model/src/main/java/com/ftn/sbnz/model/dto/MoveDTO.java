package com.ftn.sbnz.model.dto;
import com.ftn.sbnz.model.models.Move;
import com.ftn.sbnz.model.models.Tile;
import java.util.List;
import java.util.Map;

public class MoveDTO {
    private Long factoryId;
    private int targetRow;
    private List<Tile> takenTiles;
    private String description;
    private List<Map<Boolean, String>> appliedRules;

    public MoveDTO(Move move, String description) {
        this.factoryId = move.getFactoryId();
        this.targetRow = move.getTargetRow();
        this.takenTiles = move.getTakenTiles();
        this.appliedRules = move.getAppliedRules();
        this.description = description;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public void setTargetRow(int targetRow) {
        this.targetRow = targetRow;
    }

    public List<Tile> getTakenTiles() {
        return takenTiles;
    }

    public void setTakenTiles(List<Tile> takenTiles) {
        this.takenTiles = takenTiles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Map<Boolean, String>> getAppliedRules() {
        return appliedRules;
    }

    public void setAppliedRules(List<Map<Boolean, String>> appliedRules) {
        this.appliedRules = appliedRules;
    }
}