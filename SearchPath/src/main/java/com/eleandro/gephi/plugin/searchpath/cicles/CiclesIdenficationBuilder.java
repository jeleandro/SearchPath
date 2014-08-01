/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.cicles;

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
public class CiclesIdenficationBuilder implements StatisticsBuilder {

    @Override
    public String getName() {
        return "Search Path Cicle Identification";
    }

    @Override
    public Statistics getStatistics() {
        return new CiclesIdenfication();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return CiclesIdenfication.class;
    }

}
