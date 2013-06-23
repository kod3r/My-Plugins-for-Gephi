/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CommunityLabelFinder;

import java.util.Locale;

/**
 *
 * @author C. Levallois
 */
public class CountryCodes {

    public static String get(String countryCode) {
        Locale locale = new Locale(countryCode);
        return locale.getDisplayLanguage();
    }
}
