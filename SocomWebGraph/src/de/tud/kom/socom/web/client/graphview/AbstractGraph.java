package de.tud.kom.socom.web.client.graphview;

import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.*;

import java.util.HashMap;
import java.util.Map;

import org.sgx.raphael4gwt.raphael.Paper;
import org.sgx.raphael4gwt.raphael.Raphael;
import org.sgx.raphael4gwt.raphael.Rect;
import org.sgx.raphael4gwt.raphael.Set;
import org.sgx.raphael4gwt.raphael.Shape;
import org.sgx.raphael4gwt.raphael.base.Attrs;
import org.sgx.raphael4gwt.raphael.base.Glow;
import org.sgx.raphael4gwt.raphael.base.Rectangle;
import org.sgx.raphael4gwt.raphael.event.Callback;
import org.sgx.raphael4gwt.raphael.event.DDListener;
import org.sgx.raphael4gwt.raphael.event.HoverListener;
import org.sgx.raphael4gwt.raphael.event.MouseEventListener;
import org.sgx.raphael4gwt.raphael.widget.PaperWidget;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;

import de.tud.kom.socom.web.client.GraphPanel;
import de.tud.kom.socom.web.client.Helper;
import de.tud.kom.socom.web.client.drawables.DrawableConnection;
import de.tud.kom.socom.web.client.drawables.DrawableNode;
public abstract class AbstractGraph extends PaperWidget {
	
	protected Paper paper;
	protected JSONObject graph;
	protected Map<Long, Integer> levels; //a level is a vertical space in the graph (a vertical line could describe a level)
	protected Map<Long, DrawableNode> drawables;
	private Map<Integer, Integer> nodesAtLevel;
	private int maxLevel;
	private boolean concistent;
	
	private int dx;
	private DDListener dragL;
	private Set optionSet;
	protected long[] nodes;
	protected long[] endNodes;
	protected long startNode, centralNode;
	private Rect fullpaper;

	public AbstractGraph(JSONObject graph, boolean zoomable, boolean dynamicNodesize, long center){
		super(PAPER_WIDTH, PAPER_HEIGTH);
		this.paper = this.getPaper();

		this.graph = graph;
		this.nodes = getNodes();

		this.startNode = (long)graph.get("startnode").isNumber().doubleValue();
		if(startNode == -1){
			paper.text(PAPER_WIDTH/2, PAPER_HEIGTH/5, "The Graph is empty. (Startnode: " + startNode + ")");
			return;
		}
		
		this.endNodes = Helper.asArray(graph.get("endnodes").isArray());
		this.centralNode = center;
		GlobalGraphSettings.TIME_SPENT_AVG_MIN = (long)graph.get("timeSpentAvgMin").isNumber().doubleValue();
		GlobalGraphSettings.TIME_SPENT_AVG_MAX = (long)graph.get("timeSpentAvgMax").isNumber().doubleValue();
		
		this.levels = getNodeLevel();
		if(levels == null) {
			concistent = false;
			showConsistencyError();
			return;
		}
		concistent = true;
		
		this.drawables = new HashMap<Long, DrawableNode>();
		this.nodesAtLevel = new HashMap<Integer, Integer>();
		
		init();
		if(zoomable) addZoomOption();
		if(dynamicNodesize) setDynamicNodeSize();
	}

	private long[] getNodes() {
		JSONArray contexts = graph.get("contexts").isArray();
		long[] nodes = new long[contexts.size()];
		for(int i = 0; i < contexts.size(); i++) {
			JSONObject contextobject = contexts.get(i).isObject();
			nodes[i] = (long)(contextobject.get("id").isNumber().doubleValue());
		}
		return nodes;
	}

	abstract Map<Long, Integer> getNodeLevel();

	private void init() {
		maxLevel = 0;
		for(Long n : levels.keySet()){
			Integer level = levels.get(n);
			nodesAtLevel.put(level, nodesAtLevel.containsKey(level)?nodesAtLevel.get(level)+1:1);
			if(maxLevel < level) maxLevel = level;
		}
		int w = PAPER_WIDTH - NODES_OFFSET_LEFT - NODES_OFFSET_RIGHT;
        dx = w / maxLevel;

//        paper.rect(0,0,PAPER_WIDTH,PAPER_HEIGTH); //debug: show canvas limits
	}
	
	private void addZoomOption() {
        final Rect fullpaper = getFullpaperRect();
        fullpaper.drag(dragL = new DDListener() {
        	private Rect selection;
        	private int startX, startY;
        	
			@Override
			public void onStart(int x, int y, NativeEvent e) {
				startX = x - PAPER_OFFSET_X;
				startY = y - PAPER_OFFSET_Y;
				
				selection = paper.rect(startX, startY, 4, 4, 0);
				selection.attr("stroke-color", NODE_STROKE_COLOR).attr("fill", NODE_COLOR).attr("opacity", 0.3);
				ANIMATION_BUSY = true;
			}
			
			@Override
			public void onMove(int dx, int dy, int x, int y, NativeEvent e) {
				int currentX = x - PAPER_OFFSET_X; // 10 corrention for cursor
				int currentY = y - PAPER_OFFSET_Y;
				int w = currentX - startX;
				int h = currentY - startY;
				
	            if(w < 0){
	            	selection.attr("x", currentX).attr("width", w * -1);
	            } else {
	            	selection.attr("x", startX).attr("width", w);
	            }
	            
	            if(h < 0) {
	            	selection.attr("y", currentY).attr("height", h * -1);
	            } else {
	            	selection.attr("y", startY).attr("height", h);
	            }
			}
			
			@Override
			public void onEnd(NativeEvent e) {
				Rectangle box = selection.getBBox();
				if(box.getHeight() * box.getWidth() > (paper.getHeight() * paper.getHeight() / 30)) { // only zoom bigger areas
					CURRENT_ZOOM = getDiag(box.getWidth(),box.getHeight())/getDiag(paper.getWidth(),paper.getHeight());
					zoomTo(fullpaper, box);//box.getX(), box.getY(), box.getWidth(), box.getHeight());
				}
				selection.remove();
				selection = null;
				ANIMATION_BUSY = false;
			}
		},25);
	}

	public Rect getFullpaperRect() {
		if(fullpaper == null) {
			fullpaper = paper.rect(0,0,PAPER_WIDTH,PAPER_HEIGTH);
			fullpaper.attr("fill", "#000").attr("opacity", 0);
		}
		return fullpaper;
	}

	protected double getDiag(double width, double height) {
		double xq = width*width;
		double yq = height*height;
		return Math.sqrt(xq+yq);
	}

	protected void zoomTo(Rect fullpaper, final Rectangle box) {
		fullpaper.undrag(); // disable further zooming
		
//		setViewBoxAnimated(box);
		fadeOut(new Callback() {
			public void call(Shape src) {
				double originalRatio = (double)PAPER_WIDTH / (double)PAPER_HEIGTH;
				double newRatio = box.getWidth() / box.getHeight();

				double zoom;
				if(newRatio > originalRatio){
					//width ausschlaggebend
					zoom = PAPER_WIDTH/box.getWidth();
				} else {
					//height ausschlaggebend
					zoom = PAPER_HEIGTH/box.getHeight();
				}
				CURRENT_ZOOM = zoom;
				
				paper.setViewBox(box, true);
				fadeIn();
				src.hide();
			}
		});
		drawOptionBox(true);
	}

	@SuppressWarnings("unused")
	@Deprecated	/*not smooth :(*/
	private void setViewBoxAnimated(Rectangle box) {
		final double x1,y1,w1,h1;
		x1=0;
		y1=0;
		w1=paper.getWidth();
		h1=paper.getHeight();
		final double x2,y2,w2,h2;
		x2=box.getX();
		y2=box.getY();
		w2=box.getWidth();
		h2=box.getHeight();
		
		double steps = 25;
		for(double step = 1; step <= steps; step++) {
			final double factor = step/steps;
			new Timer() {
				@Override
				public void run() {
					int x = (int) ((1-factor)*x1 + factor*x2);
					int y = (int) ((1-factor)*y1 + factor*y2);
					int w = (int) ((1-factor)*w1 + factor*w2);
					int h = (int) ((1-factor)*h1 + factor*h2);
					paper.setViewBox((int)x, (int)y, (int)w, (int)h, true);
				}
			}.schedule((int) (100*5*(step/steps)));
		}
	}

	public void drawOptionBox(boolean paint) {
		if(!paint) return;
		int w = GlobalGraphSettings.OPTION_BOX_WIDTH;
		int h = GlobalGraphSettings.OPTION_BOX_HEIGHT;
//		Paper optionPaper = Raphael.paper(PAPER_OFFSET_X,PAPER_OFFSET_Y,w,h);
		PaperWidget pW = new PaperWidget(w, h);
		Paper optionPaper = pW.getPaper();
		GraphPanel.get(-1, -1).addPanel(pW);
		
		Shape r1 = optionPaper.rect(0,0,w,h,0).attr("stroke-color", "#333").attr("fill","#fff");
		Shape r2 = optionPaper.rect(5,5,h-10,h-10,0);
		//from http://raphaeljs.com/icons
		Shape p1 = optionPaper.path("M29.772,26.433l-7.126-7.126c0.96-1.583,1.523-3.435,1.524-5.421C24.169,8.093,19.478,3.401,13.688,3.399C7.897,3.401,3.204,8.093,3.204,13.885c0,5.789,4.693,10.481,10.484,10.481c1.987,0,3.839-0.563,5.422-1.523l7.128,7.127L29.772,26.433zM7.203,13.885c0.006-3.582,2.903-6.478,6.484-6.486c3.579,0.008,6.478,2.904,6.484,6.486c-0.007,3.58-2.905,6.476-6.484,6.484C10.106,20.361,7.209,17.465,7.203,13.885z");
		p1.transform("s0.6t-2,-2");
		Shape t1 = optionPaper.text(0, h/2, "Reset Zoom");
		t1.transform("t" + (h+t1.getBBox().getWidth()/2) + ",0");
		
		final Rect hoverRect = paper.rect(0,0,w,h,0);
		hoverRect.attr("fill", "#f00").attr("opacity", 0);
		final Set glowSet = Raphael.set(optionPaper, r1, r2, p1);
		if(optionSet!=null)optionSet.remove();
		optionSet = Raphael.set(optionPaper, glowSet, t1, hoverRect);

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
		
		optionSet.click(new MouseEventListener() {
			public void notifyMouseEvent(NativeEvent e) {
				optionSet.unhover(hoverL);
				hoverL.hoverOut(null);
				optionSet.remove();
				fadeOut(new Callback() {
					public void call(Shape src) {
						resetViewbox(getFullpaperRect());
						src.hide();
						fadeIn();
					}
				});
			}

		});
		optionSet.hover(hoverL);
	}

	public void resetViewbox(Rect fullpaper) {
		CURRENT_ZOOM = 1;
		paper.setViewBox(fullpaper.getBBox(), true);
		fullpaper.drag(dragL, 25);
	}
	
	private void setDynamicNodeSize() {
		int nc = nodes.length;
		int mediumNC = 15;
		
		if(nc > mediumNC){
			int rx = NODE_WIDTH;
			int ry = NODE_HEIGHT;
			int fontsize = NODE_TEXT_SIZE;
			
			int factor = nc/mediumNC;
			DYNAMIC_NODE_WIDTH = rx / factor;
			DYNAMIC_NODE_HEIGHT = ry / factor;
			DYNAMIC_NODE_TEXT_SIZE = fontsize / factor;
		}
	}
	
	public void paint(boolean paint, int nodeTextYOffset){
		if(!paint)return;
		if(!concistent) return;
		int[] alreadyPutOnLevel = new int[maxLevel + 1];
		
		for(long n : nodes) {
        	Integer level = levels.get(n);
        	if(level == null) continue; //skip nodes w/o level (e.g. if subgraph)
        	int x = NODES_OFFSET_LEFT + dx * level;
        	int y = NODES_OFFSET_TOP + (alreadyPutOnLevel[level]+1)*(PAPER_HEIGTH - NODES_OFFSET_TOP - NODES_OFFSET_BOTTOM)/(nodesAtLevel.get(level)+1);
   			alreadyPutOnLevel[level]++;
        	
   			JSONObject contectdata = Helper.findContextData(graph.get("contexts").isArray(), n);
			DrawableNode dnode = new DrawableNode(contectdata);
   			
   			drawables.put(n, dnode);
   			dnode.paint(paper, x, y, nodeTextYOffset);
   			JSONArray relationsTo = contectdata.get("relationsTo").isArray();
			for(int i = 0; i < relationsTo.size(); i++){
				long timesUsed = (long) relationsTo.get(i).isObject().get("timesUsed").isNumber().doubleValue();
				if(timesUsed < CONNECTION_USED_MIN) CONNECTION_USED_MIN = timesUsed;
				if(timesUsed > CONNECTION_USED_MAX) CONNECTION_USED_MAX = timesUsed;
			}
        }
		
		for(long n : levels.keySet()) {
			JSONObject context = Helper.findContextData(graph.get("contexts").isArray(), n);
			JSONArray relationsTo = context.get("relationsTo").isArray();
			
			for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
				long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
				double timesused = relationsTo.get(i).isObject().get("timesUsed").isNumber().doubleValue();
				double weight =	(timesused - GlobalGraphSettings.CONNECTION_USED_MIN)/(GlobalGraphSettings.CONNECTION_USED_MAX - GlobalGraphSettings.CONNECTION_USED_MIN);

				if(levels.containsKey(next)) { //draw only connections to nodes w/ levels (e.g. subgraph)
					DrawableConnection dc = new DrawableConnection(n, next, (long)timesused, weight);
					concistent = concistent && dc.paint(paper, drawables, levels); // if this returns false there is some inconsistency
				}
			}
		}
		
		if(!concistent) {
			showConsistencyError();
		}
		
		addGraphSpecificDrawings();
	}

	abstract void addGraphSpecificDrawings();

	private void showConsistencyError() {
			paper.text(paper.getWidth() / 2, paper.getHeight() / 2, "WARNING: the graph is inconsistent").attr("fill", "#f00").attr("font-size", 30);
	}
	

	public void fadeIn() {
		Rect r1 = paper.rect(0,0,paper.getWidth(),paper.getHeight(),0);
		r1.attr("fill", "#fff").attr("stroke", "#fff");
		r1.animate(Raphael.animation(Attrs.create().opacity(0), 300, "<", new Callback() {
			public void call(Shape src) {
				src.hide();
			}
		}));
	}
	
	public void fadeOut(final Callback callback) {
			Rect r1 = paper.rect(0,0,paper.getWidth(),paper.getHeight(),0);
			r1.attr("fill", "#fff").attr("stroke", "#fff").attr("opacity", 0);
			r1.animate(Raphael.animation(Attrs.create().opacity(1), 300, "<", new Callback() {
				public void call(Shape src) {
					callback.call(src);
				}
			}));
	}
}
