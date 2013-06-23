package DefaultPluginClasses;

import Control.GeneralController;
import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

@ServiceProvider(service = StatisticsUI.class)
public class InfluencersDetectionUI implements StatisticsUI {

    private InfluencersDetectionMetricPanel panel;
    private GeneralController IDMetric;

    @Override
    public JPanel getSettingsPanel() {
        panel = new InfluencersDetectionMetricPanel();
        return panel;
    }

    @Override
    public void setup(Statistics statistics) {
        this.IDMetric = (GeneralController) statistics;

    }

    @Override
    public void unsetup() {
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return GeneralController.class;
    }

    @Override
    public String getValue() {
            return "done";
    }

    @Override
    public String getDisplayName() {
        return "Detector of roles";

    }

    @Override
    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        return 800;

    }

    @Override
    public String getShortDescription() {
        return "Detects agents with specific roles in the network";
    }
}
