/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class GraphUtils {

    /**
     * Gera um CSV com os caminhos
     *
     * @param paths
     * @param statName nome da coluna do edge que contem a estatistica
     * @return
     */
    public static String getCSV(Map<String, Path> paths, String statName) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Path> entry : paths.entrySet()) {
            //pegando a coluna e  incrementando
            sb.append(entry.getKey()).append(";[");
            boolean first = true;
            for (Edge ee : entry.getValue().getEdges()) {
                if (first) {
                    sb.append(ee.getSource().getNodeData().getId());
                    first = false;
                }
                sb.append(',').append(ee.getTarget().getNodeData().getId());
            }
            sb.append(";").append(entry.getValue().getLinkCountTotal());
            sb.append("\r\n");

        }
        return sb.toString();
    }

    public static String getCSV(Path[] stats) {
        StringBuilder sb = new StringBuilder();
        for (Path s : stats) {
            //pegando a coluna e  incrementando
            sb.append(s.getName()).append(";[");
            boolean first = true;
            for (Edge ee : s.getEdges()) {
                if (first) {
                    sb.append(ee.getSource().getNodeData().getId());
                    first = false;
                }
                sb.append(',').append(ee.getTarget().getNodeData().getId());
            }
            sb.append(";").append(s.getLinkCountTotal());
            sb.append("\r\n");

        }
        return sb.toString();
    }

    /**
     * for each node if it has not a
     *
     * @param graph
     * @param cancelable
     * @return
     */
    public static List<Node> findStartNodes(Graph graph, CancelableLongTask cancelable) {
        ArrayList<Node> startNodes = new ArrayList<Node>();
        for (Node n : graph.getNodes()) {
            boolean isStart = true;
            for (Edge e : graph.getEdges(n)) {
                //todos os inícios nao podem ser Target
                if (e.getTarget().getNodeData().equals(n.getNodeData())) {
                    isStart = false;
                    break;
                }
            }

            if (isStart) {
                startNodes.add(n);
            }
            //se o usuário clicar em sair parar o algoritmo.
            if (cancelable.isCanceled()) {
                return new ArrayList<Node>();
            }
        }
        return startNodes;
    }

    public static ArrayList<Node> findEndNodes(Graph graph, CancelableLongTask cancelable) {
        ArrayList<Node> endNodes = new ArrayList<Node>();
        for (Node n : graph.getNodes()) {
            boolean isEnd = true;

            for (Edge e : graph.getEdges(n)) {
                //todos os inicios nao pode ser target
                if (e.getSource().getNodeData().equals(n.getNodeData())) {
                    isEnd = false;
                    break;
                }
            }

            if (isEnd) {
                endNodes.add(n);
            }

            //se o usuário clicar em sair parar o algoritmo.
            if (cancelable.isCanceled()) {
                return new ArrayList<Node>();
            }
        }
        return endNodes;
    }

    public static String edgeToString(Edge e) {
        return new StringBuilder("Source=").append(nodeToString(e.getSource())).append(" ").append("Target=").append(nodeToString(e.getTarget())).toString();
    }

    public static String nodeToString(Node n) {
        return n.getNodeData().getId();
    }

    public static void deletePath(AttributeModel attributeModel, String colName) {
        AttributeHelper.deleteAttribute(attributeModel.getEdgeTable(), colName);
        AttributeHelper.deleteAttribute(attributeModel.getNodeTable(), colName);
    }
}
