/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;


/**
 *
 * @author eleandro
 */
public class SearchPathConfig {
    private String weightField;
    private float percDiffMinimunPath;
    private boolean ignoreSPLConDiffFilter;
    private int numberOfPathsOnReport;
    private int numberOfPathsOnMetaData;
    private int numberOfPathToBeMerged;
    private boolean showStartAndEnd;
    private DifferenceType differenceType = DifferenceType.ONE_ALL_ONE;
    

    public SearchPathConfig() {
    }

    public int getNumberOfPathToBeMerged() {
        return numberOfPathToBeMerged;
    }

    public SearchPathConfig setAmountOfPathToBeMerged(int amountOfPathToBeMerged) {
        this.numberOfPathToBeMerged = Math.max(0,amountOfPathToBeMerged);
        return this;
    }

    public int getNumberOfPathsOnMetaData() {
        return numberOfPathsOnMetaData;
    }

    public SearchPathConfig setNumberOfPathsOnMetaData(int numberOfPathsOnMetaData) {
        this.numberOfPathsOnMetaData = Math.max(0,numberOfPathsOnMetaData);
        return this;
    }

    public int getNumberOfPathsOnReport() {
        return numberOfPathsOnReport;
    }

    public SearchPathConfig setNumberOfPathsOnReport(int NumberOfPathsOnReport) {
        this.numberOfPathsOnReport = Math.max(0,NumberOfPathsOnReport);
        return this;
    }

    public float getPercDiffMinimunPath() {
        return percDiffMinimunPath;
    }

    public SearchPathConfig setPercDiffMinimunPath(float percDiffMinimunPath) {
        this.percDiffMinimunPath = Math.min(Math.max(0,percDiffMinimunPath),1);
        return this;
    }

    public boolean isShowStartAndEnd() {
        return showStartAndEnd;
    }

    public SearchPathConfig setShowStartAndEnd(boolean showStartAndEnd) {
        this.showStartAndEnd = showStartAndEnd;
        return this;
    }

    public String getWeightField() {
        return weightField;
    }

    public SearchPathConfig setWeightField(String weightField) {
        this.weightField = weightField;
        return this;
    }

    public boolean isIgnoreSPLConDiffFilter() {
        return ignoreSPLConDiffFilter;
    }

    public SearchPathConfig setIgnoreSPLConDiffFilter(boolean ignoreSPLConDiffFilter) {
        this.ignoreSPLConDiffFilter = ignoreSPLConDiffFilter;
        return this;
    }

    public DifferenceType getDifferenceType() {
        return differenceType;
    }

    public SearchPathConfig setDifferenceType(DifferenceType differenceType) {
        this.differenceType = differenceType;
        return this;
    }
    
    
}
