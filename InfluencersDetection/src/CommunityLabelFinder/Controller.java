/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunityLabelFinder;

import Control.GeneralController;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.clementlevallois.classes.Community;
import net.clementlevallois.classes.TempMetrics;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author C. Levallois
 */
public class Controller {

    private static String colDescription;
    private static String colLang = "";
    private static Set<String> stopwordsSet;
    private Graph graph;
    private AttributeModel attributeModel;
    private String mainLang;
    private Map<Integer, TempMetrics> map;

    public Controller(Graph graph, AttributeModel attributeModel, Map<Integer, TempMetrics> map) {
        this.graph = graph;
        this.map = map;
        this.attributeModel = attributeModel;
    }

    public void detectLabels() throws IOException {
        AttributeTable nodeTable = attributeModel.getNodeTable();

        //finds the name of the description column
        for (AttributeColumn col : nodeTable.getColumns()) {
            if (col.getTitle().toLowerCase().contains("description")) {
                colDescription = col.getTitle();
            }
        }

        if (colDescription == null) {
            return;
        }

        //finds the name of the language column
        for (AttributeColumn col : nodeTable.getColumns()) {
            if (col.getTitle().toLowerCase().contains("lang")) {
                colLang = col.getTitle();
            }
        }

        //builds a a stopwords list
        InputStream stopwordsStream = Controller.class.getResourceAsStream("stopwords_10000_most_frequent_filtered.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsStream));
        String[] stopwordsArray = br.readLine().split(",");
        stopwordsArray = Arrays.copyOf(stopwordsArray, 5000);
        br.close();
        stopwordsSet = new HashSet();
        stopwordsSet.addAll(Arrays.asList(stopwordsArray));

        //finds the main language of the tweets;
        if (!colLang.equals("")) {
            String mostFrequentLang = "";
            int highestCount = 0;
            Multiset<String> langs = HashMultiset.create();
            for (Node node : graph.getNodes()) {
                langs.add((String) node.getNodeData().getAttributes().getValue(colLang));
            }
            for (String lang : langs.elementSet()) {
                if (langs.count(lang) >= highestCount) {
                    highestCount = langs.count(lang);
                    mostFrequentLang = lang;
                }
            }
            mainLang = mostFrequentLang;
            System.out.println("main lang is: " + mainLang);
        }

        //launches the detection

        List<Community> communities = new ArrayList();
        CommunityLabelFinder clf = new CommunityLabelFinder(graph, 1);
        clf.collectDescriptions();
        clf.findMostFrequentsLabelsInTotal(stopwordsSet);

        for (Community community : GeneralController.getCommunities()) {
            if (community.getSize() < GeneralController.getMinCommunitySize()) {
                continue;
            }
            CommunityLabelFinder clf2 = new CommunityLabelFinder(graph, community.getId());
            String lang = "";
            if (!colLang.equals("")) {
                lang = clf2.determineLanguage();
            }
            if (!lang.equals(mainLang) & !lang.equals("")) {
                community.setLabel("We speak " + CountryCodes.get(lang));
            } else {
                clf2.collectDescriptionsInACommunity();
                community.setLabel(clf2.findMostFrequentsLabelsInACommunity(stopwordsSet));
            }
            System.out.println("community " + community.getId() + " has label " + community.getLabel());
            communities.add(community);
        }
        GeneralController.setCommunities(communities);

    }

    public static String getColDescription() {
        return colDescription;
    }

    public static String getColLang() {
        return colLang;
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
