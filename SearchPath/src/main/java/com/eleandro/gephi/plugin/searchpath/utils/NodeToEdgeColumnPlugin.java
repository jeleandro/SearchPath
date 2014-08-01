/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.utils;

import com.eleandro.gephi.plugin.searchpath.AttributeHelper;
import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.swing.CenteredFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
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
id = "com.eleandro.gephi.plugin.searchpath.utils.NodeToEdgeColumnPlugin")
@ActionRegistration(displayName = "#CTL_NodeToEdgeColumnPlugin")
@ActionReferences({
    @ActionReference(path = "Menu/Plugins", position = 3343)
})
@NbBundle.Messages("CTL_TesterPlugin=NodeToEdgeColumnPlugin")/*
 *
 */

public final class NodeToEdgeColumnPlugin implements ActionListener, CancelableLongTask, LongTask {
    private GraphController graphController;
    private AttributeController attController;

    @Override
    public void actionPerformed(ActionEvent e) {
        graphController = Lookup.getDefault().lookup(GraphController.class);
        attController = Lookup.getDefault().lookup(AttributeController.class);
        
        if(graphController == null && attController == null){
            JOptionPane.showMessageDialog(null, "Grafo não inicializado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        final NodeToEdgeColumnUI panel = new NodeToEdgeColumnUI();
        final CenteredFrame frame = new CenteredFrame(NbBundle.getMessage(NodeToEdgeColumnPlugin.class, "NodeToEdgeColumnPlugin"), panel);

        panel.addActionListenerToBtCancel(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (panel.validateConfig()) {
                    execute(panel.getConfig());
                    frame.setVisible(false);
                }
            }
        });

        panel.addActionListenerToBtCancel(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                panel.removeAllActionListeners();
                frame.setVisible(false);
            }
        });
        
        frame.setSize(500, 400);
        frame.setVisible(true);
    }

    public void execute(NodeToEdgeColumnUI.NodeToEdgeColumnConfig config) {
        try {
            GraphModel graphModel = graphController.getModel();
            AttributeModel attModel = attController.getModel();
            Graph graph = graphModel.getHierarchicalDirectedGraphVisible();
            AttributeColumn ac = AttributeHelper.createDouble(attModel.getEdgeTable(), config.getNewFieldName());
            String operation = config.getOperation();
            
            for (Edge e : graph.getEdges()) {
                Double s = ((Number)e.getSource().getAttributes().getValue(config.getFieldOnSource())).doubleValue();
                Double t = ((Number)e.getTarget().getAttributes().getValue(config.getFieldOnTarget())).doubleValue();
                
                if("+".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), s + t);
                }else if("-".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), s - t);
                }else if("*".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), s * t);
                }else if("/".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), s / t);
                }else if("-".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), s - t);
                }else if("^".equals(operation)){
                    e.getAttributes().setValue(ac.getIndex(), Math.pow(s,t));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isCanceled() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
    }
}
