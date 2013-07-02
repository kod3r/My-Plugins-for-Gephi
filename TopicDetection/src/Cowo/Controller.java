/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cowo;

import Utils.Clock;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 *
 * FUNCTION OF THIS PROGRAM: Take a text as input, returns semantic networks as
 * an output.
 *
 * DETAIL OF OPERATIONS: 1. loads several stopwords files in memory 2. reads the
 * text file and does some housekeeping on it 3. lemmatization of the text 4.
 * extracts n-grams 5. housekeeping on n-grams, removal of least frequent terms
 * 6. removal of stopwords, removal of least frequent terms 7. removal of
 * redudant n-grams (as in: removal of "united states of" if "united stated of
 * America" exists frequently enough 8. determines all word co-occurrences for
 * each line of the text 9. prints vosViewer output 10. prints GML file 11.
 * print a short report of all these operations
 */
public class Controller {

    public static Multiset<String> freqSet = HashMultiset.create();
    public static LinkedHashMultimap<Integer, String> wordsPerLine = LinkedHashMultimap.create();
    public static LinkedHashMultimap<Integer, String> wordsPerLineFiltered = LinkedHashMultimap.create();
    public static Set<String> setOfWords = new HashSet();
    public static Multiset<String> multisetNGrams = TreeMultiset.create();
    public static Multiset<String> multisetOfWords = ConcurrentHashMultiset.create();
    public static Map<String, Integer> ngramsCountinCorpus = new HashMap();
    public static Multiset<String> future = ConcurrentHashMultiset.create();
    Multiset<String> setCombinations = ConcurrentHashMultiset.create();
    public static String[] stopwords;
    public static int occurrenceThreshold = 4;
    private static FileReader fr;
    public static int maxgram = 4;
    private final static int nbStopWords = 5000;
    private final static int nbStopWordsShort = 200;
    public final static int maxAcceptedGarbage = 3;
    //static int numberOfThreads = Runtime.getRuntime().availableProcessors();
    static int numberOfThreads = 7;
    private static Integer counterLines = 0;
    // logic of freqThreshold: the higher the number of stopwords filtered out, the lower the number of significant words which should be expected
    private static int freqThreshold = 400;
    public static String wordSeparator;
    private String wk;
    public static String wkOutput;
    private String textFileName;
    private String textFile;
    static String cleanWord;
    public static int counter = 0;
    public static int numberOfDocs;
    private static BufferedReader fileStopWords;
    public static String[] stopwordsShort;
    public static Set<String> setStopWords = new HashSet();
    public static Set<String> setNoLemma = new HashSet();
    public static int minWordLength = 3;
    public static Map<Integer, String> mapofLines = new HashMap();
    public static Set<String> setFreqWords = new HashSet();
    private static String fileMapName;
    private static BufferedWriter fileMapFile;
    private static String fileNetworkName;
    private static BufferedWriter fileNetworkFile;
    private static String fileParametersName;
    private static BufferedWriter fileParametersFile;
    public static Set<String> setStopWordsShort = new HashSet();
    public static Set<String> setKeepWords = new HashSet();
    private static BufferedReader fileNoLemma;
    private static String[] noLemmaArray;
    public static String[] keepWordsArray;
    static InputStream in10000 = Controller.class.getResourceAsStream("stopwords_10000_most_frequent_filtered.txt");
    static InputStream inOwn;
    static InputStream innolemma = Controller.class.getResourceAsStream("nolemmatization.txt");
    public static boolean binary = true;
    public static boolean filterDifficultChars = true;
    private Iterator<String> multisetNGramsIterator;
    private int absoluteNBTerms;

    public static Multimap<String, String> currMapTypeToText = HashMultimap.create();
    public static Multimap<String, String> overallMapTypeToText = HashMultimap.create();
    public static HashMap<String, String> overallMapTextToType = new HashMap();
    public static ExecutorService executor;
    public static HashMap<Integer, Future<String>> listFutures;
//    public static HashMultiset<String> multisetTypesOfEntities = HashMultiset.create();
    public static HashSet<String> setFilteredFields = new HashSet();
    

    public Controller() {
    }

    public String analyze(List<String> strings) throws IOException {


        System.out.println("---------------------------------");
        System.out.println();

        // #### 1. LOADING FILES CONTAINING STOPWORDS
        // Several sources of stopfiles are used.
        // Once transformed in array, they will be invoked by the StopWordsRemoverRT class

        Clock loadingStopWordsTime = new Clock("Loading the list of stopwords");

        fileNoLemma = new BufferedReader(new InputStreamReader(innolemma));
        noLemmaArray = fileNoLemma.readLine().split(",");
        setNoLemma.addAll(Arrays.asList(noLemmaArray));
        fileNoLemma.close();

        fileStopWords = new BufferedReader(new InputStreamReader(in10000));
        stopwords = fileStopWords.readLine().split(",");
        stopwords = Arrays.copyOf(stopwords, nbStopWords);
        fileStopWords.close();

        setStopWords.addAll(Arrays.asList(stopwords));
        stopwordsShort = Arrays.copyOf(stopwords, nbStopWordsShort);

        setStopWordsShort.addAll(Arrays.asList(stopwordsShort));

        loadingStopWordsTime.closeAndPrintClock();
        //-------------------------------------------------------------------------------------------------------------


        // ### 2. LOADING FILE IN MEMORY AND CLEANING  ...

        Clock loadingTime = new Clock("Loading text file: " + textFile);

        for (String currLine : strings) {
            if (!currLine.matches(".*\\w.*")) {
                continue;
            }
            counterLines++;
            currLine = TextCleaner.doBasicCleaning(currLine);
            mapofLines.put(counterLines, currLine);

        } // end looping through all lines of the original text file

        counterLines = 0;
        loadingTime.closeAndPrintClock();
        //### END of file reading---------------------------------------------------------

        numberOfDocs = mapofLines.keySet().size();
        System.out.println("nb of docs treated: " + numberOfDocs);



        // ### EXTRACTING set of NGrams
        multisetNGrams = NGramFinder.runIt(mapofLines);
//                if (multisetNGrams.contains("working memory")) {
//                    System.out.println("working memory present!");
//                    System.out.println("number is: " + multisetNGrams.count("working memory"));
//                }
        multisetNGrams = NGramCleaner.cleanIt(multisetNGrams);

        // ### LEMMATIZING
        Clock LemmatizerClock = new Clock("Lemmatizing");
        multisetNGrams = Lemmatizer.doLemmatizationReturnMultiSet(multisetNGrams);
        LemmatizerClock.addText("number of words after lemmatization: " + multisetNGrams.elementSet().size());
        //registering this number: it corresponds to the number of terms found before filtering out
        absoluteNBTerms = multisetNGrams.elementSet().size();
        LemmatizerClock.closeAndPrintClock();


        // ### REMOVAL SMALL WORDS

        Clock removalSmallWords = new Clock("removing words shorter than " + minWordLength + " characters");
        multisetNGramsIterator = multisetNGrams.iterator();
        while (multisetNGramsIterator.hasNext()) {
            if (multisetNGramsIterator.next().length() < minWordLength) {
                multisetNGramsIterator.remove();
            }
        }
        removalSmallWords.addText("number of words after the removal of short words: " + multisetNGrams.elementSet().size());
        removalSmallWords.closeAndPrintClock();


        //-------------------------------------------------------------------------------------------------------------        
        // #### 6. REMOVING STOPWORDS


        Clock stopwordsRemovalTime = new Clock("Removing stopwords");
        Iterator<Entry<String>> it = multisetNGrams.entrySet().iterator();
        Multiset<String> tempMultiset = HashMultiset.create();
        StopWordsRemover swr;
        while (it.hasNext()) {
            counter++;
            Entry<String> entry = it.next();
            swr = new StopWordsRemover(entry.getElement().trim(), entry.getCount());
            tempMultiset.addAll(swr.call());
        }
        multisetNGrams = HashMultiset.create();
        multisetNGrams.addAll(tempMultiset);

        counter = 0;
        counterLines = 0;
        stopwordsRemovalTime.addText("number of words after the removal of stopwords: " + multisetNGrams.elementSet().size());
        stopwordsRemovalTime.closeAndPrintClock();

        //-------------------------------------------------------------------------------------------------------------   
        // #### DELETES bi-grams trigrams and above, IF they are already contained in n+1 grams
        multisetNGrams = NGramDuplicatesCleaner.removeDuplicates(multisetNGrams);





        // #### SORTS TERMS BY FREQUENCY, TAKE THE MOST FREQUENT
        Clock filteringOutLowFrequencies = new Clock("Keeping only the  " + freqThreshold + " most frequent words in the corpus");
        multisetNGrams = Multisets.copyHighestCountFirst(multisetNGrams);
        multisetNGramsIterator = multisetNGrams.elementSet().iterator();
        tempMultiset = HashMultiset.create();
        int count = 0;
        String string = "";
        while (multisetNGramsIterator.hasNext()) {
            count++;
            string = multisetNGramsIterator.next();
            tempMultiset.add(string, multisetNGrams.count(string));
            if (count == freqThreshold) {
                break;
            }
        }

        return string;

    }
}
