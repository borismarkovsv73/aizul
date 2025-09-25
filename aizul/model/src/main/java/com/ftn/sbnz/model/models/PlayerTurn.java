package com.ftn.sbnz.model.models;

import java.util.List;

public class PlayerTurn {
    private Long id;
    private Long playerId;
    private Board newBoardState;
    private List<Factory> factoriesState;
    private Move move;
    private String explanation;


    public PlayerTurn() {
    }

    public PlayerTurn(Long id, Long playerId, Board newBoardState, List<Factory> factoriesState, Move move, String explanation) {
        this.id = id;
        this.playerId = playerId;
        this.newBoardState = newBoardState;
        this.factoriesState = factoriesState;
        this.move = move;
        this.explanation = explanation;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "PlayerTurn{id=" + id + 
               ", playerId=" + playerId + 
               ", newBoardState=" + newBoardState + 
               ", factoriesState=" + factoriesState + 
               ", move=" + move + 
               ", explanation='" + explanation + "'}";
    }
}
