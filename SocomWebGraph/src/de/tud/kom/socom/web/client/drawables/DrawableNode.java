package de.tud.kom.socom.web.client.drawables;

import static de.tud.kom.socom.web.client.graphview.GlobalGraphSettings.*;

import java.util.HashSet;

import org.sgx.raphael4gwt.raphael.Paper;
import org.sgx.raphael4gwt.raphael.Path;
import org.sgx.raphael4gwt.raphael.Raphael;
import org.sgx.raphael4gwt.raphael.Rect;
import org.sgx.raphael4gwt.raphael.Set;
import org.sgx.raphael4gwt.raphael.Shape;
import org.sgx.raphael4gwt.raphael.Text;
import org.sgx.raphael4gwt.raphael.base.Attrs;
import org.sgx.raphael4gwt.raphael.base.Glow;
import org.sgx.raphael4gwt.raphael.event.Callback;
import org.sgx.raphael4gwt.raphael.event.HoverListener;
import org.sgx.raphael4gwt.raphael.event.MouseEventListener;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;

import de.tud.kom.socom.web.client.GraphPanel;

public class DrawableNode {

	private Paper paper;
	private JSONObject node;
	private Shape nodeShape;
	private Text text;
	private NodeTooltip toolbox;
	private static java.util.Set<NodeTooltip> openToolboxes = new HashSet<NodeTooltip>();
	private boolean allowMultipleToolboxes = false;

	public DrawableNode(JSONObject node) {
		this.node = node;
	}

	public void paint(Paper paper, int x, int y, int yTextOffset) {
		this.paper = paper;
//		nodeShape = paper.ellipse(x, y, DYNAMIC_NODE_WIDTH, DYNAMIC_NODE_HEIGHT);
		nodeShape = paper.rect(x-DYNAMIC_NODE_WIDTH, y-DYNAMIC_NODE_HEIGHT, DYNAMIC_NODE_WIDTH*2, DYNAMIC_NODE_HEIGHT*2, DYNAMIC_NODE_HEIGHT);
		String nodeName = node.get("name").isString().stringValue();
		nodeName = nodeName.length() < 12 ? nodeName : nodeName.substring(0, 10) + "..";
		text = paper.text(x, y + yTextOffset, nodeName);

		applyTheme();
		initListeners();
	}

	private void initListeners() {
		MouseEventListener mel = getNodeClickListener();
		nodeShape.click(mel);
		text.click(mel);
		HoverListener hl = getNodeHoverListener();
		nodeShape.hover(hl);
		text.hover(hl);
	}

	private MouseEventListener getNodeClickListener() {
		return new MouseEventListener() {
			@Override
			public void notifyMouseEvent(NativeEvent e) {
				// lazy creation & update hide other toolboxes if necessary
				if (toolbox == null)
					toolbox = new NodeTooltip(DrawableNode.this);
				if (!allowMultipleToolboxes && !openToolboxes.isEmpty()) {
					for (NodeTooltip box : openToolboxes)
						box.hide();
					openToolboxes.clear();
				}
				openToolboxes.add(toolbox);
				// show actual toolbox
				toolbox.show();
			}
		};
	}

	private HoverListener getNodeHoverListener() {
		HoverListener hoverL = new HoverListener() {
			Set r;
			Set g;
			boolean stillinside;

			@Override
			public void hoverOut(NativeEvent e) {
				if (g != null){
					g.remove();
				}
				if(r != null) {
					new Timer() {
						public void run() {
							if(!stillinside)
								r.hide();
						}
					}.schedule(100);
				}
				g = null;
				stillinside = false;
			}

			@Override
			public void hoverIn(NativeEvent e) {
				stillinside = true;
				g = nodeShape.glow(new Glow(10, false, 1, 0, 0, NODE_COLOR));
				if(r==null) {
					final Rect rect = paper.rect(DrawableNode.this.getLeftX(), DrawableNode.this.getTopY(), 15, 15, 2);
					rect.attr("fill", NODE_COLOR).attr("stroke", NODE_STROKE_COLOR);
					Path symbol = paper.path("M29.772,26.433l-7.126-7.126c0.96-1.583,1.523-3.435,1.524-5.421C24.169,8.093,19.478,3.401,13.688,3.399C7.897,3.401,3.204,8.093,3.204,13.885c0,5.789,4.693,10.481,10.484,10.481c1.987,0,3.839-0.563,5.422-1.523l7.128,7.127L29.772,26.433zM7.203,13.885c0.006-3.582,2.903-6.478,6.484-6.486c3.579,0.008,6.478,2.904,6.484,6.486c-0.007,3.58-2.905,6.476-6.484,6.484C10.106,20.361,7.209,17.465,7.203,13.885z");
					symbol.transform("s0.4T"+(rect.getBBox().getX()-rect.getBBox().getWidth()/2 -1)+","+(rect.getBBox().getY()-rect.getBBox().getHeight()/2 -1));
					symbol.attr("fill", NODE_TEXT_COLOR).attr("stroke", NODE_TEXT_COLOR).attr("stroke-width",1);
					r = Raphael.set(paper, rect,symbol);
					
					r.click(new MouseEventListener() {
						public void notifyMouseEvent(NativeEvent e) {
							r.hide();
							GraphPanel.get(-1,-1).showSubGraph((long)node.get("id").isNumber().doubleValue());
						}
					});
					r.hover(new HoverListener() {
						Set subgraphboxglow;
						public void hoverOut(NativeEvent e) {
							stillinside = false;
							new Timer() {
								public void run() {
									if(!stillinside)
										r.hide();
								}
							}.schedule(100);
							if (subgraphboxglow != null){
								subgraphboxglow.remove();
							}
						}
						
						@Override
						public void hoverIn(NativeEvent e) {
							stillinside = true;
							subgraphboxglow = rect.glow(new Glow(5, false, 1, 0, 0, NODE_COLOR));
							r.add(subgraphboxglow);
						}
					});
				}
				else r.show();
			}
		};
		return hoverL;
	}

	private void applyTheme() {
		double timeSpentAvg = node.get("timeSpentAvg").isNumber().doubleValue();
		double normalized = (timeSpentAvg - TIME_SPENT_AVG_MIN) / (TIME_SPENT_AVG_MAX - TIME_SPENT_AVG_MIN);
		//normalized := 0...1
		double width = 1+5*normalized;
		nodeShape.attr("fill", NODE_COLOR).attr("stroke", NODE_STROKE_COLOR).attr("stroke-width", width);
		text.attr("fill", NODE_TEXT_COLOR).attr("font-size", DYNAMIC_NODE_TEXT_SIZE);
	}

	public long getLeftX() {
		return Math.round(nodeShape.getBBox(true).getX());
	}

	public long getRightX() {
		return Math.round(nodeShape.getBBox(true).getX() + nodeShape.getBBox(true).getWidth());
	}

	public long getTopY() {
		return Math.round(nodeShape.getBBox(true).getY());
	}

	public long getBottomY() {
		return Math.round(nodeShape.getBBox(true).getY() + nodeShape.getBBox(true).getHeight());
	}

	public long getX() {
		return Math.round(nodeShape.getBBox(true).getX() + nodeShape.getBBox(true).getWidth() / 2);
	}

	public long getY() {
		return Math.round(nodeShape.getBBox(true).getY() + nodeShape.getBBox(true).getHeight() / 2);
	}

	public Shape getNodeShape() {
		return nodeShape;
	}

	public void bringToFront() {
		nodeShape.toFront();
		text.toFront();
	}

	public void allowMultipleToolboxes(boolean allow) {
		this.allowMultipleToolboxes = allow;
	}

	private class NodeTooltip {
		private DrawableNode underlyingNode;
		private static final int FADETIME = 500;

		private Rect box;
		private Rect closeButton;
		
		private Set textSet;
		private Text header;
		private Path headerUnderline;
		private Text description;

		public NodeTooltip(DrawableNode node) {
			underlyingNode = node;
		}

		public void show() {
			openToolboxes.add(this);
			if (box == null) {
				createBox();
				initListeners();
			}
			double factor = 1/CURRENT_ZOOM;
			
			box.animate(Raphael.animation(Attrs.create().transform("s1"), FADETIME, "backOut"));
			closeButton.animate(Raphael.animation(Attrs.create().transform("t0,0s1"), FADETIME, "bounce"));
			textSet.show().animate(Raphael.animation(Attrs.create().opacity(1), FADETIME, "<"));
		}

		public void hide() {
			textSet.animate(Raphael.animation(Attrs.create().opacity(0), FADETIME*1/2, ">", new Callback() {
				public void call(Shape src) { textSet.hide(); }
			}));
			box.animate(Raphael.animation(Attrs.create().transform("s0"), FADETIME * 2 / 3, "backIn"));
			closeButton.animate(Raphael.animation(Attrs.create().transform("t-80,80s0"), FADETIME * 2 / 3, "backIn"));
			
			openToolboxes.remove(this);
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
			header = paper.text((int)(box.getBBox(true).getX()), (int)(box.getBBox(true).getY()),
					getHeader());
			header.attr("font-size", 14).transform("t"+ (header.getBBox().getWidth()/2) + "," + (header.getBBox().getHeight()));
			headerUnderline = paper.path("M" + (box.getBBox(true).getX() + 7) + "," + (header.getBBox().getY() + header.getBBox(true).getHeight() + 2) + 
					"L" + (box.getBBox(true).getX() + box.getBBox(true).getWidth()*6/7) + "," + (header.getBBox().getY() + header.getBBox(true).getHeight() + 2));
			headerUnderline.attr("stroke", "#ddd").attr("stroke-width", 2);
			description = paper.text((int) header.getBBox().getX()+5, 0, getDescription(node));
			description.attr("font-size", 11).attr("text-anchor", "start").
				transform("t0," +(int)(header.getBBox().getY() + header.getBBox().getHeight() + description.getBBox().getHeight()/2 + 5));
			
			textSet = Raphael.set(paper, header, headerUnderline, description);
			textSet.attr("opacity", 0).attr("fill", "#ddd").attr("font-family", "Titillium Web, Lucida Sans Unicode, Lucida Grande, sans-serif");
		}

		private String getHeader() {
			String name = node.get("name").isString().stringValue();
			name = name.length() < 18 ? name : name.substring(0,16) + ".."; 
			String id = node.get("externalid").isString().stringValue();
			return name + " (ID: " + id + ")";
		}

		private String getDescription(JSONObject node) {
			return "Visited by users: " + (int)node.get("usersSeen").isNumber().doubleValue() + "\nTime spent (total): " + 
					(int)(node.get("timeSpentTotal").isNumber().doubleValue()/60) + " min\nTime spent (average): " + 
					(int)(node.get("timeSpentAvg").isNumber().doubleValue()/60) + "  min/User\nContents: " +
					(int)node.get("contentCount").isNumber().doubleValue() + "\nInfluences: " +
					(int)node.get("influenceCount").isNumber().doubleValue();
		}

		private void createCloseButton() {
			int closeButtonSize = 17;
			closeButton = paper.rect(box.getBBox(true).getX() + box.getBBox(true).getWidth() - closeButtonSize, box
					.getBBox(true).getY(), closeButtonSize, closeButtonSize, 2);
			closeButton.attr("fill", "90-#f00-#b22").attr("stroke-width", "1").transform("t-80,80s0");
		}

		private void createOuterBox() {
			box = paper.rect(underlyingNode.getX() - NODE_TOOLTIP_WIDTH/2, underlyingNode.getY() - NODE_TOOLTIP_HEIGHT/2,
					NODE_TOOLTIP_WIDTH, NODE_TOOLTIP_HEIGHT , 5);
			box.attr("fill", "135-" + NODE_COLOR + "-" + NODE_STROKE_COLOR).transform("s0");
		}

		private void initListeners() {
			closeButton.hover(new HoverListener() {
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
					if(ANIMATION_BUSY) return;
					g = closeButton.glow(new Glow(5, false, 0.7, 0, 0, "#f22"));
				}
			});
			closeButton.click(new MouseEventListener() {
				
				@Override
				public void notifyMouseEvent(NativeEvent e) {
					NodeTooltip.this.hide();
				}
			});
		}
	}
}