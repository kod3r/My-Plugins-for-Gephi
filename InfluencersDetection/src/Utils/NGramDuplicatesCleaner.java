/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class NGramDuplicatesCleaner {

    public static HashMultiset<String> removeDuplicates(Multiset<String> setNGrams) {

        Clock deletingDuplicatesTime = new Clock("Deleting n-grams when they are already included in longer n-grams");

        deletingDuplicatesTime.addText("Example: it will remove \"United States of \" because \"United States of America\" exists and is quite frequent too");
        deletingDuplicatesTime.printText();

        HashMultiset multisetWords = HashMultiset.create();


        Iterator<Multiset.Entry<String>> itFreqList;
        Set<String> wordsToBeRemoved = new HashSet();
        Multiset.Entry<String> entry;
        Multiset.Entry<String> entry2;
        Iterator<Multiset.Entry<String>> itFreqList2;
        String currWord;
        int currWordCount;
        String currWord2;
        int currWordCount2;
        Clock loopOne = new Clock("loop one");
        Set<String> setCurrentSubNGrams;
        Iterator<String> setCurrentSubNGramsIterator;
        String string;
        String[] termsInBigram;
        int maxNGrams = 2;
        for (int i = maxNGrams - 1; i > 0; i--) {
            System.out.println("looping through ngrams: " + (i + 1));
            itFreqList = setNGrams.entrySet().iterator();
            while (itFreqList.hasNext()) {
                entry = itFreqList.next();
                currWord = entry.getElement().trim();
                if (currWord.split(" ").length == i - 1) {
                    //special condition for i = 1 since this is a very simple case that does not need a heavy duty n-gram detection approach
                    if (i == 1) {
                        termsInBigram = currWord.split(" ");
                        for (int j = 0; j <= 1; j++) {
                            string = termsInBigram[j];
                            if (!setNGrams.contains(string)) {
                                continue;
                            } else if (setNGrams.count(string) < entry.getCount() * 2) {
                                wordsToBeRemoved.add(string);
                            }
                        }

                    } else {
                        setCurrentSubNGrams = NGramFinder.ngramsInString(i, currWord);
                        setCurrentSubNGramsIterator = setCurrentSubNGrams.iterator();
                        while (setCurrentSubNGramsIterator.hasNext()) {
                            string = setCurrentSubNGramsIterator.next().trim();
                            if (!setNGrams.contains(string)) {

                                continue;
                            } else if (setNGrams.count(string) < entry.getCount() * 2) {
                                wordsToBeRemoved.add(string);
                            }
                        }
                    }
                }
            }
        }


        System.out.println("number of terms to be removed: " + wordsToBeRemoved.size());
        loopOne.closeAndPrintClock();

        Clock loop2 = new Clock("loop2");
        itFreqList = setNGrams.entrySet().iterator();
        while (itFreqList.hasNext()) {
            boolean toRemain;
            entry = itFreqList.next();
            currWord = entry.getElement();
            toRemain = wordsToBeRemoved.add(currWord);

            //This line includes the condition for an important word to remain in the list of words, even if listed with stopwords.
            if (toRemain) {
                multisetWords.add(entry.getElement(), entry.getCount());
            }


        }
        loop2.closeAndPrintClock();
        deletingDuplicatesTime.addText("Number of words after removing redundant n-grams: " + multisetWords.elementSet().size());
        deletingDuplicatesTime.closeAndPrintClock();

        return multisetWords;

    }
}
