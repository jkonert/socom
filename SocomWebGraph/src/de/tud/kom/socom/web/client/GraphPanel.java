package de.tud.kom.socom.web.client;

import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.CURRENT_ZOOM;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.DYNAMIC_NODE_HEIGHT;

import org.sgx.raphael4gwt.raphael.widget.PaperWidget;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import de.tud.kom.socom.web.client.graphview.FullGraph;
import de.tud.kom.socom.web.client.graphview.GlobalGraphSettings;
import de.tud.kom.socom.web.client.graphview.SubGraph;

public class GraphPanel extends AbsolutePanel {

	private static GraphPanel instance = new GraphPanel();
	
	private JSONObject graph;
	private FullGraph fullgraph;
	private SubGraph subgraph;

	private GraphPanel(){
	}

	public static GraphPanel get(int offX, int offY){
		if(offX > 0) GlobalGraphSettings.PAPER_OFFSET_X = offX;
		if(offY > 0) GlobalGraphSettings.PAPER_OFFSET_Y = offY;
		return instance;
	}
	
	public void setGraph(JSONObject graph) {
		this.unloadGraph();
		this.graph = graph;
	}
	
	public void showFullGraph(){
		this.clear();
		if(this.graph == null) {
			this.add(new Label("Graph or Startnode not set. Abort."));
			return;
		}
		
		if(fullgraph == null) {
			fullgraph = new FullGraph(this.graph);
			fullgraph.paint(true,DYNAMIC_NODE_HEIGHT/5);
		} else {
			fullgraph.drawOptionBox(CURRENT_ZOOM != 1);
		}
		this.add(fullgraph);
		fullgraph.fadeIn();
	}

	public void showSubGraph(long center) {	
		if(this.graph == null || center == -1) {
			this.add(new Label("Graph or central Node not set. Abort."),0,0);
			return;
		}
		if(subgraph != null) subgraph.unpaintOptionPaper();
		subgraph = new SubGraph(this.graph, center);
		subgraph.paint(false,0);
		this.add(subgraph);
		this.clear();
		//FIXME only every second try will show connection's arrow, why?
//		subgraph = new SubGraph(this.graph);
		subgraph.paint(true,0);
		this.add(subgraph);
		subgraph.fadeIn();
	}
	
	private void unloadGraph() {
		fullgraph = null;
		subgraph = null;
		graph = null;
	}
	
	public void addPanel(PaperWidget pW) {
		this.add(pW,0,0);
	}
}
