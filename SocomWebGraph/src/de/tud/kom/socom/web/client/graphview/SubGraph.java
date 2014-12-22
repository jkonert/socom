package de.tud.kom.socom.web.client.graphview;

import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.NODE_WIDTH;

import java.util.HashMap;
import java.util.Map;

import org.sgx.raphael4gwt.raphael.Paper;
import org.sgx.raphael4gwt.raphael.Path;
import org.sgx.raphael4gwt.raphael.Raphael;
import org.sgx.raphael4gwt.raphael.Set;
import org.sgx.raphael4gwt.raphael.Shape;
import org.sgx.raphael4gwt.raphael.base.Glow;
import org.sgx.raphael4gwt.raphael.event.HoverListener;
import org.sgx.raphael4gwt.raphael.event.MouseEventListener;
import org.sgx.raphael4gwt.raphael.widget.PaperWidget;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import de.tud.kom.socom.web.client.GraphPanel;
import de.tud.kom.socom.web.client.Helper;

public class SubGraph extends AbstractGraph {
	
	private Set optionSet;

	public SubGraph(JSONObject graph, long center) {
		super(graph, false, true, center);
	}

	@Override
	Map<Long, Integer> getNodeLevel() {
		if (centralNode == -1)
			return null;
		Map<Long, Integer> levels = new HashMap<Long, Integer>();

		for (Long n : nodes) {
			JSONObject context = Helper.findContextData(graph.get("contexts").isArray(), n);
			JSONArray relationsTo = context.get("relationsTo").isArray();
			for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
				Long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
			
				if (next.equals(centralNode)) {
					levels.put(n, 0);
				}
			}
		}
		levels.put(centralNode, 1);
		
		JSONObject context = Helper.findContextData(graph.get("contexts").isArray(), centralNode);
		JSONArray relationsTo = context.get("relationsTo").isArray();
		for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
			long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
			levels.put(next, 2);
		}
		return levels;
	}

	@Override
	void addGraphSpecificDrawings() {
		//for each connection to a outside the subgraph draw 
		for (long n : levels.keySet()) {
			int to = 0, from = 0;
			int level = levels.get(n);
			boolean leftNode = level == 0;
			boolean rightNode = level == 2;
			if (leftNode || rightNode) {
				for (long n1 : nodes) {
					if (n1 == centralNode)
						continue;
					JSONObject context = Helper.findContextData(graph.get("contexts").isArray(), n1);
					JSONArray relationsTo = context.get("relationsTo").isArray();
					for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
						long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
						if (next == n && levels.get(n1) == null)
							from++;
					}
				}
				JSONObject context = Helper.findContextData(graph.get("contexts").isArray(), n);
				JSONArray relationsTo = context.get("relationsTo").isArray();
				for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
					long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
					if (next != centralNode && levels.get(next) == null)
						to++;
				}
				for (int i = 0; i < from; i++) {
					int s = leftNode ? -1 : 1;
					int l = (int)3*NODE_WIDTH;
					long nx = drawables.get(n).getX();
					long ny = drawables.get(n).getY();
					int x1 = (int) nx + l * s;
					int y1 = (int) ny - l/4 + i * l/2/from;
					Path p1 = paper.path("M"+x1+","+y1 + " L"+nx+","+ny);
					Path p2 = paper.path(p1.getSubpath(0,(int) (p1.getTotalLength()/3)));
					p2.attr("arrow-end", "open-narrow-long");
					Raphael.set(paper, p1,p2).attr("stroke-width",2).attr("stroke-dasharray", "--").attr("stroke", "#aaa").toBack();

				}
				for (int i = 0; i < to; i++) {
					int s = leftNode ? -1 : 1;
					int l = (int)3*NODE_WIDTH;
					long nx = drawables.get(n).getX();
					long ny = drawables.get(n).getY();
					int x1 = (int) nx + l * s;
					int y1 = (int) ny + l/4 + i * l/2/to;
					Path p1 = paper.path("M"+nx+","+ny + " L"+x1+","+y1);
					Path p2 = paper.path(p1.getSubpath(0, (int) (p1.getTotalLength()*2/3)));
					p2.attr("arrow-end", "open-narrow-long");
					Raphael.set(paper, p1,p2).attr("stroke-width",2).attr("stroke-dasharray", "--").attr("stroke", "#aaa").toBack();
				}
			}
		}
		addOverviewButton();
	}

	private void addOverviewButton() {
		int w = 110;
		int h = 30;
//		Paper optionPaper = Raphael.paper(PAPER_OFFSET_X, PAPER_OFFSET_Y, w, h);
		PaperWidget pW = new PaperWidget(w, h);
		Paper optionPaper = pW.getPaper();
		GraphPanel.get(-1, -1).addPanel(pW);
		
		Shape r1 = optionPaper.rect(0,0,w,h,0).attr("stroke-color", "#333").attr("fill","#fff");
		//from http://raphaeljs.com/icons
		Shape p1 = optionPaper.path("M1.999,2.332v26.499H28.5V2.332H1.999zM26.499,26.832H4V12.5h8.167V4.332h14.332V26.832zM15.631,17.649l5.468,5.469l-1.208,1.206l5.482,1.469l-1.47-5.481l-1.195,1.195l-5.467-5.466l1.209-1.208l-5.482-1.469l1.468,5.48L15.631,17.649z");
		p1.transform("s0.6t-2,-2");
		Shape t1 = optionPaper.text(0, h/2, "Show Overview");
		t1.transform("t" + (h+t1.getBBox().getWidth()/2) + ",0");
		
		final Set glowSet = Raphael.set(optionPaper, r1, p1);
		final Set hoverSet = Raphael.set(paper, paper.rect(0, 0, w, h, 0).attr("fill","#f00").attr("opacity", 0));
		
		final HoverListener hoverL = new HoverListener() {
			private Set g;

			@Override
			public void hoverOut(NativeEvent e) {
				if (g != null) {
					g.remove();
					g = null;
				}
			}

			@Override
			public void hoverIn(NativeEvent e) {
				g = glowSet.glow(new Glow(5, false, 0.7, 0, 0, "#999"));
			}
		};
		hoverSet.hover(hoverL);
		
		hoverSet.click(new MouseEventListener() {
			public void notifyMouseEvent(NativeEvent e) {
				hoverSet.unhover(hoverL);
				hoverL.hoverOut(null);
				unpaintOptionPaper();
				GraphPanel.get(-1,-1).showFullGraph();
			}
		});
	}

	public void unpaintOptionPaper() {
		if(optionSet != null) optionSet.remove();		
	}
}