package de.tud.kom.socom.web.client.graphview;

public abstract class GlobalGraphSettings {
	
	public static final int PAPER_WIDTH = 1000;
	public static final int PAPER_HEIGTH = 700;
	
	public static final int NODES_OFFSET_LEFT = 100;
	public static final int NODES_OFFSET_RIGHT= NODES_OFFSET_LEFT;
	public static final int NODES_OFFSET_TOP = 0;
	public static final int NODES_OFFSET_BOTTOM = NODES_OFFSET_TOP;
	
	//edges
	public static final int EDGE_CURVE_STRENGTH = 30; //lower is more curved 
	public static final int MAX_EDGE_THICKNESS = 5;
	public static final int MIN_EDGE_THICKNESS = 1;
	public static final String EDGE_AVG_COLOR = "#080a32";

	//nodes
	public static final String NODE_COLOR = "#787cbf";
	public static final String NODE_STROKE_COLOR = "#434798";
	public static final String NODE_TEXT_COLOR = "#efefff";
	public static final int NODE_STROKE_WIDTH = 2;
	public static final int NODE_HEIGHT = 20;
	public static final int NODE_WIDTH = 40;
	public static final int NODE_TEXT_SIZE = 12;
	
	//node tooltip
	public static final int NODE_TOOLTIP_WIDTH = 190;
	public static final int NODE_TOOLTIP_HEIGHT = 100;
	
	//connection tooltip
	public static final int CONNECTION_TOOLTIP_WIDTH = 100;
	public static final int CONNECTION_TOOLTIP_HEIGHT = 40;
	public static final String CONNECTION_TOOLTIP_STROKE_COLOR = "#080a32";
	public static final String CONNECTION_TOOLTIP_COLOR_1 = "#fff";
	public static final String CONNECTION_TOOLTIP_COLOR_2 = "#ddd";
	
	//metanodes
	public static final String METANODE_COLOR = "#787cbf";
	public static final String METANODE_STROKE_COLOR = "#434798";
	public static final String METANODE_COLOR_TEXT_COLOR = "#efefff";
	public static final int METANODE_HEIGHT = 20;
	public static final int METANODE_WIDTH = 30;
	
	//dynamically changing settigns
	public static int DYNAMIC_NODE_HEIGHT = NODE_HEIGHT;
	public static int DYNAMIC_NODE_WIDTH = NODE_WIDTH;
	public static int DYNAMIC_NODE_TEXT_SIZE = NODE_TEXT_SIZE;
	public static boolean ANIMATION_BUSY = false; // if animations should be supressed (e.g. because zoom rectrangle is used -> no glow) 
	public static double CURRENT_ZOOM = 1;
	
	//the offset on the website must be set from other gwt project
	public static int PAPER_OFFSET_X = 0;
	public static int PAPER_OFFSET_Y = 0;
	public static long CONNECTION_USED_MIN = Long.MAX_VALUE;
	public static long CONNECTION_USED_MAX = 0;
	public static long TIME_SPENT_AVG_MIN = 0;
	public static long TIME_SPENT_AVG_MAX = 0;
	
	//optionbox
	public static final int OPTION_BOX_WIDTH = 90;
	public static final int OPTION_BOX_HEIGHT = 30;
}
