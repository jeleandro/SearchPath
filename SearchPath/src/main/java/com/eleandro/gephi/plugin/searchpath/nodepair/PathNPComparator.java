/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.nodepair;

import com.eleandro.gephi.plugin.searchpath.linkcount.PathLCComparator;
import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.Comparator;

/**
 *
 * @author eleandro
 */
public class PathNPComparator implements Comparator<Path>{
    private static final PathLCComparator INSTANCE = new PathLCComparator();
    
    public static final PathLCComparator getInstance(){
        return INSTANCE;
    }
    
    public int compare(Path o1, Path o) {
        o1.calculateStats();
        o.calculateStats();
        if(o1.getNodePairTotal() > o.getNodePairTotal()){
            return -1;
        } else if (o1.getLinkCountTotal() < o.getLinkCountTotal()) {
            return 1;
        } else {
            //desempatando
            if (o1.getLinkCountTotal() > o.getLinkCountTotal()) {
                return -1;
            }else if (o1.getEdges().size() > o.getEdges().size()) {
                return -1;
            } else if (o1.getStartNode().getId() > o.getStartNode().getId()) {
                return -1;
            }
        }
        return 0;
    }
    
}
