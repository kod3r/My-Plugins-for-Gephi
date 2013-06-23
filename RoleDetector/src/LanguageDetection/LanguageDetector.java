/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LanguageDetection;

import LanguageDetection.Cyzoku.Detector;
import LanguageDetection.Cyzoku.DetectorFactory;
import LanguageDetection.Cyzoku.LangDetectException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class LanguageDetector {

    Detector detector;

    public LanguageDetector() {
        try {
            detector = DetectorFactory.create();
        } catch (LangDetectException ex) {
            Logger.getLogger(LanguageDetector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String detect(String status) throws LangDetectException {
//        System.out.println("status: " + status);
        String lang;

        if (status != null && status.trim().split(" ").length > 2) {
            detector.append(status);
//            System.out.println("text: " + status);
            lang = detector.detect();
        } else {
            lang = null;
        }
//        System.out.println("lang detected: " + lang);
        return lang;
    }
}
