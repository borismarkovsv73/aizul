package com.ftn.sbnz.model.strategy;

/**
 * Represents a strategic goal in backward chaining
 * Corresponds to declare StrategicGoal in Drools rules
 */
public class StrategicGoal {
    
    private String type; // "ROW_COMPLETION", "COLUMN_COMPLETION", "COLOR_COMPLETION"
    private int targetRow;
    private int targetCol;
    private int priority;
    private boolean achievable;
    
    public StrategicGoal() {}
    
    public StrategicGoal(String type, int targetRow, int targetCol, int priority, boolean achievable) {
        this.type = type;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
        this.priority = priority;
        this.achievable = achievable;
    }
    
    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getTargetRow() { return targetRow; }
    public void setTargetRow(int targetRow) { this.targetRow = targetRow; }
    
    public int getTargetCol() { return targetCol; }
    public void setTargetCol(int targetCol) { this.targetCol = targetCol; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean isAchievable() { return achievable; }
    public void setAchievable(boolean achievable) { this.achievable = achievable; }
    
    @Override
    public String toString() {
        return "StrategicGoal{" +
                "type='" + type + '\'' +
                ", targetRow=" + targetRow +
                ", targetCol=" + targetCol +
                ", priority=" + priority +
                ", achievable=" + achievable +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrategicGoal)) return false;
        StrategicGoal that = (StrategicGoal) o;
        return targetRow == that.targetRow &&
                targetCol == that.targetCol &&
                priority == that.priority &&
                achievable == that.achievable &&
                type.equals(that.type);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, targetRow, targetCol, priority, achievable);
    }
}