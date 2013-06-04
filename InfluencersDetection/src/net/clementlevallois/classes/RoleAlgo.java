/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

import Control.GeneralController;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author C. Levallois
 */
public class RoleAlgo {

    Map<Integer, TempMetrics> map;
    Graph graph;
    boolean directedGraph;

    public RoleAlgo(Graph graph, Map<Integer, TempMetrics> map) {
        this.map = map;
        this.graph = graph;
    }

    public Map<Integer, TempMetrics> detectRoles() {
        rockStars();
        commmunityBridgers();
        specialists();
        return map;
    }

    private void rockStars() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapIterator;
        mapIterator = map.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Integer, TempMetrics> entry = mapIterator.next();
            if (belongsToSmallCommunity(entry.getKey())) {
                continue;
            }
            TempMetrics tm = entry.getValue();
            if (!tm.getRole().equals("agent")) {
                continue;
            }
            if (tm.getFollowers() > 100000) {
                tm.setRole("rock star");
                entry.setValue(tm);
            }
        }
    }

    public void commmunityBridgers() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapIterator;

        for (int i = 0; i <= GeneralController.getNbCommunities(); i++) {
            mapIterator = map.entrySet().iterator();
            Node highestDegreeNode = null;
            Float highestInDegreeDiversity = 0f;
            Float highestDegreeDiversity = 0f;
            Float inDegree = 0f;
            Float degree = 0f;
            Float currCandidate;
            while (mapIterator.hasNext()) {
                Map.Entry<Integer, TempMetrics> entry = mapIterator.next();
                if (belongsToSmallCommunity(entry.getKey())) {
                    continue;
                }
                TempMetrics currValue = entry.getValue();
                if (currValue.getCommunity() != i | !currValue.getRole().equals("agent")) {
                    continue;
                }
                if (!graph.getGraphModel().isUndirected()) {
                    currCandidate = (float) currValue.getInDegreeWithDifferentCommunities() * (currValue.getNbDiffCommunitiesIn());
                    if ((currCandidate == highestInDegreeDiversity && currValue.getInDegree() > inDegree) | currCandidate > highestInDegreeDiversity) {
                        highestInDegreeDiversity = currCandidate;
                        highestDegreeNode = graph.getNode(entry.getKey());
                        inDegree = (float) currValue.getInDegree();
                    }
                } else {
                    currCandidate = (float) currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities();
                    if ((currCandidate == highestDegreeDiversity && currValue.getDegree() > degree) | currCandidate > highestDegreeDiversity) {
                        highestDegreeDiversity = currCandidate;
                        highestDegreeNode = graph.getNode(entry.getKey());
                        degree = (float) currValue.getDegree();

                    }
                }
            }

            TempMetrics tm;
            if (highestDegreeNode != null) {
                tm = map.get(highestDegreeNode.getId());
                tm.setRole("community bridger");
                map.put(highestDegreeNode.getId(), tm);
            }
        }
    }

    public void specialists() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapEntryIterator;

        for (int i = 0; i <= GeneralController.getNbCommunities(); i++) {
            mapEntryIterator = map.entrySet().iterator();
            Node lowestDegreeNode = null;
            Float lowestInDegreeDiversity = 0f;
            Float lowestDegreeDiversity = 0f;
            Float inDegree = 0f;
            Float degree = 0f;
            Float currCandidate;
            while (mapEntryIterator.hasNext()) {
                Map.Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
                if (belongsToSmallCommunity(entry.getKey())) {
                    continue;
                }

                TempMetrics currValue = entry.getValue();
                if (currValue.getCommunity() != i | !currValue.getRole().equals("agent")) {
                    continue;
                }
                if (!graph.getGraphModel().isUndirected()) {
//                    if (graph.getNode(entry.getKey()).getNodeData().getLabel().equals("IamMattHale")) {
//                        System.out.println("IamMattHale");
//                    }
                    currCandidate = (float) currValue.getInDegree() - (currValue.getInDegreeWithDifferentCommunities() * currValue.getNbDiffCommunitiesIn());
                    if ((currCandidate == lowestInDegreeDiversity && currValue.getInDegree() > inDegree) | currCandidate > lowestInDegreeDiversity) {
                        lowestInDegreeDiversity = currCandidate;
                        lowestDegreeNode = graph.getNode(entry.getKey());
                        inDegree = (float) currValue.getInDegree();
                    }

                } else {
                    currCandidate = (float) currValue.getDegree() - (currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities());
                    if ((currCandidate == lowestDegreeDiversity && currValue.getDegree() > degree) | currCandidate > lowestDegreeDiversity) {
                        lowestDegreeDiversity = currCandidate;
                        lowestDegreeNode = graph.getNode(entry.getKey());
                        degree = (float) currValue.getDegree();
                    }
                }
            }

            TempMetrics tm;
            if (lowestDegreeNode != null) {
                tm = map.get(lowestDegreeNode.getId());
                tm.setRole("specialist");
                map.put(lowestDegreeNode.getId(), tm);
            }
        }
    }

    private boolean belongsToSmallCommunity(int nodeId) {
        int size = 0;
        int communityOfNode = map.get(nodeId).getCommunity();
        Community community = new Community();
        community.setId(communityOfNode);
        List<Community> communities = GeneralController.getCommunities();
        int index = communities.indexOf(community);
        if (index != -1) {
            size = communities.get(index).getSize();
        }
        if (size < GeneralController.getMinCommunitySize()) {
            return true;
        } else {
            return false;
        }
    }
}
