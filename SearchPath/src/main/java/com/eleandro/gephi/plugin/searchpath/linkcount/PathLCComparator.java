/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.linkcount;

import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.Comparator;

/**
 *
 * @author eleandro
 */
public class PathLCComparator implements Comparator<Path> {

    private static final PathLCComparator INSTANCE = new PathLCComparator();

    public static final PathLCComparator getInstance() {
        return INSTANCE;
    }

    //reverse order
    public int compare(Path o1, Path o) {
        o1.calculateStats();
        o.calculateStats();

        double result = o.getLinkCountTotal() - o1.getLinkCountTotal();

        if (result == 0) {
            result = o.getNodePairTotal() - o1.getNodePairTotal();
            if (result == 0) {
                result = o.getEdges().size() - o1.getEdges().size();
                if (result == 0) {
                    result = o.getStartNode().getId() - o1.getStartNode().getId();

                }
            }
        }




        return (int) Math.round(result);
    }
}
