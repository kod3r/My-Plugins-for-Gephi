/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataLaboratory;

import Control.GeneralController;
import Model.TempMetrics;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.graph.api.Graph;

/**
 *
 * @author C. Levallois
 */
public class ValuesWritter {

    Graph graph;
    Map<Integer, TempMetrics> map;

    public ValuesWritter(Graph graph, Map<Integer, TempMetrics> map) {
        this.graph = graph;
        this.map = map;
    }

    public void writeRoleValues() {
        for (Integer id : map.keySet()) {
            TempMetrics tm = map.get(id);
            String role = tm.getRole();
            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue(GeneralController.getRoleColumn(), role);
        }
    }

    public void writeAllRoleColumns() {
        for (Integer id : map.keySet()) {
            TempMetrics tm = map.get(id);
            Float score;

            score = tm.getCommunityBridgerScore();
                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Community Bridgers", score);

            score = tm.getSpecialistScore();
                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Specialists", score);

            score = tm.getLocalStarScore();
                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Local Authorities", score);

            score = tm.getGlobalAuthorityScore();
                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Global Authorities", score);
        }

    }
}
