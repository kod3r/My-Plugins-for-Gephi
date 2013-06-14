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



        HashMultiset multisetWords = HashMultiset.create();


        Iterator<Multiset.Entry<String>> itFreqList;
        Set<String> wordsToBeRemoved = new HashSet();
        Multiset.Entry<String> entry;
        String currWord;
        Set<String> setCurrentSubNGrams;
        Iterator<String> setCurrentSubNGramsIterator;
        String string;
        String[] termsInBigram;
        int maxNGrams = 2;
        itFreqList = setNGrams.entrySet().iterator();
        while (itFreqList.hasNext()) {
            entry = itFreqList.next();
            currWord = entry.getElement().trim();
            if (!currWord.contains(" ")) {
                continue;
            }
            setCurrentSubNGrams = new HashSet();
            for (int i = maxNGrams-1; i > 0; i--) {
                setCurrentSubNGrams.addAll(NGramFinder.ngramsInString(i, currWord));
            }
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

        itFreqList = setNGrams.entrySet().iterator();

        while (itFreqList.hasNext()) {
            boolean toRemain;
            entry = itFreqList.next();
            currWord = entry.getElement();
            toRemain = wordsToBeRemoved.add(currWord);

            if (toRemain) {
                multisetWords.add(entry.getElement(), entry.getCount());
            }


        }
        return multisetWords;
    }
}
