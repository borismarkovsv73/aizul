package com.ftn.sbnz.model.models;

public class Tile {
    private Long id;
    private String color;

    public Tile() {
    }

    public Tile(Long id, String color) {
        this.id = id;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
