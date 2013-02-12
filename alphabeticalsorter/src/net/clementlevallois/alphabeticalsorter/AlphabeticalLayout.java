/*
 Copyright 2013 Clement Levallois
 Authors : Clement Levallois <clement.levallois@gephi.org>
 Website : http://www.clementlevallois.net


 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Clement Levallois. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */

package net.clementlevallois.alphabeticalsorter;


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
