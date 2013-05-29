/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author C. Levallois
 */
public class RoleAlgo {

    Map<Integer, TempMetrics> map;

    public RoleAlgo(Map<Integer, TempMetrics> map) {
        this.map = map;
    }

    public Map<Integer, TempMetrics> detectRoles() {
        commmunityBridgers();
        return map;
    }

    public void commmunityBridgers() {
        Iterator<Map.Entry<Integer, TempMetrics>> mapEntryIterator;
        mapEntryIterator = map.entrySet().iterator();
        while (mapEntryIterator.hasNext()) {
            Map.Entry<Integer, TempMetrics> entry = mapEntryIterator.next();
            TempMetrics tm = entry.getValue();
            if (tm.isIsHighestInDegreeWithDifferentCommunities()) {
                tm.setRole("community bridger");
            }
        }


    }
}
