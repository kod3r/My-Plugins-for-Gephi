/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

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
        nbCommunities = nbCommunityFinder();
        degreeWithOtherCommunities();
//        highestDegreeWithOtherCommunities();
        highestDegreeAndDiversityWithOtherCommunities();
        return map;
    }

    private int nbCommunityFinder() {
        Set<Integer> communities = new HashSet();
        for (TempMetrics tm : map.values()) {
            communities.add(tm.getCommunity());
        }
        System.out.println("number of communities: " + communities.size());
        return communities.size();

    }

    private void degreeWithOtherCommunities() {
        int diffCommunityIn;
        int diffCommunityOut;
        Iterator<Entry<Integer, TempMetrics>> mapEntryIterator;
        System.out.println("number of communities in the degree method: " + nbCommunities);

        for (int i = 0; i <= nbCommunities; i++) {
            mapEntryIterator = map.entrySet().iterator();
            while (mapEntryIterator.hasNext()) {
                Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
                if (entry.getValue().getCommunity() != i) {
                    continue;
                }
                //initializing values for this node
                diffCommunityIn = 0;
                diffCommunityOut = 0;
                Set<Integer> setDiffCommunityOut = new HashSet();
                Set<Integer> setDiffCommunityIn = new HashSet();
                Set<Integer> setDiffCommunity = new HashSet();
                boolean isNodeSource = true;

                //looping through the neighbours of this node
                NodeIterable nodes = graph.getNeighbors(graph.getNode(entry.getKey()));
                Iterator<Node> nodesIterator = nodes.iterator();
                while (nodesIterator.hasNext()) {
                    Node node = nodesIterator.next();
                    if (map.get(node.getId()).getCommunity() != i) {
                        Edge edge = graph.getEdge(graph.getNode(entry.getKey()), node);
                        if (edge == null) {
                            edge = graph.getEdge(node, graph.getNode(entry.getKey()));
                            isNodeSource = false;
                        }
                        if (edge.isDirected()) {
                            if (edge.getSource().getId() == node.getId()) {
                                diffCommunityIn++;
                                setDiffCommunityIn.add(map.get(edge.getSource().getId()).getCommunity());
                            } else {
                                diffCommunityOut++;
                                setDiffCommunityOut.add(map.get(edge.getTarget().getId()).getCommunity());
                            }
                        } else {
                            diffCommunityIn++;
                            if (isNodeSource) {
                                setDiffCommunity.add(map.get(edge.getTarget().getId()).getCommunity());
                            } else {
                                setDiffCommunity.add(map.get(edge.getSource().getId()).getCommunity());
                            }
                        }
                    }
                    TempMetrics updatedTM = entry.getValue();
                    updatedTM.setInDegreeWithDifferentCommunities(diffCommunityIn);
                    updatedTM.setOutDegreeWithDifferentCommunities(diffCommunityOut);
                    updatedTM.setNbDiffCommunities(setDiffCommunity.size());
                    updatedTM.setNbDiffCommunitiesIn(setDiffCommunityIn.size());
                    updatedTM.setNbDiffCommunitiesOut(setDiffCommunityOut.size());

                    if (!graph.getGraphModel().isUndirected()) {
                        updatedTM.setDegreeWithDifferentCommunities(diffCommunityOut + diffCommunityIn);
                    } else {
                        updatedTM.setDegreeWithDifferentCommunities(diffCommunityIn);
                    }
                    entry.setValue(updatedTM);
                }
            }
        }
    }

    private void highestDegreeWithOtherCommunities() {
        Iterator<Entry<Integer, TempMetrics>> mapEntryIterator;
        System.out.println("number of communities in the highest degree method: " + nbCommunities);

        for (int i = 0; i <= nbCommunities; i++) {
            mapEntryIterator = map.entrySet().iterator();
            Node highestInDegreeNode = null;
            Node highestOutDegreeNode = null;
            Node highestDegreeNode = null;
            int highestInDegree = 0;
            int highestOutDegree = 0;
            int highestDegree = 0;

            while (mapEntryIterator.hasNext()) {
                Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
                if (entry.getValue().getCommunity() != i) {
                    continue;
                }
                if (entry.getValue().getDegreeWithDifferentCommunities() > highestDegree) {
                    highestDegree = entry.getValue().getDegreeWithDifferentCommunities();
                    highestDegreeNode = graph.getNode(entry.getKey());
                }
                if (!graph.getGraphModel().isUndirected()) {
                    if (entry.getValue().getInDegreeWithDifferentCommunities() > highestInDegree) {
                        highestInDegree = entry.getValue().getInDegreeWithDifferentCommunities();
                        highestInDegreeNode = graph.getNode(entry.getKey());
                    }
                    if (entry.getValue().getOutDegreeWithDifferentCommunities() > highestOutDegree) {
                        highestOutDegree = entry.getValue().getOutDegreeWithDifferentCommunities();
                        highestOutDegreeNode = graph.getNode(entry.getKey());
                    }

                }
            }
            TempMetrics tm;
            if (highestDegreeNode != null) {
                tm = map.get(highestDegreeNode.getId());
                tm.setIsHighestDegreeWithDifferentCommunities(true);
                System.out.println("finding a highestDegree for community " + i);
                map.put(highestDegreeNode.getId(), tm);
            }
            if (!graph.getGraphModel().isUndirected()) {
                if (highestInDegreeNode != null) {
                    tm = map.get(highestInDegreeNode.getId());
                    tm.setIsHighestInDegreeWithDifferentCommunities(true);
                    map.put(highestInDegreeNode.getId(), tm);
                }
                if (highestOutDegreeNode != null) {
                    tm = map.get(highestOutDegreeNode.getId());
                    tm.setIsHighestOutDegreeWithDifferentCommunities(true);
                    map.put(highestOutDegreeNode.getId(), tm);
                }
            }


        }
    }

    private void highestDegreeAndDiversityWithOtherCommunities() {
        Iterator<Entry<Integer, TempMetrics>> mapEntryIterator;

        for (int i = 0; i <= nbCommunities; i++) {
            mapEntryIterator = map.entrySet().iterator();
            Node highestInDegreeNode = null;
            Node highestOutDegreeNode = null;
            Node highestDegreeNode = null;
            int highestInDegreeDiversity = 0;
            int highestOutDegreeDiversity = 0;
            int highestDegreeDiversity = 0;
            int currCandidate;
            while (mapEntryIterator.hasNext()) {
                Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
                TempMetrics currValue = entry.getValue();
                if (currValue.getCommunity() != i) {
                    continue;
                }
                currCandidate = currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities();
                if (currCandidate > highestDegreeDiversity) {
                    highestDegreeDiversity = currCandidate;
                    highestDegreeNode = graph.getNode(entry.getKey());
                }
                if (!graph.getGraphModel().isUndirected()) {

                    currCandidate = currValue.getInDegreeWithDifferentCommunities() * currValue.getNbDiffCommunitiesIn();
                    if (currCandidate > highestInDegreeDiversity) {
                        highestInDegreeDiversity = currCandidate;
                        highestInDegreeNode = graph.getNode(entry.getKey());
                    }

                    currCandidate = currValue.getOutDegreeWithDifferentCommunities() * currValue.getNbDiffCommunitiesOut();
                    if (currCandidate > highestOutDegreeDiversity) {
                        highestOutDegreeDiversity = currCandidate;
                        highestOutDegreeNode = graph.getNode(entry.getKey());
                    }
                }
            }
            TempMetrics tm;
            if (highestDegreeNode != null) {
                tm = map.get(highestDegreeNode.getId());
                tm.setIsHighestDegreeWithDifferentCommunities(true);
                System.out.println("finding a highestDegree for community " + i);
                map.put(highestDegreeNode.getId(), tm);
            }
            if (!graph.getGraphModel().isUndirected()) {
                if (highestInDegreeNode != null) {
                    tm = map.get(highestInDegreeNode.getId());
                    tm.setIsHighestInDegreeWithDifferentCommunities(true);
                    map.put(highestInDegreeNode.getId(), tm);
                }
                if (highestOutDegreeNode != null) {
                    tm = map.get(highestOutDegreeNode.getId());
                    tm.setIsHighestOutDegreeWithDifferentCommunities(true);
                    map.put(highestOutDegreeNode.getId(), tm);
                }
            }


        }
    }
}
