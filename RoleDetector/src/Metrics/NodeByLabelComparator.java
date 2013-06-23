package Metrics;

import java.util.Comparator;
import org.gephi.graph.api.Node;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class NodeByLabelComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        //return o1.getNodeData().getId().compareTo(o2.getNodeData().getId());
        return o1.getNodeData().getLabel().compareTo(o2.getNodeData().getLabel());
    }
}
