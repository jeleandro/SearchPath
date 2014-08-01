/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.algorithms;

import com.eleandro.gephi.plugin.searchpath.Path;
import java.util.List;

/**
 *
 * @author eleandro
 */
public interface PathAlgorithm {
    public void execute(List<Path> paths);
    
}
