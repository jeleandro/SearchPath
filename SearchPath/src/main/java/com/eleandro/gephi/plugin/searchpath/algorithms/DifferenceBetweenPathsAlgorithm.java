/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.gephi.graph.api.Edge;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author eleandro
 */
public class DifferenceBetweenPathsAlgorithm implements CancelableLongTask, PathAlgorithm{
    private boolean cancel;
    private ProgressTicket progressTicket;
    /**
     *
     * @param path1
     * @param path2
     * @return (count nodes this + count node that)/(size this + size that)
     */
    public static double calculateDifference(Path path1, Path path2) {
        if (path2 == null || path2.getEdges() == null || path2.getEdges().isEmpty()) {
            return 1D;
        }
        HashSet<String> labelsThis = new HashSet<String>();
        for (Edge e : path1.getEdges()) {
            labelsThis.add(e.getSource().getNodeData().getId());
            labelsThis.add(e.getTarget().getNodeData().getId());
        }


        HashSet<String> labelsThat = new HashSet<String>();
        for (Edge e : path2.getEdges()) {
            labelsThat.add(e.getSource().getNodeData().getId());
            labelsThat.add(e.getTarget().getNodeData().getId());
        }

        ArrayList<String> dif1 = new ArrayList<String>(labelsThis);
        dif1.removeAll(labelsThat);

        ArrayList<String> dif2 = new ArrayList<String>(labelsThat);
        dif2.removeAll(labelsThis);

        double total = labelsThis.size() + labelsThat.size();
        double different = dif1.size()+dif2.size();
        return 100D*different/total;
    }


    public void execute(List<Path> paths) {
        if (paths != null && paths.size() >= 2) {
            for (int i = 0; i < paths.size() - 1; i++) {
                double diff = calculateDifference(paths.get(i), paths.get(i + 1));
                paths.get(i + 1).setDifferenceToPreviousPath(diff);
            }
        }
    }

    public boolean isCanceled() {
        return cancel;
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket= progressTicket;
    }
    public ProgressTicket getProgressTicket(){
        return progressTicket;
    }
}
