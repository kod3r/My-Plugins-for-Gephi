/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

import Control.GeneralController;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.plugin.partition.PartitionBuilder.NodePartitionFilter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.openide.util.Lookup;

/**
 *
 * @author C. Levallois
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



        Set<String> roles = new HashSet();

        //create a new node for each label
        GraphModel gm = graph.getGraphModel();
        for (int id : map.keySet()) {
            TempMetrics tm = map.get(id);
            if (!tm.getRole().equals("agent")) {
                roles.add(tm.getRole());
                //resize the node with this special role to a size of 20
                graph.getNode(id).getNodeData().setSize(20);
                graph.getNode(id).getNodeData().getTextData().setSize(2);

                //create the new node for the label;
                Node node = gm.factory().newNode();
                node.getNodeData().setLabel(tm.getRole());
                node.getNodeData().setSize(20);
                node.getNodeData().getTextData().setSize(2);
                node.getNodeData().getAttributes().setValue("Modularity Class", -1);
                Edge edge = gm.factory().newEdge(graph.getNode(id), node);
                edge.setWeight(1.1f);
                graph.addNode(node);
                graph.addEdge(edge);
            }
        }
        for (Community community : GeneralController.getCommunities()) {
            Node node = gm.factory().newNode();
            node.getNodeData().setLabel(community.getLabel());
            System.out.println("label comm: " + community.getLabel());
            node.getNodeData().getAttributes().setValue("Modularity Class", -2);
            Edge edge = null;
            for (Node node2 : graph.getNodes().toArray()) {
                if ((Integer) node2.getAttributes().getValue("Modularity Class") == community.getId()) {
                    node.getNodeData().setR(node2.getNodeData().r());
                    node.getNodeData().setG(node2.getNodeData().g());
                    node.getNodeData().setB(node2.getNodeData().b());
                    node.getNodeData().getTextData().setSize(3);
                    node.getNodeData().setSize(20);
                    edge = gm.factory().newEdge(graph.getNode(node2.getId()), node);
                    edge.setWeight(1f);
                    graph.addNode(node);
                    graph.addEdge(edge);
                    break;
                }
            }
        }

//        GraphView mainView = graph.getView();


//        //filter the graph to keep only these new nodes.
//        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
//        PartitionController pc = Lookup.getDefault().lookup(PartitionController.class);
//        Partition p = pc.buildPartition(attributeModel.getNodeTable().getColumn("Modularity Class"), graph);
//
//        NodePartitionFilter partitionFilter = new NodePartitionFilter(p);
//        partitionFilter.unselectAll();
//        Part part;
//        part = p.getPartFromValue(-1);
//        partitionFilter.addPart(part);
//        part = p.getPartFromValue(-2);
//        partitionFilter.addPart(part);
//
//        Query query = filterController.createQuery(partitionFilter);
//        GraphView view = filterController.filter(query);
//        graph.getGraphModel().setVisibleView(view); //Set the filter result as the visible view
//
//        //resize labels only for the nodes selected in the partition
//        rc = Lookup.getDefault().lookup(RankingController.class);
//        col = attributeModel.getNodeTable().getColumn("Modularity Class");
//        dummyRanking = rc.getModel().getRanking(Ranking.NODE_ELEMENT, col.getId());
//        labelSizeTransformer = (AbstractSizeTransformer) rc.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.LABEL_SIZE);
//        labelSizeTransformer.setMinSize(2);
//        labelSizeTransformer.setMaxSize(2);
//        rc.transform(dummyRanking, labelSizeTransformer);
//
//        filterController.remove(query);
//        graph.getGraphModel().setVisibleView(mainView);
//
    }
}
