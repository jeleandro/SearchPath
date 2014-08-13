/*
 * Your license here
 */

package com.eleandro.gephi.plugin.searchpath.linkcount;

import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author José Eleandro Custódio
 */
@ServiceProvider(service = StatisticsUI.class)
public class SearchPathLinkCountUI implements StatisticsUI {
    
    /**
     * The name with
     */
    public static final String STATITICS_NAME = NbBundle.getMessage(SearchPathLinkCountUI.class, "SearchPathUI.displayName");

    private SearchPathLinkCountPanel panel;
    private SearchPathLinkCount myMetric;

    @Override
    public JPanel getSettingsPanel() {
        panel = new SearchPathLinkCountPanel();
        return panel; //null if no panel exists
    }

    @Override
    public void setup(Statistics statistics) {
        this.myMetric = (SearchPathLinkCount) statistics;
        if (panel != null) {
            panel.setSearchPathConfig(myMetric.getSearchPathConfig());
        }
    }

    @Override
    public void unsetup() {
        if (panel != null) {
            myMetric.setSearchPathConfig(panel.getSearchPathConfig());
        }
        panel = null;
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return SearchPathLinkCount.class;
    }

    @Override
    public String getValue() {
        //Returns the result value on the front-end. 
        //If your metric doesn't have a single result value, return null.
        return null;
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
        return 800;
    }

    @Override
    public String getShortDescription() {
        return "SPLC";
    }

}
