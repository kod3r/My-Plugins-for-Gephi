/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunityLabelFinder;

import Utils.StatusCleaner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public final class StopWordsRemover {

    private String entryWord;
    boolean multipleWord;
    private Multiset<String> multisetToReturn = HashMultiset.create();
    private Set<String> stopwords;

    public StopWordsRemover(Set<String> stopwords) {
        this.stopwords = stopwords;

    }

    //@Override
    public Multiset<String> process(Multiset<String> terms) {
        StatusCleaner sc = new StatusCleaner();
        terms = sc.removeSmallWords(terms);
        for (String string : terms.elementSet()) {
            boolean write = true;
            entryWord = string.replaceAll(" +", " ");

            if (stopwords.contains(entryWord)) {
                write = false;
            }

            String[] wordsNGrams = entryWord.split(" ");
            for (int i = 0; i < wordsNGrams.length; i++) {
                if (stopwords.contains(wordsNGrams[i])) {
                    write = false;
                }
            }

            if (string.length() <= 3 & string.contains(" ")) {
                write = false;
            }


            if (write) {
                multisetToReturn.add(entryWord, terms.count(entryWord));
            }
        }
        return multisetToReturn;
    }
}