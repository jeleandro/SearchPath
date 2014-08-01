/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import com.eleandro.gephi.plugin.searchpath.algorithms.PathAlgorithm;
import com.eleandro.gephi.plugin.searchpath.cicles.CicleException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author eleandro
 */
public class PathStatistics implements CancelableLongTask{

    private HashMap<String, Path> indexes = new HashMap<String, Path>();
    private List<Path> pathAsList = new ArrayList<Path>();
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    public PathStatistics() {
    }

    public Map<String, Path> getPaths() {
        return indexes;
    }
    
    public List<Path> getPathsAsList(){
        return new ArrayList(pathAsList);
    }
    
    /**
     * create paths starting with startNodes
     * @param graph
     * @param startNodes
     * @throws CicleException 
     */
    public void generatePaths(Graph graph, List<Node> startNodes) throws CicleException {
        cancel = false;
        for (Node n : startNodes) {
            if(isCanceled()){return;}
            //the name the start node id
            String pathName = "" + n.getId();
            indexes.put(pathName, new Path(pathName));
            generatePath(graph, n, pathName);
        }
        this.pathAsList.addAll(indexes.values());
    }
    
    /**
     * generate add nodes to the pathName.
     * Split the path if it is necessary.
     * @param graph
     * @param currentNode
     * @param pathName
     * @throws CicleException 
     */
    private void generatePath(Graph graph, Node currentNode, String pathName) throws CicleException {
        Path originalPath;
        Edge[] edges = graph.getEdges(currentNode).toArray();
        
        //filtrando apenas arestas que são arestas onde currentNode é inicio
        ArrayList<Edge> temp = new ArrayList<Edge>(edges.length);
        for(Edge e:edges){
            if(isSourceNode(edges[0], currentNode)){
                temp.add(e);
            }
        }
        if(temp.isEmpty() ){
            return;
        }
        edges = temp.toArray(new Edge[temp.size()]);
        
        
        //current node has only one edge.
        if (edges.length == 1) {
            Node next = edges[0].getTarget();
            
            
            originalPath = indexes.get(pathName);
            if (originalPath.contains(edges[0])) {
                throw new CicleException("Grafos com ciclos não podem ser analisados." ,edges[0]);
            }

            originalPath.add(edges[0]);
            generatePath(graph, next, pathName);
        } else {
            //split the path in many others
            originalPath = indexes.remove(pathName);
            int i = 1;
            for (Edge e : edges) {
                if (isSourceNode(e, currentNode) && !e.isSelfLoop()) {
                    //if the path already contains the edge so it is a cicle
                    if (originalPath.contains(e)) {
                        throw new CicleException("Grafos com ciclos não podem ser analisados.",e);
                    }

                    String newPathName = pathName + i;
                    Path newPath = new Path(newPathName, originalPath);
                    newPath.add(e);
                    indexes.put(newPathName, newPath);
                    //recursively generate.
                    generatePath(graph, e.getTarget(), newPathName);
                    i++;
                    if (cancel) {
                        return;
                    }
                }
            }

            if (i == 1 && originalPath.getEdges().size() > 0) {
                //nenhum subcaminho elegível foi encontrado
                indexes.put(pathName, originalPath);
            }
        }
    }
    
    private boolean isSourceNode(Edge edges, Node node) {
        return edges.getSource().getNodeData().equals(node.getNodeData());
    }

    public boolean isCanceled() {
        return cancel;
    }

    public boolean cancel() {
       cancel =true;
       return true;
    }
    
    public int size(){
        return pathAsList.size();
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
    
    public void apply(PathAlgorithm algoritm){
        if(algoritm instanceof CancelableLongTask){
            ((CancelableLongTask)algoritm).setProgressTicket(progressTicket);
        }
        algoritm.execute(pathAsList);
    }
}
