package com.ftn.sbnz.model.models;

import java.util.List;

public class Board {
    private Long id;
    private List<List<Tile>> rows;
    private Tile[][] wall;
    private List<Tile> floor;

    public Board() {
    }

    public Board(Long id, List<List<Tile>> rows, Tile[][] wall, List<Tile> floor) {
        this.id = id;
        this.rows = rows;
        this.wall = wall;
        this.floor = floor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<List<Tile>> getRows() {
        return rows;
    }

    public void setRows(List<List<Tile>> rows) {
        this.rows = rows;
    }

    public Tile[][] getWall() {
        return wall;
    }

    public void setWall(Tile[][] wall) {
        this.wall = wall;
    }

    public List<Tile> getFloor() {
        return floor;
    }

    public void setFloor(List<Tile> floor) {
        this.floor = floor;
    }
}
