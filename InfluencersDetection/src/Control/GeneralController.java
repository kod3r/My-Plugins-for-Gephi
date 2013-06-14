package Control;

import CommunityLabelFinder.ControllerLabelsFinder;
import Layout.LabelPositioner;
import Layout.Layout;
import Partition.ColorPartitions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.clementlevallois.classes.Community;
import net.clementlevallois.classes.LabelCreation;
import net.clementlevallois.classes.NodeMetricsComputer;
import net.clementlevallois.classes.RoleAlgo;
import net.clementlevallois.classes.TempMetrics;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.Modularity;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.visualization.VizController;

/**
 *
 * @author Clement Levallois
 */
public class GeneralController implements Statistics, LongTask {

    private boolean cancel = false;
    private ProgressTicket progressTicket;
    private static String colFollowers;
    private static int nbCommunities;
    private Graph graph;
    private static List<Community> communities;
    private static int minCommunitySize = 10;
    private static int medianInDegree;

    @Override
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        this.graph = graphModel.getGraphVisible();
        graph.readLock();
        Map<Integer, TempMetrics> tempMap = new HashMap();
        communities = new ArrayList();


        AttributeTable nodeTable = attributeModel.getNodeTable();

        //finds the name of the followers column
        for (AttributeColumn col : nodeTable.getColumns()) {
            if (col.getTitle().toLowerCase().contains("follower")) {
                colFollowers = col.getTitle();
            }
        }

        //creates a "role" column
        AttributeColumn roleColumn = nodeTable.getColumn("role");
        if (roleColumn != null) {
            nodeTable.removeColumn(roleColumn);
            roleColumn = nodeTable.addColumn("role", "Role", AttributeType.STRING, AttributeOrigin.COMPUTED, "");
        } else {
            roleColumn = nodeTable.addColumn("role", "Role", AttributeType.STRING, AttributeOrigin.COMPUTED, "");
        }

        //remove any existing modularity column
        AttributeColumn modularityColumn = nodeTable.getColumn("Modularity Class");
        if (modularityColumn != null) {
            nodeTable.removeColumn(modularityColumn);
        }


        try {
            Progress.start(progressTicket, graph.getNodeCount());

            double initModularity = 0;
            double newModularity = 0;
            boolean continueLoop = true;
            float resolution = 0.7f;
            //finds communities, until a minimum differentiation between communities is achieved
            while (continueLoop) {
                Modularity mod = new Modularity();
                mod.setResolution(resolution);
                mod.execute(graphModel, attributeModel);
                newModularity = mod.getModularity();
                if (newModularity > initModularity) {
                    continueLoop = true;
                    resolution = resolution + 0.03f;
                    initModularity = newModularity;
                } else {
                    System.out.println("final resolution: " + resolution);
                    System.out.println("modularity achieved: " + newModularity);
                    continueLoop = false;
                }
            }


            //computes degree metrics
            Degree degree = new Degree();
            degree.execute(graphModel, attributeModel);

            //data structure to compute median indegree;
            List<Integer> sortedInDegrees = new ArrayList();

            for (Node node : graph.getNodes()) {
                TempMetrics tm = new TempMetrics();
                tm.setRole("agent");
                int nbFollowers = 0;
                try {
                    nbFollowers = (Integer) node.getNodeData().getAttributes().getValue(colFollowers);
                } catch (NullPointerException e) {
                    nbFollowers = 0;
                } catch (ClassCastException c) {
                    if (node.getNodeData().getAttributes().getValue(colFollowers) instanceof Double) {
                        double nbFollowersDouble = (Double) node.getNodeData().getAttributes().getValue(colFollowers);
                        nbFollowers = (int) nbFollowersDouble;
                    }
                }
                tm.setFollowers(nbFollowers);
                tm.setCommunity((Integer) node.getAttributes().getValue(Modularity.MODULARITY_CLASS));
                if (!graph.getGraphModel().isUndirected()) {
                    tm.setInDegree((Integer) node.getAttributes().getValue(Degree.INDEGREE));
                    tm.setOutDegree((Integer) node.getAttributes().getValue(Degree.OUTDEGREE));
                }

                tm.setDegree((Integer) node.getAttributes().getValue(Degree.DEGREE));
                tempMap.put(node.getId(), tm);
                if (cancel) {
                    break;
                }

                //adds to the data structure to compute the median IndeGree;
                sortedInDegrees.add(tm.getInDegree());


                //updates the list of communities;
                Community community = new Community();
                community.setId(tm.getCommunity());
                int index = communities.indexOf(community);
                if (index != -1) {
                    community = communities.get(index);
                    community.incrementSize();
                    communities.set(index, community);
                } else {
                    community.incrementSize();
                    communities.add(community);
                }
            }

            nbCommunities = communities.size();

            //compute the median Indegree()
            Collections.sort(sortedInDegrees);
            medianInDegree = sortedInDegrees.get(Math.round((float) sortedInDegrees.size() / 2));

            //colorizes communities with "I want hue" colors
            ColorPartitions.colorize(graph);


            //detects roles
            NodeMetricsComputer roleComputer = new NodeMetricsComputer(tempMap, graph);
            tempMap = roleComputer.runMetrics();
            RoleAlgo ra = new RoleAlgo(graph, tempMap);
            tempMap = ra.detectRoles();


            //writes roles to Data Laboratory
            for (Integer id : tempMap.keySet()) {
                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue(roleColumn, tempMap.get(id).getRole());
                if (cancel) {
                    break;
                }
            }

            //detects labels for each community
            ControllerLabelsFinder clf = new ControllerLabelsFinder(graph, attributeModel, tempMap);
            clf.detectLabels();

            //creates labels on the graph for roles and labels of community
            LabelCreation lc = new LabelCreation(graph, attributeModel, tempMap);
            lc.create();


            //shows labels on the graph, sets backgroundcolor to black
            VizController.getInstance().getTextManager().getModel().setShowNodeLabels(true);
            VizController.getInstance().getVizModel().setBackgroundColor(Color.BLACK);

            //layout to dispose the labels nicely
            Layout layout = new Layout(graphModel);
            layout.setZoom(true, "out", 2f);
            layout.executeForceAtlas2Layout();
//            LabelPositioner lp = new LabelPositioner(graph);
//            lp.execute();
            layout.executeLabelAdjust();


            Progress.progress(progressTicket);
            graph.readUnlockAll();
        } catch (Exception e) {
            System.out.println("exception: " + e);
            e.printStackTrace();
            graph.readUnlockAll();
        }
//    catch (Exception e
//
//    
//        ) {
//            e.printStackTrace();
//        //Unlock graph
//        graph.readUnlockAll();
//    }
    }

    @Override
    public String getReport() {
        String report = "<HTML> <BODY> <h1>Detection of Roles Report </h1> "
                + "<hr>"
                + "<br> <h2> Results: </h2>"
                + "Average number: " + "value here" + "<br />"
                + "Number of unconnected nodes: " + "<br />"
                + "</BODY></HTML>";
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    public static int getNbCommunities() {
        return nbCommunities;
    }

    public static List<Community> getCommunities() {
        return communities;
    }

    public static void setCommunities(List<Community> communities) {
        GeneralController.communities = communities;
    }

    public static int getMinCommunitySize() {
        return minCommunitySize;
    }

    public static int getMedianInDegree() {
        return medianInDegree;
    }
}
