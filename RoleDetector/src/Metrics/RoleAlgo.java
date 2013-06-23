/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
 */
package Metrics;

import Model.TempMetrics;
import Model.Community;
import Control.GeneralController;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author C. Levallois
 */
public class RoleAlgo {

    Map<Integer, TempMetrics> map;
    Graph graph;
    boolean directedGraph;
    AttributeController ac;

    public RoleAlgo(Graph graph, Map<Integer, TempMetrics> map) {
        this.map = map;
        this.graph = graph;
    }

    public Map<Integer, TempMetrics> detectRoles() {
        rockStars();
        commmunityBridgers();
        specialists();
        localAuthorities();
        globalAuthorities();
        return map;
    }

    private void rockStars() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapIterator;
        mapIterator = map.entrySet().iterator();

        int rockStartThreshold = Math.max(GeneralController.getMedianInDegree() * 50, 100000);
        createIntegerColumn("Rock stars");

        while (mapIterator.hasNext()) {
            Map.Entry<Integer, TempMetrics> entry = mapIterator.next();
            if (belongsToSmallCommunity(entry.getKey())) {
                continue;
            }
            TempMetrics tm = entry.getValue();
            if (!tm.getRole().equals("agent")) {
                continue;
            }
            if (tm.getFollowers() > rockStartThreshold) {
                tm.setRole("rock star");
                entry.setValue(tm);
                ((AttributeRow) graph.getNode(entry.getKey()).getAttributes()).setValue("Rock stars", tm.getFollowers());

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
//            createFloatColumn("CommunityBridgers");

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
                    currValue.setCommunityBridgerScore(currCandidate);

                    if ((currCandidate == highestInDegreeDiversity && currValue.getInDegree() > inDegree) | currCandidate > highestInDegreeDiversity) {
                        highestInDegreeDiversity = currCandidate;
                        highestDegreeNode = graph.getNode(entry.getKey());
                        inDegree = (float) currValue.getInDegree();
                    }
                } else {
                    currCandidate = (float) currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities();
                    currValue.setCommunityBridgerScore(currCandidate);

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
//            createFloatColumn("Specialists");

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
                    currValue.setSpecialistScore(currCandidate);

                    if ((currCandidate == lowestInDegreeDiversity && currValue.getInDegree() > inDegree) | currCandidate > lowestInDegreeDiversity) {
                        lowestInDegreeDiversity = currCandidate;
                        lowestDegreeNode = graph.getNode(entry.getKey());
                        inDegree = (float) currValue.getInDegree();
                    }

                } else {
                    currCandidate = (float) currValue.getDegree() - (currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities());
                    currValue.setSpecialistScore(currCandidate);
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

    public void globalAuthorities() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapEntryIterator;

        mapEntryIterator = map.entrySet().iterator();
        Node highestGlobalAuthorityNode = null;
        Float highestGlobalAuthority = 0f;
        Float inDegree = 0f;
        Float degree = 0f;
        Float currCandidate;
//        createFloatColumn("GlobalAuthorities");

        while (mapEntryIterator.hasNext()) {
            Map.Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
            if (belongsToSmallCommunity(entry.getKey())) {
                continue;
            }

            TempMetrics currValue = entry.getValue();

            if (!currValue.getRole().equals("agent")) {
                continue;
            }

            if (!graph.getGraphModel().isUndirected()) {
//                    if (graph.getNode(entry.getKey()).getNodeData().getLabel().equals("IamMattHale")) {
//                        System.out.println("IamMattHale");
//                    }
                currCandidate = (float) currValue.getEigenvectorCentrality() * 100 * currValue.getCentileFollowers();
                currValue.setGlobalAuthorityScore(currCandidate);

                if ((currCandidate == highestGlobalAuthority && currValue.getInDegree() > inDegree) | currCandidate > highestGlobalAuthority) {
                    highestGlobalAuthority = currCandidate;
                    highestGlobalAuthorityNode = graph.getNode(entry.getKey());
                    inDegree = (float) currValue.getInDegree();
                }

            } else {
                currCandidate = (float) currValue.getDegree() - (currValue.getDegreeWithDifferentCommunities() * currValue.getNbDiffCommunities());
                currValue.setSpecialistScore(currCandidate);
                if ((currCandidate == highestGlobalAuthority && currValue.getDegree() > degree) | currCandidate > highestGlobalAuthority) {
                    highestGlobalAuthority = currCandidate;
                    highestGlobalAuthorityNode = graph.getNode(entry.getKey());
                    degree = (float) currValue.getDegree();
                }
            }
        }

        TempMetrics tm;
        if (highestGlobalAuthorityNode != null) {
            tm = map.get(highestGlobalAuthorityNode.getId());
            tm.setRole("global authority");
            map.put(highestGlobalAuthorityNode.getId(), tm);
        }

    }

    public void localAuthorities() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapEntryIterator;

        for (int i = 0; i <= GeneralController.getNbCommunities(); i++) {
            mapEntryIterator = map.entrySet().iterator();
            Node highestLocalEVCNode = null;
            Float highestlocalEigenvectorCentrality = 0f;
            Float evc = 0f;
            Float currCandidate;
//            createFloatColumn("localStars");

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
                    currCandidate = (float) currValue.getLocalEigenvectorCentrality();
                    currValue.setLocalStarScore(currCandidate);
                    if ((currCandidate == highestlocalEigenvectorCentrality && currValue.getInDegree() > evc) | currCandidate > highestlocalEigenvectorCentrality) {
                        highestlocalEigenvectorCentrality = currCandidate;
                        highestLocalEVCNode = graph.getNode(entry.getKey());
                        evc = (float) currValue.getLocalEigenvectorCentrality();
                    }
                } else {
                    currCandidate = (float) currValue.getDegree();
                    currValue.setLocalStarScore(currCandidate);
                    if (currCandidate > highestlocalEigenvectorCentrality) {
                        highestlocalEigenvectorCentrality = currCandidate;
                        highestLocalEVCNode = graph.getNode(entry.getKey());
                        evc = (float) currValue.getDegree();
                    }

                }
            }

            TempMetrics tm;
            if (highestLocalEVCNode != null) {
                tm = map.get(highestLocalEVCNode.getId());
                tm.setRole("local authority");
                map.put(highestLocalEVCNode.getId(), tm);
                for (Community community : GeneralController.getCommunities()) {
                    if (community.getId() == tm.getCommunity()) {
                        community.setLocalStar(highestLocalEVCNode.getNodeData().getLabel());
                    }

                }
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

    private AttributeColumn createFloatColumn(String nameRole) {
        ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel am = ac.getModel();
        AttributeTable nodeTable = am.getNodeTable();

        AttributeColumn roleColumn = nodeTable.getColumn(nameRole);
        if (roleColumn != null) {
            nodeTable.removeColumn(roleColumn);
            nodeTable.addColumn(nameRole, nameRole, AttributeType.FLOAT, AttributeOrigin.DATA, 0f);
        } else {
            nodeTable.addColumn(nameRole, nameRole, AttributeType.FLOAT, AttributeOrigin.DATA, 0f);
        }
        roleColumn = nodeTable.getColumn(nameRole);
        return roleColumn;
    }

    private AttributeColumn createStringColumn(String nameRole) {
        ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel am = ac.getModel();
        AttributeTable nodeTable = am.getNodeTable();

        AttributeColumn roleColumn = nodeTable.getColumn(nameRole);
        if (roleColumn != null) {
            nodeTable.removeColumn(roleColumn);
            nodeTable.addColumn(nameRole, nameRole, AttributeType.STRING, AttributeOrigin.DATA, "");
        } else {
            nodeTable.addColumn(nameRole, nameRole, AttributeType.STRING, AttributeOrigin.DATA, "");
        }
        roleColumn = nodeTable.getColumn(nameRole);
        return roleColumn;
    }

    private AttributeColumn createIntegerColumn(String nameRole) {
        ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel am = ac.getModel();
        AttributeTable nodeTable = am.getNodeTable();

        AttributeColumn roleColumn = nodeTable.getColumn(nameRole);
        if (roleColumn != null) {
            nodeTable.removeColumn(roleColumn);
            nodeTable.addColumn(nameRole, nameRole, AttributeType.INT, AttributeOrigin.DATA, 0);
        } else {
            nodeTable.addColumn(nameRole, nameRole, AttributeType.INT, AttributeOrigin.DATA, 0);
        }
        roleColumn = nodeTable.getColumn(nameRole);
        return roleColumn;
    }
}
