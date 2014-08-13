/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.nodepair;

import com.eleandro.gephi.plugin.searchpath.Path;
import com.eleandro.gephi.plugin.searchpath.linkcount.PathLCComparator;
import java.util.Comparator;

/**
 *
 * @author eleandro
 */
public class PathNPComparator implements Comparator<Path> {

    private static final PathLCComparator INSTANCE = new PathLCComparator();

    public static final PathLCComparator getInstance() {
        return INSTANCE;
    }

    @Override
     public int compare(Path o1, Path o) {
        o1.calculateStats();
        o.calculateStats();

        int result = (int)Math.round(o.getNodePairTotal() - o1.getNodePairTotal());

        if (result == 0) {
            result = o.getEdges().size() - o1.getEdges().size();
            if (result == 0) {
                result = o.getStartNode().getId() - o1.getStartNode().getId();
            }
        }

        return  result;
    }
}
