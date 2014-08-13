/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eleandro.gephi.plugin.searchpath;

import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class MetadataUtils {
    public static Number getNumber(Node n,String attribute){
        return (Number)n.getAttributes().getValue(attribute);
    }
    
    public static double getDouble(Node n,String attribute){
        return getNumber(n, attribute).doubleValue();
    }
    
}
