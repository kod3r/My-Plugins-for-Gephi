package DefaultPluginClasses;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXHeader;

/**
 *
 * @author Jaroslav Kuchar
 */
public class InfluencersDetectionMetricPanel extends JPanel {

//    private List<Node> listNode;
//    private JList list;
    private JXHeader header;

    public InfluencersDetectionMetricPanel() {
        this.setLayout(new BorderLayout());
        header = new JXHeader();
        header.setTitle("Influencers Detection");
        header.setDescription("Launch detection");
        this.add(header,BorderLayout.NORTH);


//        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
//        GraphModel model = graphController.getModel();
//        Graph graph = model.getGraph();
//        listNode = Arrays.asList(graph.getNodes().toArray());
//        Collections.sort(listNode, new NodeByLabelComparator());
//        list = new JList(listNode.toArray());
//        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        list.setVisibleRowCount(10);
//        list.setCellRenderer(new NodeListRenderer());
//        JScrollPane listScroller = new JScrollPane(list);        
//        this.add(listScroller);
    }

//    public void setSelected(int index) {
//        list.setSelectedIndex(index);
//    }
//
//    public Node getSelected() {
//        return (Node) list.getSelectedValue();
//    }
}
