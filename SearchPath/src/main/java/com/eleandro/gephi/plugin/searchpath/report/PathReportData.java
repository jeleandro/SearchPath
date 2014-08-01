package com.eleandro.gephi.plugin.searchpath.report;

import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class PathReportData {

    protected Node start;
    protected Node end;
    protected double minLC;
    protected double maxLC;
    protected double minNP;
    protected double maxNP;
    protected double minNodes;
    protected double maxNodes;

    public PathReportData(Node start, Node end, double minLC, double maxLC, double minNP, double maxNP, double minNodes, double maxNodes) {
        this.start = start;
        this.end = end;
        this.minLC = minLC;
        this.maxLC = maxLC;
        this.minNP = minNP;
        this.maxNP = maxNP;
        this.minNodes = minNodes;
        this.maxNodes = maxNodes;
    }

    public Object[] getArray() {
        return new Object[]{
                    start.getNodeData().getId(),
                    end.getNodeData().getId(),
                    minLC,
                    maxLC,
                    minNP,
                    maxNP,
                    minNodes,
                    maxNodes
                };
    }
}