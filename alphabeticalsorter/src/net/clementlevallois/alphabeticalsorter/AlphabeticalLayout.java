package net.clementlevallois.alphabeticalsorter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

public class AlphabeticalLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private Map<String, Integer> mapSortedNodes;
    private double size;
    private boolean inverseOrder;

    public AlphabeticalLayout(LayoutBuilder layoutBuilder, double size, boolean inverseOder) {
        super(layoutBuilder);
        this.size = size;
        this.inverseOrder = inverseOrder;
    }

    @Override
    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
    }

    @Override
    public void setGraphModel(GraphModel gm) {
        this.graphModel = gm;
    }

    @Override
    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        mapSortedNodes = new TreeMap();
        double y = 0;
        String label;
        for (Node n : graph.getNodes()) {
            label = n.getNodeData().getLabel();
            if (label == null) {
                continue;
            }
            mapSortedNodes.put(n.getNodeData().getLabel(), n.getId());
        }
        Node n;
        List<String> listNodes = new ArrayList(mapSortedNodes.keySet());
        Collections.sort(listNodes);
        if (!inverseOrder) {
            Collections.reverse(listNodes);
        }
        for (String string : listNodes) {
            n = graph.getNode(mapSortedNodes.get(string));
            n.getNodeData().setX((float) (00));
            n.getNodeData().setY((float) (y));
            y = y + size;

        }
        converged = true;

    }

    @Override
    public boolean canAlgo() {
        return !converged;
    }

    @Override
    public void endAlgo() {
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "Alphabet.spaceSize.name"),
                    null,
                    "Alphabet.spaceSize.name",
                    NbBundle.getMessage(getClass(), "Alphabet.spaceSize.desc"),
                    "getSize", "setSize"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "Alphabet.invertedOrder.name"),
                    null,
                    "Alphabet.invertedOrder.name",
                    NbBundle.getMessage(getClass(), "Alphabet.invertedOrder.desc"),
                    "isInverseOrder", "setInverseOrder"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[1]);
    }

    public void resetPropertiesValues() {
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Boolean isInverseOrder() {
        return inverseOrder;
    }

    public void setInverseOrder(Boolean inverseOrder) {
        this.inverseOrder = inverseOrder;
    }
}
