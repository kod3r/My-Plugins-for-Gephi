/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Layout;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author C. Levallois
 */
public class LabelPositioner {

    private Graph graph;

    public LabelPositioner(Graph graph) {
        this.graph = graph;
    }

    public void execute() {
        for (Node node : graph.getNodes().toArray()) {
            if ((Integer) node.getAttributes().getValue("Modularity Class") > 0) {
                continue;
            }

            Node neighborNode = graph.getNeighbors(node).toArray()[0];
            if (neighborNode == null) {
                continue;
            }

            node.getNodeData().setY(neighborNode.getNodeData().y() - neighborNode.getNodeData().getTextData().getHeight() - 10f);
            node.getNodeData().setX(neighborNode.getNodeData().x());
            node.getNodeData().setZ(neighborNode.getNodeData().z());
            if ((Integer) node.getAttributes().getValue("Modularity Class") == -1) {
                node.getNodeData().setSize(Math.round(neighborNode.getNodeData().getSize() - 2));
            }
        }

    }
}
