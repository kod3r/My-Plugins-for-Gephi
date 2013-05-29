package net.clementlevallois.classes;

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
        return new InfluencersDetectionMetric();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return InfluencersDetectionMetric.class;
    }
}
