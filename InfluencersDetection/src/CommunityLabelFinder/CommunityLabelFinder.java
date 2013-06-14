package CommunityLabelFinder;

import LanguageDetection.Cyzoku.DetectorFactory;
import LanguageDetection.Cyzoku.LangDetectException;
import LanguageDetection.LanguageDetector;
import Utils.NGramDuplicatesCleaner;
import Utils.NGramFinder;
import Utils.StatusCleaner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.IOException;
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
    private List<String> descriptions;
    private List<String> descriptionsInACommunity;
    private static Multiset<String> termsInTotal;
    private String mainLang;

    public CommunityLabelFinder(Graph graph, int communityIndex, String mainLang) {
        this.graph = graph;
        this.communityIndex = communityIndex;
        this.mainLang = mainLang;
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
            descriptions.add((String) node.getNodeData().getAttributes().getValue(ControllerLabelsFinder.getColDescription()));
        }
        descriptions = cleanDescriptions(descriptions);

    }

    public void collectDescriptionsInACommunity() throws LangDetectException, IOException {
        descriptionsInACommunity = new ArrayList();
        DetectorFactory.loadProfileChooseSource("", true);
        LanguageDetector ld;
        for (Node node : graph.getNodes()) {
            if ((Integer) node.getNodeData().getAttributes().getValue("Modularity Class") == communityIndex) {
                String nodeDescription = (String) node.getNodeData().getAttributes().getValue(ControllerLabelsFinder.getColDescription());
                String lang = new LanguageDetector().detect(nodeDescription);
                if (lang == null || !lang.equals(mainLang)) {
                    continue;
                }
                descriptionsInACommunity.add(nodeDescription);
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
                String currLang = (String) node.getNodeData().getAttributes().getValue(ControllerLabelsFinder.getColLang());
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

    public String determineLanguageFromDescription(String mainLang) throws LangDetectException, IOException {
        Multiset<String> communityLangs = HashMultiset.create();
        int highestCount = 0;
        String mostFrequentLang = "";
        DetectorFactory.loadProfileChooseSource("", true);
        LanguageDetector ld;
        for (Node node : graph.getNodes()) {
            if ((Integer) node.getNodeData().getAttributes().getValue("Modularity Class") == communityIndex) {
                ld = new LanguageDetector();
                String currDescription = (String) node.getNodeData().getAttributes().getValue(ControllerLabelsFinder.getColDescription());
                String currLang = ld.detect(currDescription);
                if (currLang != null) {
                    communityLangs.add(currLang);
                }

                System.out.println("currDescription: " + currDescription);
                System.out.println("currLang: " + currLang);

            }
        }
        for (String lang : communityLangs.elementSet()) {
            if (communityLangs.count(lang) >= highestCount) {
                highestCount = communityLangs.count(lang);
                mostFrequentLang = lang;
            }
        }
        if (highestCount * 3 > communityLangs.size()) {
            return mostFrequentLang;
        } else {
            return mainLang;
        }
    }

    public void findMostFrequentsLabelsInTotal(Set<String> stopwords) {

        Multiset<String> terms = HashMultiset.create();
        NGramFinder ngf = new NGramFinder(descriptions);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        termsInTotal = swr.process(terms);
    }

    public String findMostFrequentsLabelsInACommunity(Set<String> stopwords) {

        Multiset<String> terms;
        NGramFinder ngf = new NGramFinder(descriptionsInACommunity);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        terms = swr.process(terms);
        terms = NGramDuplicatesCleaner.removeDuplicates(terms);
        double highestFrequency = 0.0;
        String mostFrequentTerm = "";
        for (String string : terms.elementSet()) {
            double freq = ((double) terms.count(string) * 2 * (1 / Math.max(1, termsInTotal.count(string) - terms.count(string))));
            
            //social media should also count as "#socialmedia"
            if (string.contains(" ")) {
                String hashtagEquiv = string.replace(" ", "");
                freq = freq + ((double) terms.count(hashtagEquiv) * 2 * (1 / Math.max(1, termsInTotal.count(hashtagEquiv) - terms.count(hashtagEquiv))));
            }
            if (freq > highestFrequency) {
                highestFrequency = freq;
                mostFrequentTerm = string;
            }
        }
        return mostFrequentTerm;

    }
}
