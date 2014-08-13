/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.utils;

import com.eleandro.gephi.plugin.searchpath.AttributeHelper;
import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.MetadataUtils;
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
import org.openide.util.Exceptions;
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
@NbBundle.Messages("CTL_TesterPlugin=NodeToEdgeColumnPlugin")
public final class NodeToEdgeColumnPlugin implements ActionListener, CancelableLongTask, LongTask {

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        AttributeController attController = Lookup.getDefault().lookup(AttributeController.class);

        if (graphController == null || attController == null || graphController.getModel() == null) {
            JOptionPane.showMessageDialog(null, "Grafo não inicializado", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final NodeToEdgeColumnUI panel = new NodeToEdgeColumnUI();
        final CenteredFrame frame = new CenteredFrame(NbBundle.getMessage(NodeToEdgeColumnPlugin.class, "NodeToEdgeColumnPlugin"), panel);

        panel.addActionListenerToBtOK(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.validateConfig()) {
                    execute(panel.getConfig());
                    frame.setVisible(false);
                }
            }
        });

        panel.addActionListenerToBtCancel(new ActionListener() {

            @Override
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
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            AttributeController attController = Lookup.getDefault().lookup(AttributeController.class);
            GraphModel graphModel = graphController.getModel();
            AttributeModel attModel = attController.getModel();
            Graph graph = graphModel.getHierarchicalDirectedGraphVisible();
            AttributeColumn ac = AttributeHelper.createDouble(attModel.getEdgeTable(), config.getNewFieldName());
            
            
            String operation = config.getOperation();            
            String sourceAtt = config.getFieldOnSource();
            String targetAtt = config.getFieldOnTarget();

            Double s;
            Double t;
            Double defaultS = null;
            Double defaultT  =null;
            
            if("0".equals(sourceAtt)){
               defaultS =0D;
            }else if("1".equals(sourceAtt)){
               defaultS = 1D;
            }
            
            if("0".equals(targetAtt)){
               defaultT =0D;
            }else if("1".equals(targetAtt)){
               defaultT = 1D;
            }
            
            for (Edge e : graph.getEdges()) {
                s = (defaultS != null)? defaultS: MetadataUtils.getDouble(e.getSource(), sourceAtt);
                t = (defaultT != null)? defaultT: MetadataUtils.getDouble(e.getSource(), targetAtt);
                
                Double result = doMath(s, t, operation);
                
                e.getAttributes().setValue(ac.getIndex(),result);
            }

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private Double doMath(Double s,Double t, String operation){
        if ("+".equals(operation)) {
            return s + t;
        } else if ("-".equals(operation)) {
            return s - t;
        } else if ("*".equals(operation)) {
            return s * t;
        } else if ("/".equals(operation)) {
           return s / t;
        } else if ("^".equals(operation)) {
           return Math.pow(s, t);
        }
        return 0D;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
    }
}
