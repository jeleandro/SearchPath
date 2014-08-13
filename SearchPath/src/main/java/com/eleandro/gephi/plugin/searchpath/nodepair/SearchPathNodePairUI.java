package com.eleandro.gephi.plugin.searchpath.nodepair;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class SearchPathNodePairUI implements StatisticsUI {
    
    /**
     * The name with
     */
    public static final String STATITICS_NAME = NbBundle.getMessage(SearchPathNodePairUI.class, "SearchPathNodePairUI.displayName");

    private SearchPathNodePairtPanel panel;
    private SearchPathNodePair myMetric;

    @Override
    public JPanel getSettingsPanel() {
        panel = new SearchPathNodePairtPanel();
        return panel; //no panel
    }

    @Override
    public void setup(Statistics statistics) {
        this.myMetric = (SearchPathNodePair) statistics;
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
        return SearchPathNodePair.class;
    }

    @Override
    public String getValue() {
        NumberFormat format = new DecimalFormat("#.##");
        return format.format(myMetric.getAvgNodePair());
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
        return 802;
    }

    @Override
    public String getShortDescription() {
        return STATITICS_NAME;
    }

}
