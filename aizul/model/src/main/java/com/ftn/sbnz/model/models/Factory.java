package com.ftn.sbnz.model.models;

import java.util.List;

public class Factory {
    private Long id;
    private List<Tile> tiles;
    private boolean isCenter;

    public Factory() {
    }

    public Factory(Long id, List<Tile> tiles, boolean isCenter) {
        this.id = id;
        this.tiles = tiles;
        this.isCenter = isCenter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }
}
