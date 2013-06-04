/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Partition;

import Control.GeneralController;
import Utils.Colors;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.clementlevallois.classes.Community;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author C. Levallois
 */
public class ColorPartitions {

    public static void colorize(Graph graph) {
        Map<Integer, Color> mapCommunityToColors = new HashMap();
        int index = 0;
        Colors colors = new Colors();

        for (Community community : GeneralController.getCommunities()) {
            Color color = Color.decode(colors.getColors().get(index));
            mapCommunityToColors.put(community.getId(), color);
        }
        
        for (Node node: graph.getNodes().toArray()){
            node.getNodeData().setR(mapCommunityToColors.get(graph.getNode(node.getId()).getAttributes().getValue("Modularity Class")).getRed());
            node.getNodeData().setG(mapCommunityToColors.get(graph.getNode(node.getId()).getAttributes().getValue("Modularity Class")).getGreen());
            node.getNodeData().setB(mapCommunityToColors.get(graph.getNode(node.getId()).getAttributes().getValue("Modularity Class")).getBlue());
        }
    }
}
