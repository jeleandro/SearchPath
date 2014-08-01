package com.eleandro.gephi.plugin.searchpath;

import com.eleandro.gephi.plugin.searchpath.linkcount.PathLCComparator;
import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

/**
 *
 *
 *
 * @author eleandro
 */
public class Path implements Comparable<Path> {

    private String name;
    private List<Edge> edges;
    private double linkCountTotal;
    private double nodePairTotal;
    private double differenceToPreviousPath;
    private int relevance;

    public Path(String name) {
        this.name = name;
        this.edges = new ArrayList<Edge>();
    }

    public Path(String name, Path old) {
        this(name);
        edges.addAll(old.getEdges());
    }

    public double getDifferenceToPreviousPath() {
        return differenceToPreviousPath;
    }

    public void setDifferenceToPreviousPath(double differenceToPreviousPath) {
        this.differenceToPreviousPath = differenceToPreviousPath;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean add(Edge e) {
        return edges.add(e);
    }

    public boolean contains(Edge o) {
        return edges.contains(o);
    }

    public double getLinkCountTotal() {
        if (linkCountTotal == 0) {
            this.linkCountTotal = sumStats(ColumnConstants.LINK_COUNT_NAME);
        }
        return linkCountTotal;
    }

    public double getNodePairTotal() {
        if (nodePairTotal == 0) {
            this.nodePairTotal = sumStats(ColumnConstants.NODE_PAIR_NAME);
        }
        return nodePairTotal;
    }

    public void calculateStats() {
        this.nodePairTotal = sumStats(ColumnConstants.NODE_PAIR_NAME);
        this.linkCountTotal = sumStats(ColumnConstants.LINK_COUNT_NAME);
    }

    public Node getStartNode() {
        return edges.get(0).getSource();
    }

    public Node getEndNode() {
        return edges.get(edges.size() - 1).getTarget();
    }

    @Override
    public int compareTo(Path o) {
        return PathLCComparator.getInstance().compare(this, o);
    }

    private double sumStats(String statName) {
        int stat = 0;
        if (edges.get(0).getAttributes().getValue(statName) == null) {
            return 0;
        }
        for (Edge ee : this.edges) {
            Attributes attributes = ee.getAttributes();
            Double i = ((Number) attributes.getValue(statName)).doubleValue();
            if (i != null) {
                stat += i;
            }
        }
        return stat;
    }

    public int getNodeCount() {
        return edges.size() + 1;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }
    
    public List<Node> nodes(){
        ArrayList<Node> nodes = new ArrayList<Node>(edges.size()+2);
        for(Edge e:edges){
            nodes.add(e.getSource());
        }
        nodes.add(edges.get(edges.size()-1).getTarget());
        return nodes;
    }
}