package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoherenceScorer implements ISolutionScorer {

	/**
	 * Calculates a cumulative difference between the ideal molecule and all of
	 * the target samples. Smaller is better.
	 */
	@Override
	public double score(OpSolution solution) {
		// Come up with a metric for which solutions have the best alignment

		OpSample ideal = solution.ideal;
		double DIGESTION_RATE = 0.7;

		// Track a list of all of the diffs between ideal and each target sample
		// in the correct orientation
		List<Double> targetPartialDiffs = new ArrayList<Double>();
		List<Double> noisePartialDiffs = new ArrayList<Double>();
		for (int i = 0; i < solution.set.size(); i++) {
			OpSample sample = solution.set.get(i);
			if (solution.isTarget[i]) {
				sample.flip(solution.isFlipped[i]);
				targetPartialDiffs.add(sample
						.partialDiff(ideal, DIGESTION_RATE));
			} else {
				noisePartialDiffs
						.add(sample.partialDiff(ideal, DIGESTION_RATE));
			}
		}
		Collections.sort(targetPartialDiffs);
		Collections.sort(noisePartialDiffs);
		double largestTargetDiff = targetPartialDiffs.get(targetPartialDiffs
				.size() - 1);

		// For each noise sample that is closer (smaller diff than a target
		// sample, add that value to the total score
		double noiseDiff = 0.0;
		for (int i = 0; i < noisePartialDiffs.size()
				&& noisePartialDiffs.get(i) < largestTargetDiff; i++) {
			noiseDiff += noisePartialDiffs.get(i);
		}
		if (noiseDiff > 0.0) {
			System.out.println("NoiseDiff " + noiseDiff);
		}
		// TODO: Should we normalize the noise diff too?

		double targetDiff = 0.0;
		for(Double d : targetPartialDiffs){
			targetDiff += d;
		}
		// Normalize the diffs
		targetDiff = targetDiff / targetPartialDiffs.size();
		return targetDiff + noiseDiff;
	}

}
