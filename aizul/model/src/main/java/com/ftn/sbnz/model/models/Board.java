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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board{id=").append(id);
        sb.append(", rows=").append(rows);
        sb.append(", wall=");
        if (wall != null) {
            sb.append("[");
            for (int i = 0; i < wall.length; i++) {
                sb.append("[");
                for (int j = 0; j < wall[i].length; j++) {
                    sb.append(wall[i][j] != null ? wall[i][j].getColor() : "null");
                    if (j < wall[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < wall.length - 1) sb.append(",");
            }
            sb.append("]");
        } else {
            sb.append("null");
        }
        sb.append(", floor=").append(floor);
        sb.append("}");
        return sb.toString();
    }
}
