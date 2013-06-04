package net.clementlevallois.classes;

import Control.GeneralController;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jaroslav Kuchar
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class InfluencersDetectionMetricBuilder implements StatisticsBuilder {

    @Override
    public String getName() {
        return "Influncers Detection";
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
