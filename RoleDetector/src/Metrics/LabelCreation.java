/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Metrics;

import Model.TempMetrics;
import Model.Community;
import Control.GeneralController;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.openide.util.Lookup;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class LabelCreation {

    private Graph graph;
    private AttributeModel attributeModel;
    private Map<Integer, TempMetrics> map;

    public LabelCreation(Graph graph, AttributeModel attributeModel, Map<Integer, TempMetrics> map) {
        this.graph = graph;
        this.map = map;
        this.attributeModel = attributeModel;
    }

    public void create() {

        //resize all labels to zero
        RankingController rc = Lookup.getDefault().lookup(RankingController.class);
        AttributeColumn col = attributeModel.getNodeTable().getColumn("Modularity Class");
        Ranking dummyRanking = rc.getModel().getRanking(Ranking.NODE_ELEMENT, col.getId());
        AbstractSizeTransformer labelSizeTransformer = (AbstractSizeTransformer) rc.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.LABEL_SIZE);
        labelSizeTransformer.setMinSize(0);
        labelSizeTransformer.setMaxSize(0);
        rc.transform(dummyRanking, labelSizeTransformer);



        //create a new node for each label
        GraphModel gm = graph.getGraphModel();
        for (int id : map.keySet()) {
            TempMetrics tm = map.get(id);
            if (!tm.getRole().equals("agent")) {
                //resize the node with this special role to a size of 20
                graph.getNode(id).getNodeData().setSize(20);
                graph.getNode(id).getNodeData().getTextData().setSize(2);

                //create the new node for the label;
//                Node node = gm.factory().newNode();
//                node.getNodeData().setLabel(tm.getRole());
//                node.getNodeData().setSize(20f);
//                node.getNodeData().getTextData().setSize(2);
//                node.getNodeData().getAttributes().setValue("Modularity Class", -1);
//                Edge edge = gm.factory().newEdge(graph.getNode(id), node);
//                edge.setWeight(1.1f);
//                graph.addNode(node);
//                graph.addEdge(edge);
            }
        }

        //create a label for each community
        for (Community community : GeneralController.getCommunities()) {
            if (community.getLabel() == null) {
                continue;
            }
            Node node = gm.factory().newNode();
            node.getNodeData().setLabel(community.getLabel());
            node.getNodeData().getAttributes().setValue("Modularity Class", community.getId()+5000);
            node.getNodeData().setSize(30f);
            node.getNodeData().getTextData().setSize(3);

            Edge edge = null;
            for (Node node2 : graph.getNodes().toArray()) {
                if ((Integer) node2.getAttributes().getValue("Modularity Class") == community.getId() & node2.getAttributes().getValue("role").equals("local authority")) {
                    node.getNodeData().setR(node2.getNodeData().r());
                    node.getNodeData().setG(node2.getNodeData().g());
                    node.getNodeData().setB(node2.getNodeData().b());
                    edge = gm.factory().newEdge(graph.getNode(node2.getId()), node);
                    edge.setWeight(3f);
                    graph.addNode(node);
                    graph.addEdge(edge);
                    break;
                }
            }

        }

        //hide all labels temporarily
        for (Node node : graph.getNodes().toArray()) {
            node.getNodeData().getTextData().setVisible(false);
        }



    }
}
