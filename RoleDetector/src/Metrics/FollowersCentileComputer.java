/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Metrics;

import Model.TempMetrics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class FollowersCentileComputer {

    private Map<Integer, TempMetrics> map;
    private List<Integer> sortedFollowers;

    public FollowersCentileComputer(Map<Integer, TempMetrics> map, List<Integer> sortedFollowers) {
        this.map = map;
        this.sortedFollowers = sortedFollowers;
    }

    public void compute() {
            int centile;
            int listSize = sortedFollowers.size();

        for (Integer id : map.keySet()) {
            TempMetrics tm = map.get(id);
            int nbFollowers = tm.getFollowers();
            Iterator<Integer> it = sortedFollowers.listIterator();
            while (it.hasNext()) {
                Integer integer = it.next();
                if (nbFollowers == integer) {
                    centile = Math.round(sortedFollowers.indexOf(integer) * 100 / (float) listSize);
                    tm.setCentileFollowers(centile);
                    break;
                }
            }
        }
    }
}
