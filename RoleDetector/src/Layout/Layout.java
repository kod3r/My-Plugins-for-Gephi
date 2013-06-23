package Layout;

import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.labelAdjust.LabelAdjust;
import org.gephi.visualization.VizController;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class Layout {

    private GraphModel graphModel;
    private float zoomFactor;
    private boolean zoom;
    private String inOrOut;
    private float origCameraDistance;

    public Layout(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    public void executeForceAtlas2Layout() throws InterruptedException {
        origCameraDistance = VizController.getInstance().getVizModel().getCameraDistance();
        ForceAtlas2 forceAtlas2 = new ForceAtlas2(null);
        forceAtlas2.setGraphModel(graphModel);
        forceAtlas2.setAdjustSizes(true);
        forceAtlas2.setScalingRatio(30.0);
        forceAtlas2.setJitterTolerance(0.5);
        forceAtlas2.setBarnesHutOptimize(true);
        forceAtlas2.setThreadsCount(Runtime.getRuntime().availableProcessors() * 2);

        forceAtlas2.initAlgo();
        Long beginning = System.currentTimeMillis();
        for (int i = 0; i < 2000 && forceAtlas2.canAlgo(); i++) {
            forceAtlas2.goAlgo();
            if (zoom) {
                float j = i * zoomFactor;
                if (inOrOut.equals("in")) {
                    VizController.getInstance().getGraphIO().setCameraDistance(origCameraDistance - (float) j * 1.5f);
                } else {
                    VizController.getInstance().getGraphIO().setCameraDistance(origCameraDistance + (float) j * 1.5f);
                }
            }
            
            //stops the algo after n seconds
            int seconds = 7;
            if ((System.currentTimeMillis() - beginning) / 1000 > seconds) {
                break;
            }
        }

        forceAtlas2.endAlgo();

    }

    public void executeLabelAdjust() {
        LabelAdjust labelAdjust = new LabelAdjust(null);
        labelAdjust.setGraphModel(graphModel);
        labelAdjust.setAdjustBySize(true);
        labelAdjust.initAlgo();

        for (int i = 0; i < 100 && labelAdjust.canAlgo(); i++) {
            labelAdjust.goAlgo();
        }
        labelAdjust.endAlgo();

    }

    public void setZoom(Boolean zoom, String inOrOut, float zoomFactor) throws InterruptedException {
        this.zoom = zoom;
        this.inOrOut = inOrOut;
        this.zoomFactor = zoomFactor;
    }
}
