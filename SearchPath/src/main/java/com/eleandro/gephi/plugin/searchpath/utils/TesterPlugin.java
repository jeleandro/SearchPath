/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.utils;

import com.eleandro.gephi.plugin.searchpath.CancelableLongTask;
import com.eleandro.gephi.plugin.searchpath.SearchPathConfig;
import com.eleandro.gephi.plugin.searchpath.linkcount.SearchPathLinkCount;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.plugin.ExporterCSV;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
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
 * /
@ActionID(category = "Tools",
id = "com.eleandro.gephi.plugin.searchpath.utils.TesterPlugin")
@ActionRegistration(displayName = "#CTL_TesterPlugin")
@ActionReferences({
    @ActionReference(path = "Menu/Plugins", position = 3343)
})
@NbBundle.Messages("CTL_TesterPlugin=TesterPlugin")/**/
public final class TesterPlugin implements ActionListener, CancelableLongTask, LongTask {

    private static final Class KLASS = TesterPlugin.class;
    private List<String> messages;
    private ProgressTicket progress;
    private boolean cancel = false;
    private static final String dir = "D:\\Projetos\\Fundace\\arquivos exemplos";
    private static final String filename = "agro sem circular a01h 1-00.csv";

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
            projectController.newProject();
            Project project = projectController.getCurrentProject();
            Workspace workspace = projectController.getCurrentWorkspace();


            // Get the graph instance
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel graphModel = graphController.getModel();
            Graph graph = graphModel.getHierarchicalMixedGraph();

            //Import file
            ImportController importController = Lookup.getDefault().lookup(ImportController.class);
            Container container;
            try {
                File file = new File(dir, filename);
                container = importController.importFile(file);
                container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            //Append imported data to GraphAPI
            importController.process(container, new DefaultProcessor(), workspace);

            AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
            GraphDistance distance = new GraphDistance();
            distance.setDirected(true);
            distance.execute(graphModel, attributeModel);
            //Get Centrality column created

            SearchPathConfig config = new SearchPathConfig();
            SearchPathLinkCount splc = new SearchPathLinkCount();
            splc.setSearchPathConfig(config);

            splc.setProgressTicket(null);
            splc.execute(graphModel, attributeModel);

            System.out.println(splc.getReport());


            ExporterCSV csv = new ExporterCSV();

            csv.setHeader(true);
            csv.setWorkspace(workspace);
            csv.setWriter(new FileWriter(new File(dir, filename + "-processed.csv")));
            System.out.println("acabou");

            FileWriter fw = new FileWriter(new File(dir, filename + ".html"));
            fw.write(splc.getReport());
            fw.close();
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
