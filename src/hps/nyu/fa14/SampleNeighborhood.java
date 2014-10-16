package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleNeighborhood {

	public final OpSample target;

	public SampleNeighborhood(OpSample target) {
		this.target = target;
	}

	private final Random RAND = new Random();
	
	/**
	 * Generates a new set of neighbors for the target solution by varying each
	 * cut by some distance
	 * 
	 * @param dist
	 * @return
	 */
	public List<OpSample> genNeighbors(double dist) {

		List<Double> tCuts = new ArrayList<Double>(target.size());
		for (Double d : target) {
			tCuts.add(d);
		}

		List<OpSample> neighbors = new ArrayList<OpSample>(2 * target.size());
		for (int i = 0; i < target.size(); i++) {

			List<Double> newCuts1 = new ArrayList<Double>(target.size());
			List<Double> newCuts2 = new ArrayList<Double>(target.size());
			for (int j = 0; j < tCuts.size(); j++) {
				if (i == j) {
					// shift this one
					newCuts1.add(Math.max(tCuts.get(j) - (dist * RAND.nextDouble()), 0.0));
					newCuts2.add(Math.min(tCuts.get(j) + (dist * RAND.nextDouble()), 1.0));
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
