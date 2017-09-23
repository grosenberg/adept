package net.certiv.adept.model.topo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.certiv.adept.model.Edge;
import net.certiv.adept.model.EdgeSet;
import net.certiv.adept.model.Feature;
import net.certiv.adept.model.load.parser.ISourceParser;
import net.certiv.adept.util.Strings;

public class Stats {

	private Feature feature;

	public double distance;
	public double selfSimF;
	public double selfSimM;
	public double mutualSim;
	public double featLabelSim;
	public double edgeSetSim;
	public double intersectSim;

	public int typeCount; 		// # of unique feature types connected to this feature
	public int edgeCount; 		// total # of edgeSet connected to this feature
	public int edgeSetIntersect;
	public int edgeSetDisjoint;
	public int intersectCount;

	public String intersectTypes;
	public String disjointTypes;

	private Set<Long> intersectKeys;
	private Set<Long> disjointKeys;

	public Stats(Feature feature) {
		this.feature = feature;
		edgeStats();
	}

	public Stats(Feature feature, Feature matched) {
		this(feature);
		this.distance = feature.distance(matched);
		this.selfSimF = feature.getSelfSim();
		this.selfSimM = matched.getSelfSim();
		this.mutualSim = feature.similarity(matched);
		this.featLabelSim = feature.featLabelSimilarity(matched);
		this.edgeSetSim = feature.getEdgeSet().similarity(matched.getEdgeSet());
		this.intersectSim = feature.getEdgeSet().intersect(matched.getEdgeSet());
		this.intersectCount = feature.getEdgeSet().intersectCount(matched.getEdgeSet());

		intersectKeys = feature.getEdgeSet().intersectKeys(matched.getEdgeSet());
		disjointKeys = feature.getEdgeSet().disjointKeys(matched.getEdgeSet());
		this.intersectTypes = intersectKeys.toString();
		this.disjointTypes = disjointKeys.toString();
	}

	public void setLanguage(ISourceParser lang) {
		List<String> v = new ArrayList<>();
		for (Long key : intersectKeys) {
			if (key.longValue() == 0) {
				v.add("Adept");
			} else {
				int rule = (int) (key >>> 32);
				int type = key.intValue();
				if (rule > 0) {
					v.add(lang.getRuleNames().get(rule));
				} else {
					v.add(lang.getTokenNames().get(type));
				}
			}
		}
		this.intersectTypes = Strings.join(v, ", ");

		v.clear();
		for (Long key : disjointKeys) {
			int rule = (int) (key >>> 32);
			int type = key.intValue();
			if (rule > 0) {
				v.add(lang.getRuleNames().get(rule));
			} else {
				v.add(lang.getTokenNames().get(type));
			}
		}
		this.disjointTypes = Strings.join(v, ", ");
	}

	private void edgeStats() {
		EdgeSet edgeSet = feature.getEdgeSet();
		typeCount = edgeSet.getTypeCount();
		edgeCount = edgeSet.getEdgeCount();

		List<Integer> metrics = new ArrayList<>();
		for (Edge edge : edgeSet.getEdges()) {
			metrics.add(edge.metric);
		}
	}
}