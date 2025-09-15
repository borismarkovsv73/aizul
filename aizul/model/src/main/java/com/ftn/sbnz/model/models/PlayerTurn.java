package com.ftn.sbnz.model.models;

import java.util.List;

public class PlayerTurn {
    private Long id;
    private Long playerId;
    private Board newBoardState;
    private List<Factory> factoriesState;
    private Move move;

    public PlayerTurn() {
    }

    public PlayerTurn(Long id, Long playerId, Board newBoardState, List<Factory> factoriesState, Move move) {
        this.id = id;
        this.playerId = playerId;
        this.newBoardState = newBoardState;
        this.factoriesState = factoriesState;
        this.move = move;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Board getNewBoardState() {
        return newBoardState;
    }

    public void setNewBoardState(Board newBoardState) {
        this.newBoardState = newBoardState;
    }

    public List<Factory> getFactoriesState() {
        return factoriesState;
    }

    public void setFactoriesState(List<Factory> factoriesState) {
        this.factoriesState = factoriesState;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
