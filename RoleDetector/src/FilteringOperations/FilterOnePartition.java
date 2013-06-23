/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FilteringOperations;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.plugin.partition.PartitionBuilder;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.openide.util.Lookup;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class FilterOnePartition {

    private Graph graph;

    public FilterOnePartition(Graph graph) {
        this.graph = graph;
    }

    public void partitionInteger(String columnPartition, Integer valuePartition) {
        GraphModel graphModel = graph.getGraphModel();
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel attributeModel = ac.getModel();

        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        PartitionController pc = Lookup.getDefault().lookup(PartitionController.class);
        Partition p = pc.buildPartition(attributeModel.getNodeTable().getColumn(columnPartition), graph);
        Part part;
        Query query;
        GraphView view;
        PartitionBuilder.NodePartitionFilter partitionFilter;

        partitionFilter = new PartitionBuilder.NodePartitionFilter(p);

        //line where the value plays a role
        if (valuePartition.equals(-99)) {
            partitionFilter.selectAll();
        } else {
            part = p.getPartFromValue(valuePartition);
            if (part != null) {
                partitionFilter.addPart(part);
            }
            part = p.getPartFromValue(valuePartition+5000);
            if (part != null) {
                partitionFilter.addPart(part);
            }
        }

        query = filterController.createQuery(partitionFilter);
        view = filterController.filter(query);
        graphModel.setVisibleView(view); //Set the filter result as the visible view
    }
}
