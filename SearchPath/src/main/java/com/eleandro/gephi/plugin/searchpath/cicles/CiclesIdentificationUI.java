/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.cicles;

import java.text.MessageFormat;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author eleandro
 */
@ServiceProvider(service = StatisticsUI.class)
public class CiclesIdentificationUI implements StatisticsUI {
    
    /**
     * The name with
     */
    public static final String STATITICS_NAME = NbBundle.getMessage(CiclesIdentificationUI.class, "CiclesUI.displayName");

    private CiclesIdenfication myMetric;

    @Override
    public JPanel getSettingsPanel() {
        return null;
    }

    @Override
    public void setup(Statistics statistics) {
        myMetric = (CiclesIdenfication)statistics;
    }

    @Override
    public void unsetup() {
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return CiclesIdenfication.class;
    }

    @Override
    public String getValue() {
        float cicleRate = myMetric.getCicleRate();
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        return nf.format(cicleRate);
    }

    @Override
    public String getDisplayName() {
        return STATITICS_NAME;
    }

    @Override
    public String getCategory() {
        //The category is just where you want your metric to be displayed: NODE, EDGE or NETWORK.
        //Choose between:
        //- StatisticsUI.CATEGORY_NODE_OVERVIEW
        //- StatisticsUI.CATEGORY_EDGE_OVERVIEW
        //- StatisticsUI.CATEGORY_NETWORK_OVERVIEW
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        //The position control the order the metric front-end are displayed. 
        //Returns a value between 1 and 1000, that indicates the position. 
        //Less means upper.
        return 801;
    }

    public String getShortDescription() {
        return "Cicles";
    }

}
