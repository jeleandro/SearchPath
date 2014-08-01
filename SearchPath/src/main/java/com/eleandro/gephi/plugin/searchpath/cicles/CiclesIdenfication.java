/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.cicles;

import com.eleandro.gephi.plugin.searchpath.AttributeHelper;
import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.ColumnConstants;
import com.eleandro.gephi.plugin.searchpath.GraphUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 * 
 * @author José Eleandro Custódio
 * @version 1.0
 */
public class CiclesIdenfication implements Statistics, CancelableLongTask, LongTask {
    private static final Double NOT_CICLE = Double.valueOf(-1);
    private static final Double IS_CICLE = Double.valueOf(1);
    
    private List<String> messages;
    private boolean cancel = false;

    private ProgressTicket progress;
    
    private long countCicles = 0;    
    private float cicleRate;

    public boolean isCanceled() {
        return cancel;
    }

    private void identifyAndMarkCicles(Graph g, AttributeModel attModel) {
        countCicles = 0;
        AttributeColumn att = AttributeHelper.createDouble(attModel.getEdgeTable(), ColumnConstants.CICLE, NOT_CICLE);
        int attEdgeIndex = att.getIndex();
        
        att = AttributeHelper.createDouble(attModel.getNodeTable(), ColumnConstants.CICLE, NOT_CICLE);
        int attNodeIndex = att.getIndex();

        HashSet<Node> nodeStack = new HashSet<Node>();
        List<Node> startNodes = GraphUtils.findStartNodes(g, this);

        if (startNodes.isEmpty()) {
            messages.add("O grafo possui nós início.");
        }

        for (Node n : startNodes) {
            nodeStack.add(n);
            deepCicleSearch(g, nodeStack, n, attEdgeIndex, attNodeIndex);
            if (isCanceled()) {
                break;
            }
        }
        
        //marcando os nós das arestas que sao um ciclo como ciclo
        for(Edge e:g.getEdges()){
            if(e.getAttributes().getValue(attEdgeIndex).equals(IS_CICLE)){
                countCicles++;
                AttributeHelper.setAttValue(e.getSource(), attNodeIndex, IS_CICLE);
                AttributeHelper.setAttValue(e.getTarget(), attNodeIndex, IS_CICLE);
            }
        }
        
    }

    private void deepCicleSearch(Graph g, HashSet<Node> nodeStack, Node n, int attEdgeIndex, int attNodeIndex) {
        nodeStack.add(n);
        for (Edge e : g.getEdges(n)) {
            //se nao for loop e n for o source teste o target
            if (!e.isSelfLoop() && e.getSource().equals(n)) {
                if (nodeStack.contains(e.getTarget())) {
                    //cicle detected stop the processing this branch
                    AttributeHelper.setAttValue(e, attEdgeIndex, IS_CICLE);
                } else {
                    deepCicleSearch(g, nodeStack, e.getTarget(), attEdgeIndex, attNodeIndex);
                }
            }
            if (isCanceled()) {
                break;
            }
        }
        nodeStack.remove(n);
    }


     /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        messages = new ArrayList<String>();

        // Get the graph instance
        Graph g = graphModel.getHierarchicalDirectedGraphVisible();

        if (g == null) {
            messages.add("Projeto não inicializado.");
        }
        try {
            g.readLock();
            identifyAndMarkCicles(g, attributeModel);
            cicleRate = ((float)countCicles)/(float)g.getEdgeCount();
        }catch(Exception e){
            messages.add(e.getStackTrace().toString());
        } finally {
            g.readUnlockAll();
        }
    }

    public String getReport() {
        if(messages.size() > 0){
            return messages.toString();
        }else{
            return null;
        }
    }

    public float getCicleRate() {
        return cicleRate;
    }

    public boolean cancel() {
         cancel = true;
         return true;
    }

}
