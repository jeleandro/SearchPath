/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.report;

import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.GraphUtils;
import com.eleandro.gephi.plugin.searchpath.Path;
import com.eleandro.gephi.plugin.searchpath.PathStatistics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.naming.OperationNotSupportedException;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "com.eleandro.gephi.plugin.searchpath.report.SearchPathReport")
@ActionRegistration(displayName = "#CTL_SearchPathReport")
@ActionReferences({
    @ActionReference(path = "Menu/Plugins", position = 3333)
})
@Messages("CTL_SearchPathReport=SPLC and SPNP export")
public final class SearchPathReport implements ActionListener, CancelableLongTask {

    private boolean cancel = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the graph instance
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        if (graphModel == null) {
            return;
        }
        Graph g = graphModel.getDirectedGraph();
        if (g == null) {
            return;
        }

        PathStatistics factory = new PathStatistics();
        List<Node> starts = GraphUtils.findStartNodes(g, this);
        try {
            factory.generatePaths(g, starts);
        } catch (OperationNotSupportedException ex) {
            Exceptions.printStackTrace(ex);
        }

        List<Path> paths = factory.getPathsAsList();
        Collections.sort(paths, new PathComparator());

        ArrayList<PathReportData> reportList = new ArrayList<PathReportData>();

        if (paths.size() > 2) {
            Path current = paths.get(0);
            Node start = current.getStartNode();
            Node end = current.getEndNode();
            double minNodes = current.getNodeCount();
            double maxNodes = minNodes;

            double minLC = current.getLinkCountTotal();
            double maxLC = minLC;
            double minNP = current.getNodePairTotal();
            double maxNP = minNP;

            for (int j = 0; j < paths.size(); j++) {
                Path next = paths.get(j);
                if (next.getStartNode().equals(start) && next.getEndNode().equals(end)) {
                    double t = next.getLinkCountTotal();
                    minLC = (minLC > t) ? t : minLC;
                    maxLC = (maxLC < t) ? t : maxLC;

                    t = next.getNodePairTotal();
                    minNP = (minNP > t) ? t : minNP;
                    maxNP = (maxNP < t) ? t : maxNP;

                    t = next.getNodeCount();
                    minNodes = (minNodes > t) ? t : minNodes;
                    maxNodes = (maxNodes < t) ? t : maxNodes;
                } else {
                    PathReportData p = new PathReportData(start, end, minLC, maxLC, minNP, maxNP, minNodes, maxNodes);
                    reportList.add(p);
                    current = next;
                    start = current.getStartNode();
                    end = current.getEndNode();
                    maxNodes = minNodes = current.getNodeCount();
                    maxLC = minLC = current.getLinkCountTotal();
                    maxNP = minNP = current.getNodePairTotal();
                }
            }

            ExcelXMLCreator creator = new ExcelXMLCreator();
            ExcelSheet sheet = creator.createSheet("Path Report");
            sheet.setHeaders(
                    new String[]{
                        "Start Node",
                        "End Node",
                        "min LC",
                        "max LC",
                        "min NP",
                        "max NP",
                        "min qtd nodes",
                        "max qtd nodes"});
            for (PathReportData r : reportList) {
                sheet.add(r.getArray());
            }
            try {
                File file = getFile();

                if (file != null) {
                    FileWriter out = new FileWriter(file);
                    out.write(creator.buildXML());
                    out.close();
                }
            } catch (Exception ee) {
            }

        }

    }

    private File getFile() {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home") + File.separator + "lib");
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File toAdd = new FileChooserBuilder("libraries-dir").setTitle("Save report").
                setDefaultWorkingDirectory(home).setApproveText("Save").showSaveDialog();
        return toAdd;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
    }

    private class PathComparator implements Comparator<Path> {

        @Override
        public int compare(Path o1, Path o2) {
            if (o1.getStartNode().equals(o2.getStartNode())) {
                if (o1.getEndNode().equals(o2.getEndNode())) {
                    return 0;
                } else if (o1.getEndNode().getId() > o2.getEndNode().getId()) {
                    return 1;
                }
            } else if (o1.getStartNode().getId() > o2.getStartNode().getId()) {
                return 1;
            }
            return -1;
        }
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }
}
