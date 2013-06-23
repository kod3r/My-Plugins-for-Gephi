package DefaultPluginClasses;

import Control.GeneralController;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

@ServiceProvider(service = StatisticsBuilder.class)
public class InfluencersDetectionMetricBuilder implements StatisticsBuilder {

    @Override
    public String getName() {
        return "Detector of roles in the network";
    }

    @Override
    public Statistics getStatistics() {
        return new GeneralController();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return GeneralController.class;
    }
}
