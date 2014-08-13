/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.cicles;

import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.GraphUtils;
import com.eleandro.gephi.plugin.searchpath.report.HtmlTable;
import com.eleandro.gephi.plugin.searchpath.swing.CenteredFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import org.gephi.graph.api.*;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author eleandro
 */
@ActionID(category = "Tools",
id = "com.eleandro.gephi.plugin.searchpath.cicles.AutomaticCicleDeletionPlugin")
@ActionRegistration(displayName = "#CTL_AutomaticCicleDeletion")
@ActionReferences({
    @ActionReference(path = "Menu/Plugins", position = 3333)
})
@NbBundle.Messages("CTL_AutomaticCicleDeletion=Automatic Remove Cicle")
public final class AutomaticCicleDeletionPlugin implements ActionListener, CancelableLongTask, LongTask {

    private static final Class KLASS = AutomaticCicleDeletionPlugin.class;
    private List<String> messages;
    private ProgressTicket progress;
    private boolean cancel = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the graph instance
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        if (graphModel == null) {
            return;
        }
        Graph g = graphModel.getHierarchicalDirectedGraphVisible();
        if (g == null) {
            return;
        }
        g.readLock();

        List<Node> starts = GraphUtils.findStartNodes(g, this);
        Collections.sort(starts, new Comparator<Node>() {

            public int compare(Node o1, Node o2) {
                return o1.getNodeData().getId().compareTo(o2.getNodeData().getId());
            }
        });
        HashSet<NodeData> visiteds = new HashSet<NodeData>();
        LinkedList<Edge> cicles = new LinkedList<Edge>();
        boolean hasCicles = false;
        do {
            for (Node n : starts) {
                hasCicles = findCicles(g, visiteds, cicles, n);
            }
        } while (hasCicles);
        g.readUnlockAll();
        g.writeLock();

        for (Edge ee : cicles) {
            g.removeEdge(ee);
        }
        g.writeUnlock();
        createReport(cicles);
    }

    private void createReport(LinkedList<Edge> cicles) throws MissingResourceException {
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><style>td,th{border:15px solid #ddd;padding:3px;}h1{font-size:10pt;}</style><BODY>");
        if (cicles.size() == 0) {
            sb.append("<center><b>").append(NbBundle.getMessage(KLASS, "Report.cicleNotFound")).append("</b></center>");
        } else {
            sb.append("<h1>").append(NbBundle.getMessage(KLASS, "Report.ciclesFoundTitle")).append("</h1>");
            
            HtmlTable table = new HtmlTable();
            table.addHeader(null, new Object[]{"Source", "Target"});
            for (Edge ee : cicles) {
               table.addData(null, new Object[]{
                            ee.getSource().getNodeData().getId(),
                            ee.getTarget().getNodeData().getId()
                        });
            }
            sb.append(table.build("cellpadding='3' cellspacing='3' border='1'"));
        }
        sb.append("</BODY></HTML>");
        showReport(sb.toString());
    }

    private void showReport(String text) {
        if (text == null || text.length() == 0) {
            return;
        }


        JEditorPane area = new JEditorPane();
        area.setContentType("text/html");
        area.setEditorKit(new HTMLEditorKit());
        area.setText(text);

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(area);
        CenteredFrame frame = new CenteredFrame(NbBundle.getMessage(KLASS, "Report.title"),scroll);
        frame.setSize(400, 500);
        frame.toCenter();
        frame.setVisible(true);
    }

    /**
     * remove cicles. if a cicle is identified the process is restarted to avoid
     * double remove in the same group.
     *
     * @param g
     * @param visiteds
     * @param cicles
     * @param current
     * @return
     */
    private boolean findCicles(Graph g, HashSet<NodeData> visiteds, List<Edge> cicles, Node current) {
        boolean hasCicle = false;
        visiteds.add(current.getNodeData());
        ArrayList<Edge> list = new ArrayList<Edge>();
        for (Edge e : g.getEdges(current)) {
            if (!e.isSelfLoop() && isSourceNode(e, current) && !cicles.contains(e)) {
                list.add(e);
            }
        }
        Collections.sort(list, SortByTargetNodeIdComparator.getInstance());
        for (Edge e : list) {
            if (visiteds.contains(e.getTarget().getNodeData())) {
                cicles.add(e);
                hasCicle = true;
            } else {
                if (findCicles(g, visiteds, cicles, e.getTarget())) {
                    hasCicle = true;
                }
            }
        }
        visiteds.remove(current.getNodeData());
        return hasCicle;
    }

    /**
     * Test if the node n is the source node of the edge
     *
     * @param e
     * @param n
     * @return true - if n is the source false - if n is not the source
     */
    private boolean isSourceNode(Edge e, Node n) {
        return e.getSource().getNodeData().equals(n.getNodeData());
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    public boolean cancel() {
        return false;
    }

    private static class SortByTargetNodeIdComparator implements Comparator<Edge> {

        private static Comparator<Edge> INSTANCE = new SortByTargetNodeIdComparator();

        public SortByTargetNodeIdComparator() {
        }

        public static Comparator<Edge> getInstance() {
            return INSTANCE;
        }

        public int compare(Edge o1, Edge o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            return o1.getTarget().getNodeData().getId().compareTo(o2.getTarget().getNodeData().getId());
        }
    }
}
