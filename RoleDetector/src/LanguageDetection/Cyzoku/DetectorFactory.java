package LanguageDetection.Cyzoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import LanguageDetection.Cyzoku.util.LangProfile;
import java.io.InputStream;

/**
 * Language Detector Factory Class
 *
 * This class manages an initialization and constructions of {@link Detector}.
 *
 * Before using language detection library, load profiles with
 * {@link DetectorFactory#loadProfile(String)} method and set initialization
 * parameters.
 *
 * When the language detection, construct Detector instance via
 * {@link DetectorFactory#create()}. See also {@link Detector}'s sample code.
 *
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 *
 * @see Detector
 * @author Nakatani Shuyo
 */
public class DetectorFactory {

    public HashMap<String, double[]> wordLangProbMap;
    public ArrayList<String> langlist;
    public Long seed = null;

    private DetectorFactory() {
        wordLangProbMap = new HashMap();
        langlist = new ArrayList();
    }
    static private DetectorFactory instance_ = new DetectorFactory();

    /**
     * Load profiles from specified directory. This method must be called once
     * before language detection.
     *
     * @param profileDirectory profile directory path
     * @throws LangDetectException Can't open profiles(error code =
     * {@link ErrorCode#FileLoadError}) or profile's format is wrong (error code
     * = {@link ErrorCode#FormatError})
     */
    public static void loadProfileChooseSource(String profileDirectory, boolean isResourceFolder) throws LangDetectException, IOException {
        if (!isResourceFolder) {
            loadProfileFromDirectory(new File(profileDirectory));
        } else {
            loadProfileFromResources(profileDirectory);
        }
    }

    private static void loadProfileFromResources(String profileDirectory) throws LangDetectException, IOException {
        String[] languages = {"af", "ar", "bg", "bn", "cs", "da", "de", "el", "en", "es", "et", "fa", "fi", "fr", "gu", "he", "hi", "hr", "hu", "id", "it", "ja", "kn", "ko", "lt", "lv", "mk", "ml", "mr", "ne", "nl", "no", "pa", "pl", "pt", "ro", "ru", "sk", "sl", "so", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh-cn", "zh-tw"};
        InputStream is = null;
        int langsize = languages.length;
        int index = 0;

        for (String language : languages) {
            try {
                String currLanguage = profileDirectory + language;
                is = DetectorFactory.class.getResourceAsStream(currLanguage);
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + language + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + language + "'");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * Load profiles from specified directory. This method must be called once
     * before language detection.
     *
     * @param profileDirectory profile directory path
     * @throws LangDetectException Can't open profiles(error code =
     * {@link ErrorCode#FileLoadError}) or profile's format is wrong (error code
     * = {@link ErrorCode#FormatError})
     */
    private static void loadProfileFromDirectory(File profileDirectory) throws LangDetectException {
        File[] listFiles = profileDirectory.listFiles();
        if (listFiles == null) {
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Not found profile: " + profileDirectory);
        }

        int langsize = listFiles.length, index = 0;
        for (File file : listFiles) {
            if (file.getName().startsWith(".") || !file.isFile()) {
                continue;
            }
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + file.getName() + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + file.getName() + "'");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * @param profile
     * @param langsize
     * @param index
     * @throws LangDetectException
     */
    static /* package scope */ void addProfile(LangProfile profile, int index, int langsize) throws LangDetectException {
        String lang = profile.name;
        if (instance_.langlist.contains(lang)) {
            System.out.println("duplicate the same language profile");
            return;
        }
        instance_.langlist.add(lang);
        for (String word : profile.freq.keySet()) {
            if (!instance_.wordLangProbMap.containsKey(word)) {
                instance_.wordLangProbMap.put(word, new double[langsize]);
            }
            int length = word.length();
            if (length >= 1 && length <= 3) {
                double prob = profile.freq.get(word).doubleValue() / profile.n_words[length - 1];
                instance_.wordLangProbMap.get(word)[index] = prob;
            }
        }
    }

    /**
     * for only Unit Test
     */
    static /* package scope */ void clear() {
        instance_.langlist.clear();
        instance_.wordLangProbMap.clear();
    }

    /**
     * Construct Detector instance
     *
     * @return Detector instance
     * @throws LangDetectException
     */
    static public Detector create() throws LangDetectException {
        return createDetector();
    }

    /**
     * Construct Detector instance with smoothing parameter
     *
     * @param alpha smoothing parameter (default value = 0.5)
     * @return Detector instance
     * @throws LangDetectException
     */
    public static Detector create(double alpha) throws LangDetectException {
        Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    static private Detector createDetector() throws LangDetectException {
        if (instance_.langlist.isEmpty()) {
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "need to load profiles");
        }
        Detector detector = new Detector(instance_);
        return detector;
    }

    public static void setSeed(long seed) {
        instance_.seed = seed;
    }

    public static final List<String> getLangList() {
        return Collections.unmodifiableList(instance_.langlist);
    }
}
