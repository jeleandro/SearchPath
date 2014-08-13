/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.AttributeHelper;
import com.eleandro.gephi.plugin.searchpath.ColumnConstants;
import com.eleandro.gephi.plugin.searchpath.GraphUtils;
import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.List;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author eleandro
 */
public class MergeColumnMetaDataAlgorithm implements PathAlgorithm, LongTask{
    private ProgressTicket progressTicket;
    private boolean cancel;
    private final AttributeModel attModel;
    private int pathToBeMerged;
    
    public MergeColumnMetaDataAlgorithm(AttributeModel attModel, int pathToBeMerged){
        this.attModel =attModel;
        this.pathToBeMerged = pathToBeMerged;
    }
    
    public boolean cancel() {
        this.cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket =progressTicket;
    }
    
    public void execute(List<Path> paths) {
        if (pathToBeMerged <= 0) {
            return;
        }
        GraphUtils.deletePath(attModel, ColumnConstants.SPLC_PATH_MERGE);
        AttributeHelper.createDouble(attModel.getNodeTable(), ColumnConstants.SPLC_PATH_MERGE);
        AttributeHelper.createDouble(attModel.getEdgeTable(), ColumnConstants.SPLC_PATH_MERGE);

        String baseCol = ColumnConstants.SPLC_PATH_MERGE;
        int merge = Math.min(pathToBeMerged, paths.size());
        
        final Double one =  1D;

        for (int i = 0; i < merge; i++) {
            List<Edge> ee = paths.get(i).getEdges();
            for (Edge e : ee) {
                //marcando os nós e arestas como 1
                e.getAttributes().setValue(baseCol, one);
                e.getSource().getAttributes().setValue(baseCol, one);
                e.getTarget().getAttributes().setValue(baseCol, one);
            }
            if (cancel) {
                return;
            }
        }
    }

    
}
