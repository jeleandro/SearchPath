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
public class DifferenceInAllAlgorithm extends DifferenceBetweenPathsAlgorithm{
    private float minDiff;
    
    public DifferenceInAllAlgorithm(float percMinimumDiffBetweenPaths){
        this.minDiff = percMinimumDiffBetweenPaths;
    }

    @Override
    public void execute(List<Path> paths) {
        float minimum = minDiff * 100;
        Progress.progress(getProgressTicket(), "Aplicando a diferença mínima", 1);

        ArrayList<Path> notPrioritized = new ArrayList<Path>();
        if (paths.size() > 2) {
         for (int i = 0; i < paths.size() - 1;i++) {
            for (int j = i+1; j < paths.size() - 1;) {
                double percent = DifferenceBetweenPathsAlgorithm.calculateDifference(paths.get(i), paths.get(j));
                if (percent < minimum) {
                    notPrioritized.add(paths.remove(j));
                    continue;//i is not incremented
                }else{
                    paths.get(j).setDifferenceToPreviousPath(percent);
                }
                j++;
                if (isCanceled()) {
                    break;
                }
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
