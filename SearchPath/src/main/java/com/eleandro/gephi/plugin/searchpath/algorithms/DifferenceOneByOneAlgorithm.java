/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.ArrayList;
import java.util.List;
import org.gephi.utils.progress.Progress;

/**
 *
 * @author eleandro
 */
public class DifferenceOneByOneAlgorithm extends DifferenceBetweenPathsAlgorithm{
    private float minDiff;
    
    public DifferenceOneByOneAlgorithm(float percMinimumDiffBetweenPaths){
        this.minDiff =percMinimumDiffBetweenPaths;
    }

    @Override
    public void execute(List<Path> paths) {
        float minimum = minDiff * 100;
        Progress.progress(getProgressTicket(), "Aplicando a diferença mínima", 1);

        ArrayList<Path> notPrioritized = new ArrayList<Path>();
        if (paths.size() > 2) {
            for (int i = 0; i < paths.size() - 1;) {
                double percent = DifferenceBetweenPathsAlgorithm.calculateDifference(paths.get(i), paths.get(i + 1));
                if (percent < minimum) {
                    notPrioritized.add(paths.remove(i + 1));
                    continue;//i is not incremented
                }else{
                    paths.get(i+1).setDifferenceToPreviousPath(percent);
                }
                i++;
                if (isCanceled()) {
                    break;
                }
            }
        }

        //calculating the border
        if (notPrioritized.size() > 0) {
            double percent = DifferenceBetweenPathsAlgorithm.calculateDifference(
                    paths.get(paths.size() - 1),
                    notPrioritized.get(0));
            notPrioritized.get(0).setDifferenceToPreviousPath(percent);
        }
        
        //joining
        paths.addAll(notPrioritized);
    }
}
