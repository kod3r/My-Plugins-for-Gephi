package CommunityLabelFinder;

import LanguageDetection.Cyzoku.DetectorFactory;
import LanguageDetection.Cyzoku.LangDetectException;
import LanguageDetection.LanguageDetector;
import Model.TempMetrics;
import Utils.NGramFinder;
import Utils.StatusCleaner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/*
 Copyright 2008-2013 Clement Levallois
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net


 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Clement Levallois. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s): Clement Levallois

 */

public class CommunityLabelFinder {

    private Graph graph;
    private int communityIndex;
    private static List<String> descriptions;
    private static List<String> descriptionsInACommunity;
    private static Multiset<String> termsInTotal;
    private String mainLang;
    private Map<Integer, TempMetrics> map;

    public CommunityLabelFinder(Graph graph, int communityIndex, String mainLang, Map<Integer, TempMetrics> map) {
        this.graph = graph;
        this.communityIndex = communityIndex;
        this.mainLang = mainLang;
        this.map = map;
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
//            descriptions.add((String) node.getNodeData().getAttributes().getValue(ControllerLabelsFinder.getColDescription()));
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
                //these few lines increase the number of times the description of local authorities will be counted as occurrences
//                int multiplyFactor = 4;
//                int coeff = Math.round(map.get(node.getId()).getLocalEigenvectorCentrality() * 100 * multiplyFactor + 1);
//                for (int i = 1; i <= coeff; i++) {
//                    descriptionsInACommunity.add(nodeDescription);
//                }
                descriptionsInACommunity.add(nodeDescription);

            }
        }
        descriptionsInACommunity = cleanDescriptions(descriptionsInACommunity);
    }

    public List<String> cleanDescriptions(List<String> text) {
        StatusCleaner cleaner = new StatusCleaner();
        List<String> toReturn = new ArrayList();
        for (String string : text) {
            if (string == null) {
                continue;
            }
            string = string.toLowerCase();
            string = cleaner.clean(string);
            string = cleaner.removePunctuationSigns(string);
            toReturn.add(string);
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

//                System.out.println("currDescription: " + currDescription);
//                System.out.println("currLang: " + currLang);

            }
        }
        for (String lang : communityLangs.elementSet()) {
            if (communityLangs.count(lang) >= highestCount) {
                highestCount = communityLangs.count(lang);
                mostFrequentLang = lang;
            }
        }
        if (highestCount * 4 > communityLangs.size()) {
            return mostFrequentLang;
        } else {
            return mainLang;
        }
    }

    public void findMostFrequentsLabelsInTotal(Set<String> stopwords) {

        Multiset<String> terms;
        NGramFinder ngf = new NGramFinder(descriptions);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        termsInTotal = swr.process(terms);
//        termsInTotal = NGramDuplicatesCleaner.removeDuplicates(termsInTotal);

    }

    public String findMostFrequentsLabelsInACommunity(Set<String> stopwords) {

        Multiset<String> terms;
        NGramFinder ngf = new NGramFinder(descriptionsInACommunity);
        terms = ngf.runIt(2, true);
        StopWordsRemover swr = new StopWordsRemover(stopwords);
        terms = swr.process(terms);
//        terms = NGramDuplicatesCleaner.removeDuplicates(terms);
        double highestFrequency = 0.0;
        String mostFrequentTerm = "";
        for (String string : terms.elementSet()) {
            int countTermInCommunity = terms.count(string);
            int countTermInTotalNetwork = termsInTotal.count(string);
            double freq = (double) Math.pow(countTermInCommunity, 1.5) / Math.max(1, countTermInTotalNetwork - countTermInCommunity);
//            double freq = ((double) terms.count(string) / (termsInTotal.count(string) - terms.count(string)) / (descriptions.size() - descriptionsInACommunity.size()));
//            double freq = terms.count(string);

            //social media should also count as "#socialmedia"
            if (string.contains(" ")) {
                String hashtagEquiv = string.replace(" ", "");
                freq = freq + (double) Math.pow(countTermInCommunity, 1.5) / Math.max(1, (termsInTotal.count(hashtagEquiv) - terms.count(hashtagEquiv)));
//                freq = freq + ((double) terms.count(hashtagEquiv) / (termsInTotal.count(hashtagEquiv) - terms.count(hashtagEquiv)) / (descriptions.size() - descriptionsInACommunity.size()));
//                freq = freq + (double) terms.count(hashtagEquiv);
            }
            if (freq > highestFrequency) {
                highestFrequency = freq;
                mostFrequentTerm = string;
            }
        }
        return mostFrequentTerm;

    }
}
