/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.cicles;

import javax.naming.OperationNotSupportedException;
import org.gephi.graph.api.Edge;

/**
 *
 * @author eleandro
 */
public class CicleException extends OperationNotSupportedException {
    private Edge edge;

    public CicleException(String explanation,Edge e) {
        super(explanation);
        this.edge = e;
        if(e==null){
            throw new NullPointerException("The edge representing the cicle can not be null.");
        }
    }
    
    public Edge getEdge() {
        return edge;
    }
    
}
