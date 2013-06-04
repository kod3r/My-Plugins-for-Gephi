package net.clementlevallois.classes;

import Control.GeneralController;
import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jaroslav Kuchar
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

        //only when there is a panel to select the Erdos, which I commnented out
        //        if (panel != null) {
//            panel.setSelected(0);
//        }
    }

    @Override
    public void unsetup() {
//        if (panel != null) {
//            this.IDMetric.setSourceNode(panel.getSelected());
//        }
//        panel = null;
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return GeneralController.class;
    }

    @Override
    public String getValue() {
        if (this.IDMetric != null) {
            return "" + IDMetric.getReport();
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Role";

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
        return "Detects Influencers";
    }
}
