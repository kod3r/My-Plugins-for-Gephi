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
import java.util.TreeMap;
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
            index++;

        }

        for (Node node : graph.getNodes().toArray()) {
            int modClass = (Integer) graph.getNode(node.getId()).getAttributes().getValue("Modularity Class");
            Float r = mapCommunityToColors.get(modClass).getRed() / 255f;
            Float g = mapCommunityToColors.get(modClass).getGreen() / 255f;
            Float b = mapCommunityToColors.get(modClass).getBlue() / 255f;
            node.getNodeData().setR(r);
            node.getNodeData().setG(g);
            node.getNodeData().setB(b);
        }
    }
}
