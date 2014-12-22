package de.tud.kom.socom.web.client.graphview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import de.tud.kom.socom.web.client.Helper;

public class FullGraph extends AbstractGraph {

	public FullGraph(JSONObject graph) {
		super(graph, true, true, -1);
	}
	
	private enum color {
		white, gray, black
	}

	public Map<Long,Integer> getNodeLevel() {
		if(startNode == -1) return null;
		Queue<Long> nodeQueue = new LinkedList<Long>();
		Map<Long, color> colors = new HashMap<Long, color>();
		Map<Long, Integer> distances = new HashMap<Long, Integer>();
		for (Long n : nodes) {
			colors.put(n, color.white);
			distances.put(n, Integer.MAX_VALUE);
		}

		colors.put(startNode, color.gray);
		distances.put(startNode, 0);

		nodeQueue.add(startNode);
		bfs(nodeQueue, colors, distances);
		
		if(endNodes != null) {
			int maxL = 0;
			boolean multipleMax = false;
			for(int l : distances.values()) {
				if(l == maxL) multipleMax = true;
				if(l > maxL) {maxL = l; multipleMax = false; }
			}
			
			for(Long n : endNodes) {
				int d = distances.get(n);
				if(d < maxL || (d == maxL && multipleMax))
					distances.put(n, maxL + 1);
			}
		}
		
		return distances;
	}

	private Map<Long,Integer> bfs(Queue<Long> nodeQueue, Map<Long, color> colors, Map<Long, Integer> distances) {
		Long current;
		JSONArray contexts = this.graph.get("contexts").isArray();
		while ((current = nodeQueue.poll()) != null) {
			JSONArray relationsTo = Helper.findContextData(contexts, current).get("relationsTo").isArray();
			for (int i = 0; relationsTo != null && i < relationsTo.size(); i++) {
				
				Long next = (long)relationsTo.get(i).isObject().get("destination").isNumber().doubleValue();
				if (colors.get(next) == color.white) {
					colors.put(next, color.gray);
					distances.put(next, distances.get(current) + 1);
					nodeQueue.add(next);
				}
			}
			colors.put(current, color.black);
		}
		return distances;
	}

	@Override
	void addGraphSpecificDrawings() {
		//nothing specific
	}
}
