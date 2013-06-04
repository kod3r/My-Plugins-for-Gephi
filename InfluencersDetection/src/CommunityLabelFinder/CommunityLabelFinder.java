package CommunityLabelFinder;

import Utils.NGramDuplicatesCleaner;
import Utils.NGramFinder;
import Utils.StatusCleaner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */
public class CommunityLabelFinder {

    private Graph graph;
    private int communityIndex;
    private String communityLang;
    private List<String> descriptions;
    private List<String> descriptionsInACommunity;
    private static Multiset<String> termsInTotal;

    public CommunityLabelFinder(Graph graph, int communityIndex) {
        this.graph = graph;
        this.communityIndex = communityIndex;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public int getCommunityIndex() {
        return communityIndex;
    }

    public void setCommunityIndex(int communityIndex) {
        this.communityIndex = communityIndex;
    }

    public void collectDescriptions() {
        descriptions = new ArrayList();
        for (Node node : graph.getNodes()) {
            descriptions.add((String) node.getNodeData().getAttributes().getValue(Controller.getColDescription()));
        }
        descriptions = cleanDescriptions(descriptions);

    }

    public void collectDescriptionsInACommunity() {
        descriptionsInACommunity = new ArrayList();
        for (Node node : graph.getNodes()) {
            if ((Integer) node.getNodeData().getAttributes().getValue("Modularity Class") == communityIndex) {
                descriptionsInACommunity.add((String) node.getNodeData().getAttributes().getValue(Controller.getColDescription()));
            }
        }
        descriptionsInACommunity = cleanDescriptions(descriptionsInACommunity);
    }

    public List<String> cleanDescriptions(List<String> text) {
        StatusCleaner cleaner = new StatusCleaner();
        List<String> toReturn = new ArrayList();
        for (String string : text) {
            string = cleaner.clean(string);
            string = cleaner.removePunctuationSigns(string);
            toReturn.add(string.toLowerCase());
        }
        return toReturn;
    }

    public String determineLanguage() {
        Multiset<String> communityLangs = HashMultiset.create();
        int highestCount = 0;
        String mostFrequentLang = "";
        for (Node node : graph.getNodes()) {
            if ((Integer) node.getNodeData().getAttributes().getValue("Modularity Class") == communityIndex) {
                String currLang = (String) node.getNodeData().getAttributes().getValue(Controller.getColLang());
                communityLangs.add(currLang);
            }
        }
        for (String lang : communityLangs.elementSet()) {
            if (communityLangs.count(lang) >= highestCount) {
                highestCount = communityLangs.count(lang);
                mostFrequentLang = lang;
            }
        }
        return mostFrequentLang;
    }

    public void findMostFrequentsLabelsInTotal(Set<String> stopwords) {

        Multiset<String> terms = HashMultiset.create();
        NGramFinder ngf = new NGramFinder(descriptions);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        termsInTotal = swr.process(terms);
    }

    public String findMostFrequentsLabelsInACommunity(Set<String> stopwords) {

        Multiset<String> terms = HashMultiset.create();
        NGramFinder ngf = new NGramFinder(descriptionsInACommunity);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        terms = swr.process(terms);
        terms = NGramDuplicatesCleaner.removeDuplicates(terms);
        double highestFrequency = 0.0;
        String mostFrequentTerm = "";
        for (String string : terms.elementSet()) {
            double freq = ((double) terms.count(string) * 2 * (1 / Math.max(1, termsInTotal.count(string) - terms.count(string))));
            if (freq > highestFrequency) {
                highestFrequency = freq;
                mostFrequentTerm = string;
            }
        }
        return mostFrequentTerm;

    }
}
