package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CosineScorer implements ISolutionScorer {

  /**
   * Calculates a cumulative difference between the ideal molecule and all of
   * the target samples. Smaller is better.
   */
  @Override
  public double score(OpSolution solution) {
    // Come up with a metric for which solutions have the best alignment
    OpSample ideal = solution.ideal;

    // Track a list of all of the diffs between ideal and each target sample
    // in the correct orientation
    List<Double> targetCosines = new ArrayList<Double>();
//    List<Double> noisePartialDiffs = new ArrayList<Double>();
    for (int i = 0; i < solution.set.size(); i++) {
      OpSample sample = solution.set.get(i);
      if (solution.isTarget[i]) {
        sample.flip(solution.isFlipped[i]);
        targetCosines.add(sample.cosine(ideal));
      }
    }
    //Collections.sort(targetCosines);
    //Collections.sort(noisePartialDiffs);
    double score = 1.0;
    for(int i=0;i<targetCosines.size();i++) {
      score *= targetCosines.get(i);
    }
    return score;
  }

}
