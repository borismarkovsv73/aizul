package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class Player implements Serializable{
    private Long id;
    private Board board;
    private int score;

    public Player() {
    }

    public Player(Long id, Board board, int score) {
        this.id = id;
        this.board = board;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Player{id=" + id + 
               ", board=" + board + 
               ", score=" + score + "}";
    }
}
