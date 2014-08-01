package com.eleandro.gephi.plugin.searchpath.report;

import com.eleandro.gephi.plugin.searchpath.Constants;
import com.eleandro.gephi.plugin.searchpath.Path;
import com.eleandro.gephi.plugin.searchpath.ReportUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class HtmlReport {

    private String title;
    private Map<String, String> errors;
    private Map<String, String> algorithmParameters;
    private List<Path> paths;
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private boolean printStartAndEndNodes = true;

    public HtmlReport() {
    }

    public HtmlReport start(String title) {
        this.title = title;
        errors = new HashMap<String, String>();
        algorithmParameters = new HashMap<String, String>();
        paths = new ArrayList<Path>();
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head><body>");
        sb.append("<h1><font size=10>").append(title).append("</font></h1>");
        sb.append("<hr>");
        buildMapSection(sb, "Parâmetros do Algoritimo:", algorithmParameters);
        addHr(sb);

        if (!errors.isEmpty()) {
            buildMapSection(sb, "Erros no processamento:", errors);
        } else if (!paths.isEmpty()) {
            buildTable(sb);
            buildStartAndEnd(sb);
            buildPaths(sb);
        }
        buildCopyRight(sb);
        sb.append("</body></html>");
        return sb.toString();
    }

    public void buildMapSection(StringBuilder sb, String title, Map<String, String> map) {
        buildSectionTitle(sb, title);
        sb.append("<p>");
        for (String key : map.keySet()) {
            sb.append("<b>").append(key).append("</b>: ").append(map.get(key)).append("<br/>");
        }
        sb.append("</p>");
    }

    private void buildSectionTitle(StringBuilder sb, String title) {
        sb.append("<h3><font size='5'>").append(title).append("</font></h3>");
    }

    public HtmlReport addError(String param, String value) {
        errors.put(param, value);
        return this;
    }

    public HtmlReport addParameter(String param, String value) {
        algorithmParameters.put(param, value);
        return this;
    }

    public HtmlReport addParameter(String param, float value) {
        algorithmParameters.put(param, decimalFormat.format(value));
        return this;
    }

    public HtmlReport addParameter(String param, int value) {
        algorithmParameters.put(param, decimalFormat.format(value));
        return this;
    }

    public HtmlReport addParameter(String param, Number value) {
        algorithmParameters.put(param, decimalFormat.format(value));
        return this;
    }

    public HtmlReport addParameter(String param, boolean value) {
        algorithmParameters.put(param, (value ? "sim" : "não"));
        return this;
    }

    public HtmlReport addPaths(List<Path> data) {
        paths.addAll(data);
        return this;
    }

    private void buildTable(StringBuilder sb) {
        HtmlTable table = new HtmlTable();
        buildSectionTitle(sb, "Tabela Resumo:");
        table.addHeader(null, new Object[]{
                    "<b>ID</b>",
                    "<b>SPLC</b>",
                    "<b>Relevancia</b>",
                    "<b>%Mudança</b>",
                    "<b>Número <br/> de nós</b>",
                    "<b>Nó inicial</b>",
                    "<b>Nó final</b>"});

        int lineCount = 0;
        for (Path p : paths) {
            lineCount++;
            table.addData("td",
                    new Object[]{
                        lineCount,
                        decimalFormat.format(p.getLinkCountTotal()),
                        p.getRelevance(),
                        decimalFormat.format(p.getDifferenceToPreviousPath()),
                        p.getNodeCount(),
                        p.getStartNode().getNodeData().getId(),
                        p.getEndNode().getNodeData().getId()
                    });
        }
        sb.append(table.build("border"));
        addHr(sb);
    }

    private void buildStartAndEnd(StringBuilder sb) {
        if (printStartAndEndNodes) {
            ArrayList<Node> startNodes = new ArrayList<Node>();
            ArrayList<Node> endNodes = new ArrayList<Node>();
            for (Path p : paths) {
                startNodes.add(p.getStartNode());
                endNodes.add(p.getEndNode());
            }
            buildSectionTitle(sb, "Nós Iniciais:");
            sb.append(ReportUtils.listNodeToCsv(startNodes));

            addHr(sb);
            buildSectionTitle(sb, "Nós finais:");
            sb.append(ReportUtils.listNodeToCsv(endNodes));
            addHr(sb);

        }
    }

    private void addHr(StringBuilder sb) {
        sb.append("<hr/>");

    }

    public boolean isPrintStartAndEndNodes() {
        return printStartAndEndNodes;
    }

    public void setPrintStartAndEndNodes(boolean printStartAndEndNodes) {
        this.printStartAndEndNodes = printStartAndEndNodes;
    }

    private void buildPaths(StringBuilder sb) {
        buildSectionTitle(sb, "Caminhos mais relevantes:");
        int lineCount = 0;
        for (Path p : paths) {
            lineCount++;
            sb.append("<p><b>Nós do caminho ").append(lineCount).append(":</b></p>");
            sb.append("<p>").append(ReportUtils.listNodeToCsv(p.nodes())).append("</p>");
        }
    }

    private void buildCopyRight(StringBuilder sb) {
        addHr(sb);
        sb.append("<p>").append(Constants.COPYRIGHT).append("</p>");
    }
}
