package com.ftn.sbnz.model.models;

import java.util.List;

public class GameState {
    private Long id;
    private List<Player> players;
    private List<Factory> factories;
    private List<Tile> bag;
    private int roundNumber;

    public GameState() {
    }

    public GameState(Long id, List<Player> players, List<Factory> factories, List<Tile> bag, int roundNumber) {
        this.id = id;
        this.players = players;
        this.factories = factories;
        this.bag = bag;
        this.roundNumber = roundNumber;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    public List<Factory> getFactories() {
        return factories;
    }
    public void setFactories(List<Factory> factories) {
        this.factories = factories;
    }
    public List<Tile> getBag() {
        return bag;
    }
    public void setBag(List<Tile> bag) {
        this.bag = bag;
    }
    public int getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public String toString() {
        return "GameState{id=" + id + 
               ", players=" + players + 
               ", factories=" + factories + 
               ", bag=" + bag + 
               ", roundNumber=" + roundNumber + "}";
    }
}
