package com.ftn.sbnz.service.templates;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

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
    
    public String generateRulesFromTemplate() {
        try {
            InputStream templateStream = getClass().getResourceAsStream("/templates/row_priority.drt");
            
            if (templateStream == null) {
                System.err.println("Template file not found: /templates/row_priority.drt");
                java.nio.file.Path templatePath = java.nio.file.Paths.get(
                    "../kjar/src/main/resources/templates/row_priority.drt"
                );
                if (java.nio.file.Files.exists(templatePath)) {
                    templateStream = java.nio.file.Files.newInputStream(templatePath);
                    System.out.println("Found template at: " + templatePath.toAbsolutePath());
                } else {
                    System.err.println("Template file not found at: " + templatePath.toAbsolutePath());
                    return null;
                }
            }
            
            Collection<RowPriorityData> data = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                data.add(new RowPriorityData(i, i + 1, "Row " + (i + 1) + " Priority"));
            }
            
            ObjectDataCompiler compiler = new ObjectDataCompiler();
            String generatedRules = compiler.compile(data, templateStream);
            
            return generatedRules;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String generateRowCompletionRulesFromTemplate() {
        try {
            InputStream templateStream = getClass().getResourceAsStream("/templates/row_completion.drt");
            
            if (templateStream == null) {
                System.err.println("Template file not found: /templates/row_completion.drt");
                java.nio.file.Path templatePath = java.nio.file.Paths.get(
                    "../kjar/src/main/resources/templates/row_completion.drt"
                );
                if (java.nio.file.Files.exists(templatePath)) {
                    templateStream = java.nio.file.Files.newInputStream(templatePath);
                    System.out.println("Found template at: " + templatePath.toAbsolutePath());
                } else {
                    System.err.println("Template file not found at: " + templatePath.toAbsolutePath());
                    return null;
                }
            }
            
            Collection<RowCompletionData> data = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int rowSize = i + 1;
                int baseScore = 5;
                int bonusScore = baseScore + (rowSize * 2);
                data.add(new RowCompletionData(i, rowSize, baseScore, bonusScore, "Row " + rowSize + " Completion"));
            }
            
            ObjectDataCompiler compiler = new ObjectDataCompiler();
            String generatedRules = compiler.compile(data, templateStream);
            
            return generatedRules;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public KieSession createDynamicTemplateSession() {
        try {
            String priorityRules = generateRulesFromTemplate();
            String completionRules = generateRowCompletionRulesFromTemplate();
            
            if (priorityRules == null || completionRules == null) {
                System.err.println("Failed to generate template rules");
                return null;
            }
            
            
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kfs = kieServices.newKieFileSystem();
            
            // Add both generated rules to KieFileSystem
            kfs.write("src/main/resources/dynamic_priority_rules.drl", priorityRules);
            kfs.write("src/main/resources/dynamic_completion_rules.drl", completionRules);
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                System.err.println("Build errors: " + kieBuilder.getResults().toString());
                return null;
            }
            
            KieContainer dynamicContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
            return dynamicContainer.newKieSession();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

}