package net.clementlevallois.classes;

import java.util.HashMap;
import java.util.Map;
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

/**
 *
 * @author Jaroslav Kuchar
 */
public class InfluencersDetectionMetric implements Statistics, LongTask {

    private boolean cancel = false;
    private ProgressTicket progressTicket;
    private Node sourceNode;
    private String role = "";
    private String average = "";
    private int numberOfUnconnected = 0;

    public void setSourceNode(Node source) {
        this.sourceNode = source;
    }

    @Override
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        Graph graph = graphModel.getGraphVisible();
        graph.readLock();
        Map<Integer, TempMetrics> tempMap = new HashMap();


        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn roleColumn = nodeTable.getColumn("role");
        if (roleColumn == null) {
            roleColumn = nodeTable.addColumn("role", "Role", AttributeType.STRING, AttributeOrigin.COMPUTED, "");
        }
        try {
            Progress.start(progressTicket, graph.getNodeCount());
            Modularity mod = new Modularity();
            mod.execute(graphModel, attributeModel);
            Degree degree = new Degree();
            degree.execute(graphModel, attributeModel);
            System.out.println("allooo!");

            for (Node node : graph.getNodes()) {
                TempMetrics tm = new TempMetrics();
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
            }

            NodeMetricsComputer roleComputer = new NodeMetricsComputer(tempMap, graph);
            tempMap = roleComputer.runMetrics();
            RoleAlgo ra = new RoleAlgo(tempMap);
            tempMap = ra.detectRoles();

            for (Integer id : tempMap.keySet()) {

                ((AttributeRow) graph.getNode(id).getNodeData().getAttributes()).setValue(roleColumn, tempMap.get(id).getRole());
                if (cancel) {
                    break;
                }
            }

//            AttributeColumn modularityCol = nodeTable.getColumn(Modularity.MODULARITY_CLASS);
//            nodeTable.removeColumn(modularityCol);

            Progress.progress(progressTicket);
            this.average = "";
            graph.readUnlockAll();
        } catch (Exception e) {
            e.printStackTrace();
            //Unlock graph
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

    public String getAverage() {
        return average;
    }

    @Override
    public String getReport() {
        String report = "<HTML> <BODY> <h1>Erdös Number Report </h1> "
                + "<hr>"
                + "<br> <h2> Results: </h2>"
                + "Average number: " + getAverage() + "<br />"
                + "Number of unconnected nodes: " + numberOfUnconnected + "<br />"
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
}
