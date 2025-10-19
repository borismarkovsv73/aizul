package com.ftn.sbnz.kjar.templates;

public class RowPriorityTemplateProcessor {
    
    public static class RowPriorityData {
        private int rowNumber;
        private int points;
        private String ruleName;
        
        public RowPriorityData(int rowNumber, int points, String ruleName) {
            this.rowNumber = rowNumber;
            this.points = points;
            this.ruleName = ruleName;
        }
        
        public int getRowNumber() { return rowNumber; }
        public int getPoints() { return points; }
        public String getRuleName() { return ruleName; }
    }
    
    public static class RowCompletionData {
        private int rowIndex;
        private int rowSize;
        private int baseScore;
        private int bonusScore;
        private String ruleName;
        
        public RowCompletionData(int rowIndex, int rowSize, int baseScore, int bonusScore, String ruleName) {
            this.rowIndex = rowIndex;
            this.rowSize = rowSize;
            this.baseScore = baseScore;
            this.bonusScore = bonusScore;
            this.ruleName = ruleName;
        }
        
        public int getRowIndex() { return rowIndex; }
        public int getRowSize() { return rowSize; }
        public int getBaseScore() { return baseScore; }
        public int getBonusScore() { return bonusScore; }
        public String getRuleName() { return ruleName; }
    }
}