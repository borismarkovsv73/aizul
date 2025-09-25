package com.ftn.sbnz.model.strategy;

import com.ftn.sbnz.model.models.Move;

/**
 * Represents the justification for why a move achieves a strategic goal
 * Used in backward chaining to link goals with moves
 */
public class GoalJustification {
    
    private StrategicGoal goal;
    private Move move;
    private String reasoning;
    
    public GoalJustification() {}
    
    public GoalJustification(StrategicGoal goal, Move move, String reasoning) {
        this.goal = goal;
        this.move = move;
        this.reasoning = reasoning;
    }
    
    // Getters and setters
    public StrategicGoal getGoal() { return goal; }
    public void setGoal(StrategicGoal goal) { this.goal = goal; }
    
    public Move getMove() { return move; }
    public void setMove(Move move) { this.move = move; }
    
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    
    @Override
    public String toString() {
        return "GoalJustification{" +
                "goal=" + goal +
                ", move=" + move +
                ", reasoning='" + reasoning + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoalJustification)) return false;
        GoalJustification that = (GoalJustification) o;
        return goal.equals(that.goal) &&
                move.equals(that.move) &&
                reasoning.equals(that.reasoning);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(goal, move, reasoning);
    }
}