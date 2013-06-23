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

import Control.GeneralController;
import FilteringOperations.FilterOnePartition;
import Layout.Layout;
import Model.Community;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
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

    static private String selectedString(ItemSelectable is) {
        Object selected[] = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    private static class ShowLabelsToolUI implements ToolUI {

        private boolean communityLabelsSelected = false;

        @Override
        public JPanel getPropertiesBar(Tool tool) {
            JPanel labelsUI = new JPanel();
            final JCheckBox checkBoxCommunityLabels;
            final JCheckBox checkBoxRockStars;
            final JCheckBox checkBoxSpecialists;
            final JCheckBox checkBoxLocalConnectors;
            final JCheckBox checkBoxCommunityBridgers;
            final JCheckBox checkBoxGlobalAuthorities;
            final JButton labelAdjustButton;

            labelAdjustButton = new JButton("fix labels");
            labelAdjustButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Layout layout = new Layout(graph.getGraphModel());
                    layout.executeLabelAdjust();
                }
            });

            // add a selector for communities
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            List<String> labelsCommunities = new ArrayList();
            for (Community community : GeneralController.getCommunities()) {
                labelsCommunities.add(community.getLabel());
            }
            Collections.sort(labelsCommunities);
            model.addElement("show all communities");
            for (String label : labelsCommunities) {
                model.addElement(label);
            }

            JComboBox comboBox = new JComboBox(model);
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    System.out.println("Command: " + actionEvent.getActionCommand());
                    ItemSelectable is = (ItemSelectable) actionEvent.getSource();
                    String selectedCommunity = selectedString(is);
                    int idCommunity = -99;
                    for (Community community : GeneralController.getCommunities()) {
                        if (selectedCommunity.equals(community.getLabel())) {
                            idCommunity = community.getId();
                        } else if (selectedCommunity.equals("show all communities")) {
                            idCommunity = -99;
                        }
                    }
                    FilterOnePartition fop = new FilterOnePartition(graph);
                    fop.partitionInteger("Modularity Class", idCommunity);

                }
            };



            comboBox.addActionListener(actionListener);
            //Add a checkbox in the property bar to run a layout algorithm
            checkBoxRockStars = new JCheckBox("rock stars");

            checkBoxRockStars.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxRockStars.isSelected()) {
                        if (!communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("rock star")) {
                                node.getNodeData().getTextData().setVisible(true);
                            }
                        }
                    } else {
                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("rock star")) {
                                node.getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });

            checkBoxSpecialists = new JCheckBox("specialists");

            checkBoxSpecialists.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxSpecialists.isSelected()) {
                        if (!communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("specialist")) {
                                node.getNodeData().getTextData().setVisible(true);
                            }
                        }
                    } else {
                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("specialist")) {
                                node.getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });

            checkBoxLocalConnectors = new JCheckBox("local auth.");

            checkBoxLocalConnectors.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxLocalConnectors.isSelected()) {
                        if (!communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("local authority")) {
                                node.getNodeData().getTextData().setVisible(true);
                            }
                        }
                    } else {
                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("local authority")) {
                                node.getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });

            checkBoxCommunityBridgers = new JCheckBox("bridgers");

            checkBoxCommunityBridgers.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxCommunityBridgers.isSelected()) {
                        if (!communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("community bridger")) {
                                node.getNodeData().getTextData().setVisible(true);
                            }
                        }
                    } else {
                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("community bridger")) {
                                node.getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });

            checkBoxGlobalAuthorities = new JCheckBox("global auth.");

            checkBoxGlobalAuthorities.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxGlobalAuthorities.isSelected()) {
                        if (!communityLabelsSelected) {
                            ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                            VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        }

                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("global authority")) {
                                node.getNodeData().getTextData().setVisible(true);
                            }
                        }
                    } else {
                        for (Node node : graph.getNodes().toArray()) {
                            if (node.getAttributes().getValue("role").equals("global authority")) {
                                node.getNodeData().getTextData().setVisible(false);
                            }
                        }
                    }

                }
            });


            checkBoxCommunityLabels = new JCheckBox("communities");

            checkBoxCommunityLabels.addActionListener(
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBoxCommunityLabels.isSelected()) {
                        communityLabelsSelected = true;

                        ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                        VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[1]);

                        for (Node node : graph.getNodes().toArray()) {
                            if ((Integer) node.getAttributes().getValue("Modularity Class") >1000) {
                                node.getNodeData().getTextData().setVisible(true);
                            }

                        }
                    } else {
                        communityLabelsSelected = false;
                        ColorMode[] cm = VizController.getInstance().getTextManager().getColorModes();
                        VizController.getInstance().getTextManager().getModel().setColorMode((ColorMode) cm[0]);
                        for (Node node : graph.getNodes().toArray()) {
                            if ((Integer) node.getAttributes().getValue("Modularity Class") > 1000) {
                                node.getNodeData().getTextData().setVisible(false);
                            }

                        }
                    }
                }
            });
            labelsUI.add(checkBoxRockStars);

            labelsUI.add(checkBoxGlobalAuthorities);

            labelsUI.add(checkBoxCommunityBridgers);

            labelsUI.add(checkBoxCommunityLabels);

            labelsUI.add(checkBoxLocalConnectors);

            labelsUI.add(checkBoxSpecialists);

            labelsUI.add(comboBox);
            labelsUI.add(labelAdjustButton);
            return labelsUI;
        }

        @Override
        public Icon getIcon() {
            return new ImageIcon(getClass().getResource("/Resources/plus.png"));
        }

        @Override
        public String getName() {
            return "Show roles";
        }

        @Override
        public String getDescription() {
            return "Show labels created by the detector of roles";
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
}
