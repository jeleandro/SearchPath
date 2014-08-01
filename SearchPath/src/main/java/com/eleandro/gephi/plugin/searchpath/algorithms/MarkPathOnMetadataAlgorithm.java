/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.AttributeHelper;
import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.ColumnConstants;
import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.List;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author eleandro
 */
public class MarkPathOnMetadataAlgorithm implements PathAlgorithm, CancelableLongTask{
    private final AttributeModel attModel;
    private final int pathOnMetadata;
    private boolean cancel;
    private ProgressTicket progressTicket;
    
    public MarkPathOnMetadataAlgorithm(AttributeModel attModel, int pathOnMetadata){
        this.attModel =attModel;
        this.pathOnMetadata = pathOnMetadata;
    }
    
    public boolean cancel() {
        this.cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket =progressTicket;
    }

    public boolean isCanceled() {
        return cancel;
    }
    
    public void execute(List<Path> paths) {
        int pathNumber = Math.min(pathOnMetadata, paths.size());

        String baseCol = ColumnConstants.SPLC_PATH;
        for (int i = 1; i <= pathNumber; i++) {
            AttributeHelper.createDouble(attModel.getEdgeTable(), baseCol + i);
            AttributeHelper.createDouble(attModel.getNodeTable(), baseCol + i);
        }

        final Double one = 1D;


        for (int i = 0; i < pathNumber; i++) {
            List<Edge> ee = paths.get(i).getEdges();
            for (Edge e : ee) {
                //marcando os nós e arestas como 1
                e.getAttributes().setValue(baseCol + (i + 1), one);
                e.getSource().getAttributes().setValue(baseCol + (i + 1), one);
                e.getTarget().getAttributes().setValue(baseCol + (i + 1), one);
            }
            if (isCanceled()) {
                return;
            }
        }
    }
    
}
