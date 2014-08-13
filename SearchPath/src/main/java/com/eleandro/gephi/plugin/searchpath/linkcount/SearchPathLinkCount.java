package com.eleandro.gephi.plugin.searchpath.linkcount;

import com.eleandro.gephi.plugin.searchpath.*;
import com.eleandro.gephi.plugin.searchpath.algorithms.*;
import com.eleandro.gephi.plugin.searchpath.cicles.CicleException;
import com.eleandro.gephi.plugin.searchpath.report.HtmlReport;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Mathmatical method to evalute routes/paths on graphs ins
 *
 * See http://wiki.gephi.org/index.php/HowTo_write_a_metric#Create_Statistics
 *
 * @author José Eleandro Custódio
 */
public class SearchPathLinkCount implements Statistics, LongTask, CancelableLongTask {
    private boolean cancel = false;
    private ProgressTicket progressTicket;
    //variáveis de sessao, limpar apos uso!!!
    private HashMap<String,String> errors = new HashMap<String,String>();
    private SearchPathConfig config = new SearchPathConfig();
    private int weightColIndex = -1;
    private PathStatistics pathFactory;

    @Override
    public void execute(GraphModel graphModel, AttributeModel attModel) {
        cancel = false;
        Graph graph = graphModel.getGraphVisible();
        graph.readLock();

        if (graph instanceof DirectedGraph) {
            graph = graph.getGraphModel().getHierarchicalDirectedGraphVisible();
        } else {
            errors.put("Tipo de grafo","SPLC só funciona em grafos direcionados.");
        }

        errors.clear();
        pathFactory = new PathStatistics();

        try {
            //avoid 
            Progress.start(progressTicket, 5);
            Progress.progress(progressTicket, 1);

            //Marcando nós inicio e nós fim
            List<Node> startNodes = GraphUtils.findStartNodes(graph, this);

            if (startNodes.isEmpty()) {
                errors.put("Nós","O grafo não possui nos início (que referenciam mas não são referenciados).");
            }

            if (errors.size() > 0) {
                return;
            }
            setupWeightColumn(attModel);

            GraphUtils.deletePath(attModel, ColumnConstants.SPLC_PATH);
            AttributeColumn lcCol = AttributeHelper.createDouble(attModel.getEdgeTable(), ColumnConstants.LINK_COUNT_NAME);

            //cria todos os caminhos

            pathFactory.generatePaths(graph, startNodes);
            runLinkCount(lcCol.getIndex(), pathFactory.getPaths());
            pathFactory.apply(new RelevancePathAlgorithm(new PathLCComparator()));
            Progress.progress(progressTicket, 1);

            if (config.getDifferenceType().equals(DifferenceType.ALL_VS_ALL)) {
                pathFactory.apply(new DifferenceInAllAlgorithm(config.getPercDiffMinimunPath()));
            } else if (config.isIgnoreSPLConDiffFilter()) {
                pathFactory.apply(new DifferenceOneByOneAlgorithm(config.getPercDiffMinimunPath()));
            } else {
                pathFactory.apply(new DifferenceOneByOneForSameSPAlgorithm(config.getPercDiffMinimunPath()));
            }
            pathFactory.apply(new DifferenceBetweenPathsAlgorithm());
            Progress.progress(progressTicket, 1);

            pathFactory.apply(
                    new MergeColumnMetaDataAlgorithm(attModel, config.getNumberOfPathToBeMerged()));
            pathFactory.apply(
                    new MarkPathOnMetadataAlgorithm(attModel, config.getNumberOfPathsOnMetaData()));
            Progress.progress(progressTicket, 1);

            //marcando inicio e fim
            Progress.progress(progressTicket, 1);
            createColumnsStartAndEnd(attModel, graph);

            graph.readUnlockAll();
        } catch (CicleException e) {
            errors.put("Ciclos",createCicleError(e));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            errors.put("Erro desconhecido:",sw.toString());
            Exceptions.printStackTrace(e);
        } finally {
            //Unlock graph
            graph.readUnlockAll();
            Progress.finish(progressTicket);
        }
    }

    private void setupWeightColumn(AttributeModel attModel) {
        AttributeColumn col = attModel.getEdgeTable().getColumn(config.getWeightField());
        if (col != null) {
            weightColIndex = col.getIndex();
        }
    }

    private void runLinkCount(int lcColIndex, Map<String, Path> paths) {
        for (Entry<String, Path> entry : paths.entrySet()) {
            //pegando a coluna e  incrementando
            for (Edge ee : entry.getValue().getEdges()) {
                Attributes attributes = ee.getAttributes();
                Double oldValue = (Double) attributes.getValue(lcColIndex);

                Double weight = Double.valueOf(1);

                if (weightColIndex != -1) {
                    weight = ((Number) attributes.getValue(weightColIndex)).doubleValue();
                }

                if (oldValue == null) {
                    attributes.setValue(lcColIndex, weight);
                } else {
                    attributes.setValue(lcColIndex, oldValue + weight);
                }

                if (cancel) {
                    return;
                }
            }
        }
    }

    private void createColumnsStartAndEnd(AttributeModel attributeModel, Graph graph) {
        AttributeColumn ref = AttributeHelper.createDouble(attributeModel.getNodeTable(), ColumnConstants.PATH_REF);

        int index = ref.getIndex();

        final Double M_ONE = -1D;
        final Double ONE = -1D;

        List<Node> startNodes = GraphUtils.findStartNodes(graph, this);
        for (Node n : startNodes) {
            n.getAttributes().setValue(index, M_ONE);
        }

        List<Node> endNodes = GraphUtils.findEndNodes(graph, this);
        if (endNodes.isEmpty()) {
            errors.put("Fim", "O grafo não possui nós fim (que sao referenciados mas não referenciam).");
        }
        for (Node n : endNodes) {
            n.getAttributes().setValue(index, ONE);
        }
    }

    /**
     * -----------------------------------------------------------
     */
    @Override
    public String getReport() {
        if (config.getNumberOfPathsOnReport() == 0) {
            return null;
        }
        HtmlReport htmlReport = new HtmlReport();
        htmlReport
                .start("Search Path Link Count");
       
        htmlReport.addParameter("Peso das arestas:", config.getWeightField());
        htmlReport.addParameter("Diferença mínima de nós entre caminhos %:", config.getPercDiffMinimunPath());
        htmlReport.addParameter("Diferença aplicada com SPLC diferente:", config.isIgnoreSPLConDiffFilter());
        
        List<Path> paths = pathFactory.getPathsAsList();
        
        int pathOnReport = Math.min(config.getNumberOfPathsOnReport(), paths.size());
        /*
        ArrayList<Path> list = new ArrayList<Path>();
        for(Path p:paths){
            list.add(p);
            if(pathOnReport == 0){
                break;
            }
            pathOnReport--;
        }*/
        
        htmlReport.addPaths(paths.subList(0, pathOnReport));
        return htmlReport.build();
    }

    private String createCicleError(CicleException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Foi identificado um ciclo na aresta:");
        sb.append("\n").append(e.getEdge());
        sb.append("\nSource Node=").append(e.getEdge().getSource());
        sb.append("\nTarget Node=").append(e.getEdge().getTarget());
        return sb.toString();
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    public void setSearchPathConfig(SearchPathConfig config) {
        this.config = config;
    }

    public SearchPathConfig getSearchPathConfig() {
        return config;
    }
}