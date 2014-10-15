package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.List;

public class SampleNeighborhood {

	public final OpSample target;

	public SampleNeighborhood(OpSample target) {
		this.target = target;
	}

	/**
	 * Generates a new set of neighbors for the target solution by varying each
	 * cut by some distance
	 * 
	 * @param dist
	 * @return
	 */
	public List<OpSample> genNeighbors(double dist) {

		List<Double> tCuts = new ArrayList<Double>();
		for (Double d : target) {
			tCuts.add(d);
		}

		List<OpSample> neighbors = new ArrayList<OpSample>();
		for (int i = 0; i < target.size(); i++) {

			List<Double> newCuts1 = new ArrayList<Double>();
			List<Double> newCuts2 = new ArrayList<Double>();
			for (int j = 0; j < tCuts.size(); j++) {
				if (i == j) {
					// shift this one
					newCuts1.add(Math.max(tCuts.get(j) - dist, 0.0));
					newCuts2.add(Math.min(tCuts.get(j) + dist, 1.0));
				} else {
					newCuts1.add(tCuts.get(j));
					newCuts2.add(tCuts.get(j));
				}
			}
			neighbors.add(new OpSample(newCuts1));
			neighbors.add(new OpSample(newCuts2));
		}
		return neighbors;
	}

}
