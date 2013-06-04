/*
 Copyright 2008-2011 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

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
package ToolUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.plugin.partition.PartitionBuilder;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.layout.api.LayoutController;
import org.gephi.layout.plugin.forceAtlas.ForceAtlas;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.tools.spi.MouseClickEventListener;
import org.gephi.tools.spi.NodeClickEventListener;
import org.gephi.tools.spi.Tool;
import org.gephi.tools.spi.ToolEventListener;
import org.gephi.tools.spi.ToolSelectionType;
import org.gephi.tools.spi.ToolUI;
import org.gephi.visualization.VizController;
import org.gephi.visualization.opengl.text.ColorMode;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Tool which reacts to clicks on the canvas by adding nodes and edges.
 * <p>
 * The tool works with two
 * <code>ToolEventListener</code> listeners: One {@link MouseClickEventListener}
 * to react on a click on a empty part of the canvas and one
 * {@link NodeClickEventListener} to react on a click on multiple nodes. The
 * tool is creating a node at the mouse location and adds edges from the newly
 * created node to all selected nodes. That works when the user increases its
 * mouse selection area.
 * <p>
 * The tool also uses some non-api methods of
 * <code>VizController</code>. It's not really recommended at this point but we
 * needed it for the mouse position. The new Visualization API coming in a
 * future version will expose much more things...
 * <p>
 * This tool class also has an UI class which displays a simple checkbox in the
 * properties bar. The checkbox triggers a layout algorithm.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Tool.class)
public class ShowLabels implements Tool {

    private final ShowLabelsToolUI ui = new ShowLabelsToolUI();
    private static GraphController gc;
    private static Graph graph;
    private static AttributeController ac;
    private static GraphView mainView;
    private static AttributeColumn col;

    @Override
    public void select() {
        gc = Lookup.getDefault().lookup(GraphController.class);
        graph = gc.getModel().getGraph();
        ac = Lookup.getDefault().lookup(AttributeController.class);
        mainView = graph.getView();
        col = ac.getModel().getNodeTable().getColumn("Modularity Class");
        if (col == null) {
            unselect();
        }

    }

    @Override
    public void unselect() {
    }

    @Override
    public ToolEventListener[] getListeners() {
        return new ToolEventListener[]{
            new MouseClickEventListener() {
                @Override
                public void mouseClick(int[] positionViewport, float[] position3d) {
                    //Get current graph
//                            GraphController gc = Lookup.getDefault().lookup(GraphController.class);
//                            Graph graph = gc.getModel().getGraph();
//                            GraphFactory factory = gc.getModel().factory();
//
//                            //Add node
//                            Node node = factory.newNode();
//                            node.getNodeData().setX(position3d[0]);
//                            node.getNodeData().setY(position3d[1]);
//                            node.getNodeData().setSize(10f);
//                            graph.addNode(node);
                }
            },
            new NodeClickEventListener() {
                @Override
                public void clickNodes(Node[] nodes) {
                    //Get mouse position
//                            float[] position3d = VizController.getInstance().getGraphIO().getMousePosition3d();
//
//                            //Get current graph
//                            GraphController gc = Lookup.getDefault().lookup(GraphController.class);
//                            Graph graph = gc.getModel().getGraph();
//                            GraphFactory factory = gc.getModel().factory();
//
//                            //Add node
//                            Node node = factory.newNode();
//                            node.getNodeData().setX(position3d[0]);
//                            node.getNodeData().setY(position3d[1]);
//                            node.getNodeData().setSize(10f);
//                            graph.addNode(node);
//
//                            //Add edges with the clicked nodes
//                            for (Node n : nodes) {
//                                Edge edge = factory.newEdge(node, n);
//                                graph.addEdge(edge);
//                            }
                }
            }};
    }

    @Override
    public ToolUI getUI() {
        return ui;
    }

    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION;
    }

    private static class ShowLabelsToolUI implements ToolUI {

        private boolean communityLabelsSelected = false;
        private boolean roleLabelsSelected = false;

        @Override
        public JPanel getPropertiesBar(Tool tool) {
            JPanel labelsUI = new JPanel();
            final JCheckBox checkBoxCommunityLabels;
            final JCheckBox checkBoxRoles;
            //Add a checkbox in the property bar to run a layout algorithm
            checkBoxRoles = new JCheckBox("Show roles");
            checkBoxRoles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxRoles.isSelected()) {
                        roleLabelsSelected = true;
                        if (communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if ((Integer) node.getAttributes().getValue("Modularity Class") == -1) {
                                node.getNodeData().getTextData().setVisible(true);
                                graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(true);

                            } else if (communityLabelsSelected) {
                                if ((Integer) node.getAttributes().getValue("Modularity Class") == -2) {
                                    node.getNodeData().getTextData().setVisible(true);
                                    graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(true);

                                } else {
                                    node.getNodeData().getTextData().setVisible(false);
                                    graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(false);

                                }
                            }
                        }
                    } else {
                        roleLabelsSelected = false;
                        for (Node node : graph.getNodes().toArray()) {
                            if (communityLabelsSelected & (Integer) node.getAttributes().getValue("Modularity Class") == -2) {
                                node.getNodeData().getTextData().setVisible(true);
                            } else {
                                node.getNodeData().getTextData().setVisible(false);
                                graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(false);

                            }
                        }
                    }

                }
            });


            checkBoxCommunityLabels = new JCheckBox("Show community labels");
            checkBoxCommunityLabels.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxCommunityLabels.isSelected()) {
                        communityLabelsSelected = true;

                        ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                        VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);

                        for (Node node : graph.getNodes().toArray()) {
                            if ((Integer) node.getAttributes().getValue("Modularity Class") == -2) {
                                node.getNodeData().getTextData().setVisible(true);
                                graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(true);
                            } else if (roleLabelsSelected) {
                                if ((Integer) node.getAttributes().getValue("Modularity Class") == -1) {
                                    node.getNodeData().getTextData().setVisible(true);
                                    graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(true);

                                } else {
                                    node.getNodeData().getTextData().setVisible(false);
                                    graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(false);

                                }
                            }
                        }
                    } else {
                        communityLabelsSelected = false;
                        for (Node node : graph.getNodes().toArray()) {
                            if (roleLabelsSelected & (Integer) node.getAttributes().getValue("Modularity Class") == -1) {
                                node.getNodeData().getTextData().setVisible(true);
                                graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(true);

                            } else {
                                node.getNodeData().getTextData().setVisible(false);
                                graph.getNeighbors(node).toArray()[0].getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });
            labelsUI.add(checkBoxRoles);

            labelsUI.add(checkBoxCommunityLabels);
            return labelsUI;
        }

        @Override
        public Icon getIcon() {
            return new ImageIcon(getClass().getResource("/Resources/plus.png"));
        }

        @Override
        public String getName() {
            return "Show Labels";
        }

        @Override
        public String getDescription() {
            return "Show labels created by the influencers detection plugin";
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
}
