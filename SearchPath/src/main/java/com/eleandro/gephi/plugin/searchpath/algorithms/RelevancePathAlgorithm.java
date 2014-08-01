/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author eleandro
 */
public class RelevancePathAlgorithm implements PathAlgorithm, CancelableLongTask {

    private Comparator<Path> comparator;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    public RelevancePathAlgorithm(Comparator<Path> comp) {
        this.comparator = comp;
    }

    public void execute(List<Path> paths) {
        Collections.sort(paths, comparator);
        double lastStast = Double.NaN;
        int relevance = 0;
        double current;
        for (Path p : paths) {
            current = p.getLinkCountTotal();
            if (current != lastStast) {
                relevance++;
            }
            p.setRelevance(relevance);
            lastStast = current;
            if (cancel) {
                return;
            }
        }
    }

    public boolean cancel() {
        this.cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    public boolean isCanceled() {
        return cancel;
    }
}
