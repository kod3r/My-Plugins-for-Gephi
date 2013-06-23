package DefaultPluginClasses;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXHeader;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
 */
public class InfluencersDetectionMetricPanel extends JPanel {

//    private List<Node> listNode;
//    private JList list;
    private JXHeader header;

    public InfluencersDetectionMetricPanel() {
        this.setLayout(new BorderLayout());
        header = new JXHeader();
        header.setTitle("Detection of roles");
        header.setDescription("Launch detection");
        this.add(header, BorderLayout.NORTH);
    }
}
