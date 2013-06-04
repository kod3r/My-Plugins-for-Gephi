/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

import Control.GeneralController;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;

/**
 *
 * @author C. Levallois
 */
public class NodeMetricsComputer {

    Map<Integer, TempMetrics> map;
    int nbCommunities;
    Graph graph;

    public NodeMetricsComputer(Map<Integer, TempMetrics> map, Graph graph) {
        this.map = map;
        this.graph = graph;
    }

    public Map<Integer, TempMetrics> runMetrics() {
        degreeWithOtherCommunities();
        return map;
    }

    private void degreeWithOtherCommunities() {
        Integer diffCommunityIn;
        Integer diffCommunityOut;
        Integer diffCommunity;
        Iterator<Entry<Integer, TempMetrics>> mapEntryIterator;

        for (int i = 0; i <= GeneralController.getNbCommunities(); i++) {
            mapEntryIterator = map.entrySet().iterator();
            while (mapEntryIterator.hasNext()) {
                Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
                if (entry.getValue().getCommunity() != i) {
                    continue;
                }
                //initializing values for this node
                diffCommunityIn = 1;
                diffCommunityOut = 0;
                diffCommunity = 1;
                Set<Float> setDiffCommunityOut = new HashSet();
                Set<Float> setDiffCommunityIn = new HashSet();
                Set<Float> setDiffCommunity = new HashSet();

                setDiffCommunityOut.add((float) i);
                setDiffCommunityIn.add((float) i);
                setDiffCommunity.add((float) i);

                boolean isNeighborNodeSource;

                //looping through the neighbours of this node
                NodeIterable neighbours = graph.getNeighbors(graph.getNode(entry.getKey()));
                Iterator<Node> nodesIterator = neighbours.iterator();

                while (nodesIterator.hasNext()) {

                    Node neighbor = nodesIterator.next();
//                    if (neighbor.getNodeData().getLabel().equals("Fantine")) {
//                        System.out.println("Fantine");
//                        System.out.println("Fantine community is: " + map.get(neighbor.getId()).getCommunity());
//                    }
                    Edge edge = graph.getEdge(graph.getNode(entry.getKey()), neighbor);
                    if (edge == null) {
                        edge = graph.getEdge(neighbor, graph.getNode(entry.getKey()));
                    }
                    if (map.get(neighbor.getId()).getCommunity() != i) {
                        if (edge.getSource().getNodeData().getLabel().equals(graph.getNode(entry.getKey()).getNodeData().getLabel())) {
                            isNeighborNodeSource = false;
                        } else {
                            isNeighborNodeSource = true;
                        }
                        if (edge.isDirected()) {
                            if (edge.getSource().getId() == neighbor.getId()) {
                                diffCommunityIn++;
                                setDiffCommunityIn.add((float) map.get(edge.getSource().getId()).getCommunity());
                            } else {
                                diffCommunityOut++;
                                setDiffCommunityOut.add((float) map.get(edge.getTarget().getId()).getCommunity());
                            }
                        } else {
                            diffCommunity++;
                            if (isNeighborNodeSource) {
                                int communityNumber = map.get(edge.getSource().getId()).getCommunity();
                                setDiffCommunity.add((float) communityNumber);
                            } else {
//                                if (edge.getTarget().getNodeData().getLabel().equals("Fantine")) {
//                                    System.out.println("Fantine found");
//                                }
                                int communityNumber = map.get(edge.getTarget().getId()).getCommunity();
                                setDiffCommunity.add((float) communityNumber);

                            }
                        }
                    }
                }
                TempMetrics updatedTM = entry.getValue();
                updatedTM.setInDegreeWithDifferentCommunities(diffCommunityIn);
                updatedTM.setOutDegreeWithDifferentCommunities(diffCommunityOut);
                updatedTM.setDegreeWithDifferentCommunities(diffCommunity);
                updatedTM.setNbDiffCommunities(setDiffCommunity.size());
                updatedTM.setNbDiffCommunitiesIn(setDiffCommunityIn.size());
                updatedTM.setNbDiffCommunitiesOut(setDiffCommunityOut.size());

                if (!graph.getGraphModel().isUndirected()) {
                    updatedTM.setDegreeWithDifferentCommunities(diffCommunityOut + diffCommunityIn);
                } else {
                    updatedTM.setDegreeWithDifferentCommunities(diffCommunity);
                }
                entry.setValue(updatedTM);

            }
        }
    }
}
