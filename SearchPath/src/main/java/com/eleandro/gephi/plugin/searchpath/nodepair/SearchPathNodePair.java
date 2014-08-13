/*
 * Your license here
 */
package com.eleandro.gephi.plugin.searchpath.nodepair;

import com.eleandro.gephi.plugin.searchpath.*;
import com.eleandro.gephi.plugin.searchpath.cicles.CicleException;
import com.eleandro.gephi.plugin.searchpath.report.HtmlTable;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.graph.api.*;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * See http://wiki.gephi.org/index.php/HowTo_write_a_metric#Create_Statistics
 *
 * @author José Eleandro Custódio
 */
public class SearchPathNodePair implements Statistics, LongTask, CancelableLongTask {

    private static final Double ZERO = 0D;
    private static final Double ONE = 1D;
    private static final Double M_ONE = -1D;
    private boolean cancel = false;
    private ProgressTicket progressTicket;
    //variáveis de sessao, limpar apos uso!!!
    private List<String> errors = new LinkedList<String>();
    private List<String> reportList = new LinkedList<String>();
    private float avgNodePair = 0;
    private List<Node> startNodes;
    private List<Node> endNodes;
    private Map<String, Path> paths;
    private SearchPathConfig config= new SearchPathConfig();

    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        cancel = false;
        Graph graph = graphModel.getGraphVisible();
        graph.readLock();

        if (graph instanceof DirectedGraph) {
            graph = graph.getGraphModel().getHierarchicalDirectedGraphVisible();
        }else{
            return;
        }

        errors.clear();
        reportList.clear();

        try {
            Progress.start(progressTicket, graph.getNodeCount() * 3);
            Progress.progress(progressTicket);

            //Marcando nós inicio e nós fim
            findStartEndAnd(graph);

            if (errors.size() > 0) {
                return;
            }

            deletePath(attributeModel, ColumnConstants.SPNP_PATH);
            deletePath(attributeModel, ColumnConstants.SPNP_PATH_MERGE);

            AttributeHelper.createDouble(attributeModel.getEdgeTable(), ColumnConstants.NODE_PAIR_NAME);
            AttributeHelper.createDouble(attributeModel.getNodeTable(), ColumnConstants.NP_FROM_TO);
            AttributeHelper.createDouble(attributeModel.getNodeTable(), ColumnConstants.NP_TO_FROM);

            //criando o nodePair
            calculateNodePair(graph);

            if (errors.size() > 0) {
                return;
            }

            //cria todos os caminho
            PathStatistics pathFactory = new PathStatistics();
            pathFactory.generatePaths(graph, startNodes);
            paths = pathFactory.getPaths();

            //cria as colunas marcando os caminhos
            markPaths(attributeModel, paths, config.getNumberOfPathToBeMerged());
            markMergeColumn(paths);

            //marcando inicio e fim
            createColumnsStartAndEnd(attributeModel);

            graph.readUnlockAll();
        } catch (CicleException e) {
            errors.add(createCicleError(e));
        } catch (OperationNotSupportedException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("Erro desconhecido:" + e.getMessage());
        } finally {
            //Unlock graph
            graph.readUnlockAll();
        }
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    private void createColumnsStartAndEnd(AttributeModel attributeModel) {
        AttributeColumn ref = AttributeHelper.createDouble(attributeModel.getNodeTable(), ColumnConstants.PATH_REF);

        int index = ref.getIndex();

        for (Node n : startNodes) {
            n.getAttributes().setValue(index, M_ONE);
        }

        for (Node n : endNodes) {
            n.getAttributes().setValue(index, ONE);
        }
    }

    private void markPaths(AttributeModel attributeModel, Map<String, Path> paths, int pathNumber) {
        Path[] arr = listPaths(new ArrayList<Path>(paths.values()), pathNumber);


        String baseCol = ColumnConstants.SPNP_PATH;
        for (int i = 1; i < arr.length; i++) {
            AttributeHelper.createDouble(attributeModel.getEdgeTable(), baseCol + i);
            AttributeHelper.createDouble(attributeModel.getNodeTable(), baseCol + i);
        }

        for (int i = 0; i < arr.length; i++) {
            List<Edge> ee = arr[i].getEdges();
            for (Edge e : ee) {
                //marcando os nós e arestas como 1
                e.getAttributes().setValue(baseCol + (i + 1), ONE);
                e.getSource().getAttributes().setValue(baseCol + (i + 1), ONE);
                e.getTarget().getAttributes().setValue(baseCol + (i + 1), ONE);
            }

        }
    }

    private void markMergeColumn(Map<String, Path> paths) {
        if (config.getNumberOfPathToBeMerged() > 0) {
            Path[] arr = listPaths(new ArrayList<Path>(paths.values()), config.getNumberOfPathToBeMerged() );
            String baseCol = ColumnConstants.SPLC_PATH_MERGE;
            for (Path arr1 : arr) {
                List<Edge> ee = arr1.getEdges();
                for (Edge e : ee) {
                    //marcando os nós e arestas como 1
                    e.getAttributes().setValue(baseCol, ONE);
                    e.getSource().getAttributes().setValue(baseCol, ONE);
                    e.getTarget().getAttributes().setValue(baseCol, ONE);
                }
            }
        }
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    private void findStartEndAnd(Graph graph) {
        startNodes = GraphUtils.findStartNodes(graph, this);
        endNodes = GraphUtils.findEndNodes(graph, this);

        if (startNodes.isEmpty()) {
            errors.add("O grafo não possui nos inicio (que referenciam mas não são referenciados).");
        }

        if (endNodes.isEmpty()) {
            errors.add("O grafo não possui nós fim (que sao referenciados mas não referenciam).");
        }
    }

    /**
     * o Node Pair de uma aresta é basicamente inDegree * outDegree
     *
     * @param attributeModel model para criar a coluna
     * @param graph grafo direcionado para calcular o grau de entrada edges
     * saida
     */
    public void calculateNodePair(Graph graph) throws OperationNotSupportedException {
        avgNodePair = 0.0f;
        calculateNodePairFromTo(graph);
        calculateNodePairToFrom(graph);

        //calculando a multiplicacao
        for (Edge e : graph.getEdges()) {
            //progressTicket.progress(countProgress++);
            if (cancel) {
                return;
            }
            //ni 
            Double fromTo = ((Number) getAttribute(e.getSource(), ColumnConstants.NP_FROM_TO)).doubleValue();
            //mj
            Double toFrom = ((Number) getAttribute(e.getTarget(), ColumnConstants.NP_TO_FROM)).doubleValue();


            double np = fromTo * toFrom;
            e.getAttributes().setValue(ColumnConstants.NODE_PAIR_NAME, new Double(np));
            avgNodePair += np;
        }
        avgNodePair = avgNodePair / graph.getEdgeCount();

    }

    private void calculateNodePairFromTo(Graph graph) throws OperationNotSupportedException {
        HashMap<Node, HashSet<Node>> successors = new HashMap<Node, HashSet<Node>>();
        HashSet<Node> completed = new HashSet<Node>();
        for (Node n : graph.getNodes()) {
            calculateNodePairFromTo(n, graph, successors, completed, 0);
        }
        for (Map.Entry<Node, HashSet<Node>> e : successors.entrySet()) {
            Node n = e.getKey();
            HashSet<Node> succ = e.getValue();
            setAttribute(n, ColumnConstants.NP_FROM_TO, succ.size());
        }
    }

    private void calculateNodePairFromTo(Node n, Graph graph, HashMap<Node, HashSet<Node>> successors, HashSet<Node> complete, int depth) throws OperationNotSupportedException {
        if (!successors.containsKey(n)) {
            HashSet<Node> succList = new HashSet<Node>();
            successors.put(n, succList);

            //n itsef counts on NP ni
            succList.add(n);

            for (Edge e : graph.getEdges(n)) {
                Node next = e.getTarget();
                if (!e.isSelfLoop() && !n.equals(next)) {
                    if (!succList.contains(next)) {
                        //se nao estiver calculado calcule
                        calculateNodePairFromTo(next, graph, successors, complete, depth + 1);
                        succList.addAll(successors.get(next));
                    } else {
                        if (complete.contains(next)) {
                            succList.addAll(successors.get(next));
                        } else {
                            throw new OperationNotSupportedException("O grafo contém ciclos");
                        }
                    }
                }
            }
            complete.add(n);
        }
    }

    private void calculateNodePairToFrom(Graph graph) throws OperationNotSupportedException {
        HashMap<Node, HashSet<Node>> ancestors = new HashMap<Node, HashSet<Node>>();
        HashSet<Node> completed = new HashSet<Node>();

        for (Node n : graph.getNodes()) {
            calculateNodePairToFrom(n, graph, ancestors, completed, 0);
        }

        for (Map.Entry<Node, HashSet<Node>> e : ancestors.entrySet()) {
            Node n = e.getKey();
            HashSet<Node> succ = e.getValue();
            //System.out.println("Node=" + n.getNodeData().getId() + " = " + succ.size());
            setAttribute(n, ColumnConstants.NP_TO_FROM, succ.size());
        }
    }

    private void calculateNodePairToFrom(Node n, Graph graph, HashMap<Node, HashSet<Node>> ancestors, HashSet<Node> complete, int depth) throws OperationNotSupportedException {
        // System.out.println(getTabs(depth) + "node=" + n.getNodeData().getId());
        if (!ancestors.containsKey(n)) {
            HashSet<Node> ancestorsSet = new HashSet<Node>();
            ancestors.put(n, ancestorsSet);

            //n itsef counts on NP ni
            ancestorsSet.add(n);

            for (Edge e : graph.getEdges(n)) {
                Node prev = e.getSource();
                if (!e.isSelfLoop() && !n.equals(prev)) {
                    if (!ancestorsSet.contains(prev)) {
                        //se nao estiver calculado calcule
                        calculateNodePairToFrom(prev, graph, ancestors, complete, depth + 1);
                        ancestorsSet.addAll(ancestors.get(prev));
                    } else {
                        if (complete.contains(prev)) {
                            ancestorsSet.addAll(ancestors.get(prev));
                        } else {
                            throw new OperationNotSupportedException("O grafo contém ciclos");
                        }
                    }

                }
            }
            complete.add(n);
        }
    }

    private void setAttribute(Node n, String column, Object value) {
        n.getAttributes().setValue(column, value);
    }

    private Object getAttribute(Node n, String column) {
        return n.getAttributes().getValue(column);
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    public float getAvgNodePair() {
        return avgNodePair;
    }

    private Path[] listPaths(List<Path> paths, int pathCount) {
        ArrayList<Path> v = new ArrayList<Path>();
        v.addAll(paths);
        Collections.sort(v, new PathNPComparator());

        //removendo acima de 10
        while (v.size() > pathCount) {
            v.remove(v.size() - 1);
        }

        return v.toArray(new Path[v.size()]);
    }

    private void deletePath(AttributeModel attributeModel, String column) {
        deletePath(attributeModel.getEdgeTable(), column);
        deletePath(attributeModel.getNodeTable(), column);
    }

    private void deletePath(AttributeTable table, String column) {
        //removing all splc paths
        List<AttributeColumn> remove = new ArrayList<AttributeColumn>();
        for (AttributeColumn a : table.getColumns()) {
            if (a.getId().contains(column)) {
                remove.add(a);
            }
        }
        for (AttributeColumn a : remove) {
            table.removeColumn(a);
        }
    }

    /**
     * -----------------------------------------------------------
     */
    @Override
    public String getReport() {
        if (config.getNumberOfPathsOnReport() <= 0) {
            return null;
        }
        //Write the report HTML string here
        StringBuilder report = new StringBuilder();
        report.append("<html><body><h1>Search Path Node Pair</h1><hr/>");


        if (errors.isEmpty() && paths != null && paths.size() > 0 && paths.values().size() > 0) {
            report.append(getSuccessfullReport());
        }
        report.append(getErrorReport());
        report.append(Constants.COPYRIGHT);
        report.append("</body></html>");
        return report.toString();
    }

    private String createCicleError(CicleException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Foi identificado um ciclo na aresta:");
        sb.append("\n").append(e.getEdge());
        sb.append("\nSource Node=").append(e.getEdge().getSource());
        sb.append("\nTarget Node=").append(e.getEdge().getTarget());
        return sb.toString();
    }

    private StringBuilder getSuccessfullReport() {
        StringBuilder report = new StringBuilder();
        Path[] arr = listPaths(new ArrayList<Path>(paths.values()), config.getNumberOfPathsOnReport());
        report.append("<h3>Resumo SPNP</h3>");
        HtmlTable table = new HtmlTable();
        table.addHeader(null, new Object[]{
                    "<b>ID</b>",
                    "<b>SPNP</b>",
                    "<b>Relevance</b>",
                    "<b>Number<br/> of Nodes</b>",
                    "<b>Initial Node</b>",
                    "<b>End Node</b>"});
        int i = 1;
        int relevance = 1;
        double lastStast = -1;
        for (Path p : arr) {
            if (p.getNodePairTotal() != lastStast) {
                lastStast = p.getNodePairTotal();
                relevance = i;
            }
           table.addData(null,
                    new Object[]{
                        i++,
                        p.getNodePairTotal(),
                        relevance,
                        p.getNodeCount(),
                        p.getStartNode().getNodeData().getId(),
                        p.getEndNode().getNodeData().getId()
                    });
        }
        report.append(table.build("border='1'"));
        report.append("<hr>");
        if(config.isShowStartAndEnd()){
            report.append("<h3>Inicital Nodes:").append(startNodes.size()).append(" Node(s) </h3>");
            report.append("<p>");
            report.append(ReportUtils.listNodeToCsv(startNodes));
            report.append("</p>");

            report.append("<h3>End Nodes:").append(endNodes.size()).append(" Node(s)</h3>");
            report.append("<p>");
            report.append(ReportUtils.listNodeToCsv(endNodes));
            report.append("</p>");
            report.append("<hr>");
        }
        i = 0;
        for (Path p : arr) {
            report.append("<h3>Nodes in SPNP Path - ").append(1 + i++).append("</h3>");
            report.append("<p>").append(ReportUtils.listNodeToCsv(p.nodes())).append("</p>");
        }
        return report;
    }

    private StringBuilder getErrorReport() {
        StringBuilder report = new StringBuilder();
        if (!errors.isEmpty()) {
            report.append("<h2>Problemas identificados:</h2>");
            report.append(ReportUtils.listToHTML(errors, "ol"));
            errors.clear();
        }
        return report;
    }
    
    public void setSearchPathConfig(SearchPathConfig config) {
        this.config = config;
    }

    public SearchPathConfig getSearchPathConfig() {
        return config;
    }
}