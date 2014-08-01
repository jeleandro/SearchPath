/*
 * Your license here
 */

package com.eleandro.gephi.plugin.searchpath.nodepair;

import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * See http://wiki.gephi.org/index.php/HowTo_write_a_metric#Create_StatisticsBuilder
 * 
 * @author José Eleandro Custódio
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class SearchPathNodePairBuilder implements StatisticsBuilder {

    @Override
    public String getName() {
        return "Search Path Link Count";
    }

    @Override
    public Statistics getStatistics() {
        return new SearchPathNodePair();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return SearchPathNodePair.class;
    }

}
