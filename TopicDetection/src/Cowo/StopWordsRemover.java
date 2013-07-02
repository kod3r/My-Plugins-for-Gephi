/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cowo;

import com.google.common.collect.HashMultiset;

/**
 *
 * @author C. Levallois
 */
public final class StopWordsRemover {

    //private final Entry<String> entry;
    private String entryWord;
    boolean multipleWord;
    //private SnowballStemmer stemmer;
    //private static String lang = "english";
    private final int entryCount;
    private HashMultiset<String> multisetToReturn;
    //static int numberOfThreads = Runtime.getRuntime().availableProcessors();
    //static int numberOfThreads = 7;
    //private static ExecutorService pool = Executors.newCachedThreadPool();

//    StopWordsRemoverWT(Entry<String> entry) {
//
//        this.entry = entry;
//        this.entryWord = entry.getElement().toLowerCase().trim();
//        this.entryCount = entry.getCount();
//        //run();
//    }
    StopWordsRemover(String element, int entryCount) {
        this.entryWord = element.replaceAll(" +", " ");
        this.entryCount = entryCount;
        this.multisetToReturn = HashMultiset.create();


    }

    //@Override
    public HashMultiset<String> call() {

        boolean write = true;


        String[] wordsNGrams = entryWord.split(" ");
        for (int i = 0; i < wordsNGrams.length; i++) {
            if (Controller.setStopWords.contains(wordsNGrams[i])) {
                write = false;
            }
        }


        if (write) {
            multisetToReturn.add(entryWord, entryCount);
//            System.out.println("term added!");
//                if ("risk".equals(entryWord)){
//                    System.out.println("risk added to filteredFreqSet "+entryCount);
//                }


//                return toReturn;
        }

        return multisetToReturn;
    }
}