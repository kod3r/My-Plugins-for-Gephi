/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataLaboratory;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/


import Control.GeneralController;
import Model.Community;
import Model.TempMetrics;
import java.util.List;
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
        List<Community> communities = GeneralController.getCommunities();

        for (Integer id : map.keySet()) {
            TempMetrics tm = map.get(id);
            Float score;

            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Community", "no label");

            score = tm.getCommunityBridgerScore();
            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Community Bridgers", score);

            score = tm.getSpecialistScore();
            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Specialists", score);

            score = tm.getLocalStarScore();
            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Local Authorities", score);

            score = tm.getGlobalAuthorityScore();
            ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Global Authorities", score);

            for (Community community : communities) {
                if (tm.getCommunity() == community.getId()) {
                    ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue("Community", community.getLabel());
                    break;
                }
            }

        }

    }
}
