package de.tud.kom.socom.web.client.drawables;

import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.ANIMATION_BUSY;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.CONNECTION_TOOLTIP_COLOR_1;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.CONNECTION_TOOLTIP_COLOR_2;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.CONNECTION_TOOLTIP_HEIGHT;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.CONNECTION_TOOLTIP_WIDTH;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.DYNAMIC_NODE_HEIGHT;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.DYNAMIC_NODE_WIDTH;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.EDGE_AVG_COLOR;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.EDGE_CURVE_STRENGTH;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.MAX_EDGE_THICKNESS;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.MIN_EDGE_THICKNESS;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.NODE_HEIGHT;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.NODE_STROKE_COLOR;
import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.NODE_WIDTH;

import java.util.HashSet;
import java.util.Map;

import org.sgx.raphael4gwt.raphael.Paper;
import org.sgx.raphael4gwt.raphael.Path;
import org.sgx.raphael4gwt.raphael.Raphael;
import org.sgx.raphael4gwt.raphael.Rect;
import org.sgx.raphael4gwt.raphael.Set;
import org.sgx.raphael4gwt.raphael.Shape;
import org.sgx.raphael4gwt.raphael.Text;
import org.sgx.raphael4gwt.raphael.base.Attrs;
import org.sgx.raphael4gwt.raphael.base.Glow;
import org.sgx.raphael4gwt.raphael.base.Point;
import org.sgx.raphael4gwt.raphael.event.Callback;
import org.sgx.raphael4gwt.raphael.event.HoverListener;
import org.sgx.raphael4gwt.raphael.event.MouseEventListener;

import com.google.gwt.dom.client.NativeEvent;

public class DrawableConnection {

	private long to;
	private long from;
	private long timesused;
	private double weight;

	private Paper paper;
	private Path path;

	private static java.util.Set<ConnectionTooltip> openToolboxes = new HashSet<ConnectionTooltip>();
	private boolean allowMultipleToolboxes = false;
	protected ConnectionTooltip toolbox;
	
	public DrawableConnection(long from, long connectionTo, long timesused, double timesusedpercentual) {
		this.from = from;
		this.to = connectionTo;
		this.timesused = timesused;
		this.weight = timesusedpercentual;
	}

	public boolean paint(Paper paper, Map<Long, DrawableNode> drawables, Map<Long, Integer> levels) {
		this.paper = paper;
		DrawableNode drawableNode = drawables.get(to);
		if (drawableNode == null)
			return false;

		Path prepath = getPath(paper, from, to, drawables, levels);
		long width = MIN_EDGE_THICKNESS + Math.round((MAX_EDGE_THICKNESS - 1) * weight);
		String color = EDGE_AVG_COLOR;

		double iPL = intersectionPoint(prepath, drawableNode.getNodeShape());
//		Point p1 = path.getPointAtLength(iPL - 1.7 * width);
//		Point p2 = path.getPointAtLength(iPL + 1.3 * width);
//		String moveToArrow = "M" + Math.round(p1.getX()) + "," + Math.round(p1.getY());
//		String lineTo = "L" + Math.round(p2.getX()) + "," + Math.round(p2.getY());
//
//		path.attr("stroke", color).attr("stroke-width", width);
//		arrowPath = paper.path(moveToArrow + lineTo);
//		arrowPath.attr("stroke", color).attr("stroke-width", Math.max(3, width)).attr("arrow-end", "open-narrow-short")
//				.toBack();
		prepath.hide();
		path = paper.path(prepath.getSubpath(0, (int)iPL));
		path.attr("stroke", color).attr("stroke-width", width).attr("arrow-end", "open-midium-midium").toBack();
		
		initListeners();
		return true;
	}

	private void initListeners() {
		int l = Math.max(NODE_HEIGHT, NODE_WIDTH);
		Shape hoverPath = paper.path(path.getSubpath(l, (int)(path.getTotalLength())));
		hoverPath.attr("stroke-width", 40).attr("opacity", 0);
		hoverPath.hover(new LinkHoverListener(path));
		hoverPath.click(getPathClickListener());
	}

	private Path getPath(Paper paper, long from, long to, Map<Long, DrawableNode> drawables, Map<Long, Integer> levels) {
		long x1 = drawables.get(from).getX();
		long y1 = drawables.get(from).getY();

		long x2 = drawables.get(to).getX();
		long y2 = drawables.get(to).getY();

		int HORIZONTAL_CURVE = EDGE_CURVE_STRENGTH;
		int VERTICAL_CURVE = EDGE_CURVE_STRENGTH;

		if (y1 == y2 || x1 == x2) {
			// a straight line
			Integer level1 = levels.get(from);
			Integer level2 = levels.get(to);
			if (level1 < level2 - 1 || level1 > level2 + 1) {
				// horizontal line AND one or more elements in between
				int l1 = level1 < level2 ? level1 : level2;
				int l2 = l1 == level1 ? level2 : level1;

				for (long n : levels.keySet()) {
					if (levels.get(n) > l1 && levels.get(n) < l2) {
						if (drawables.get(n).getY() == y1)
						HORIZONTAL_CURVE = 4;
					}
				}
			} else if (level1 == level2) {
				// vertical line
				for (long n : levels.keySet()) {
					long x = drawables.get(n).getX();
					long y = drawables.get(n).getY();
					if (x == x1 && ((y > y1 && y < y2) || (y > y2 && y < y1))) {
						VERTICAL_CURVE = 4;
					}
				}
			}
		}

		String moveto = "M" + x1 + "," + y1;
		String curve = "R" + ((x2 + x1) / 2 + (Math.random() - 0.5) * (x2 + x1) / VERTICAL_CURVE) + ","
				+ ((y2 + y1) / 2 + (Math.random() - 0.5) * (y2 + y1) / HORIZONTAL_CURVE) + "," + x2 + "," + y2;
		final Path path = paper.path(moveto + curve);
		path.toBack();

		return path;
	}

	private double intersectionPoint(Path path, Shape ellipse) {
		int l = Math.min(DYNAMIC_NODE_HEIGHT, DYNAMIC_NODE_WIDTH);
		Point p = path.getPointAtLength(path.getTotalLength() - l);
		while (ellipse.isPointInside(p.getX(), p.getY()) && l < path.getTotalLength() / 2) {
			p = path.getPointAtLength(path.getTotalLength() - l++);
		}
		return path.getTotalLength() - l;
	}


	private class LinkHoverListener implements HoverListener {
		private Set set;
		private Path path;
		private Glow g = new Glow(6, false, 0.8, 0, 0, NODE_STROKE_COLOR);
		

		public LinkHoverListener(Path p) {
			this.path = p;
		}

		@Override
		public void hoverOut(NativeEvent e) {
			if (set != null) {
				set.remove();
				set = null;
			}
		}

		@Override
		public void hoverIn(NativeEvent e) {
			if (set == null && !ANIMATION_BUSY){
				set = path.glow(g);
			}
		}
	}


	private MouseEventListener getPathClickListener() {
		return new MouseEventListener() {
			@Override
			public void notifyMouseEvent(NativeEvent e) {
				// lazy creation & update hide other toolboxes if necessary
				if (toolbox == null)
					toolbox = new ConnectionTooltip(DrawableConnection.this);
				if (!allowMultipleToolboxes && !openToolboxes.isEmpty()) {
					for (ConnectionTooltip box : openToolboxes)
						box.hide();
					openToolboxes.clear();
				}
				openToolboxes.add(toolbox);
				// show actual toolbox
				toolbox.show();
			}
		};
	}
	
	public long getX() {
		Point midP = path.getPointAtLength(path.getTotalLength()/2);
		return Math.round(midP.getX());
	}

	public long getY() {
		Point midP = path.getPointAtLength(path.getTotalLength()/2);
		return Math.round(midP.getY());
	}

	private class ConnectionTooltip {
		private DrawableConnection underlyingConnection;
		private static final int FADETIME = 500;

		private Rect box;
		private Rect closeButton;
		private Text description;
		
		public ConnectionTooltip(DrawableConnection conn) {
			underlyingConnection = conn;
		}

		public void show() {
			openToolboxes.add(this);
			if (box == null) {
				createBox();
				initListeners();
			}
			box.animate(Raphael.animation(Attrs.create().transform("s1"), FADETIME, "backOut"));
			closeButton.animate(Raphael.animation(Attrs.create().transform("t0,0s1"), FADETIME, "bounce"));
			description.show();
			description.animate(Raphael.animation(Attrs.create().opacity(1), FADETIME, "linear"));
		}

		private void createBox() {
			//big box
			createOuterBox();
			//close button
			createCloseButton();
			// inner text
			createText();
		}

		private void createText() {
			description = paper.text((int)(box.getBBox(true).getX() + 10), (int)(box.getBBox(true).getY() + box.getBBox(true).getHeight()/2), getDescription());
			description.attr("font-size", 10).attr("text-anchor", "start");
			description.attr("opacity", 0).attr("fill", "#222").attr("font-family", "Titillium Web, Lucida Sans Unicode, Lucida Grande, sans-serif");
		}
		
		private String getDescription() {
			return "Used " + timesused + " times";
		}

		private void createCloseButton() {
			int closeButtonSize = 15;
			closeButton = paper.rect(box.getBBox(true).getX() + box.getBBox(true).getWidth() - closeButtonSize, box
					.getBBox(true).getY(), closeButtonSize, closeButtonSize, 2);
			closeButton.attr("fill", "90-#f00-#b22").attr("stroke-width", "1").transform("t-40,20s0");
		}

		private void createOuterBox() {
			box = paper.rect(underlyingConnection.getX() - CONNECTION_TOOLTIP_WIDTH/2, underlyingConnection.getY() - CONNECTION_TOOLTIP_HEIGHT/2,
					CONNECTION_TOOLTIP_WIDTH, CONNECTION_TOOLTIP_HEIGHT , 5);
			box.attr("fill", "135-" + CONNECTION_TOOLTIP_COLOR_1 + "-" + CONNECTION_TOOLTIP_COLOR_2).transform("s0");
		}

		public void hide() {
			description.animate(Raphael.animation(Attrs.create().opacity(0), FADETIME*1/2, ">", new Callback() {
				public void call(Shape src) { src.hide(); }
			}));
			box.animate(Raphael.animation(Attrs.create().transform("s0"), FADETIME * 2 / 3, "backIn"));
			description.animate(Raphael.animation(Attrs.create().opacity(0), FADETIME * 2/3, "linear"));
			closeButton.animate(Raphael.animation(Attrs.create().transform("t-40,20s0"), FADETIME * 2 / 3, "backIn"));
			
			openToolboxes.remove(this);
		}
		
		private void initListeners() {
			closeButton.hover(new HoverListener() {
				private Set g;

				@Override
				public void hoverOut(NativeEvent e) {
					if (g != null)
						g.remove();
					g = null;
				}

				@Override
				public void hoverIn(NativeEvent e) {
					g = closeButton.glow(new Glow(5, false, 0.7, 0, 0, "#f22"));
				}
			});
			closeButton.click(new MouseEventListener() {
				
				@Override
				public void notifyMouseEvent(NativeEvent e) {
					ConnectionTooltip.this.hide();
				}
			});
		}
	}
}