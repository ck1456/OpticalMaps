package hps.nyu.fa14.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hps.nyu.fa14.BinCounter;
import hps.nyu.fa14.BinRefiner;
import hps.nyu.fa14.ISolutionFinder;
import hps.nyu.fa14.ISolutionViewer;
import hps.nyu.fa14.OpSample;
import hps.nyu.fa14.OpSolution;
import hps.nyu.fa14.SampleSet;

public class OpticalMapSolver implements ISolutionFinder {

  private final int BIN_COUNT = 500;
  private final int COLLAPSE_BIN_COUNT = 11;
  private final int TARGET_BIN_COUNT = 40;

  private final ISolutionViewer viewer;
  public OpticalMapSolver(ISolutionViewer viewer){
    this.viewer = viewer;
  }
  
  @Override
  public OpSolution generateSolution(SampleSet set) {
    OpSolution solution0 = OpSolution.trivial(set);
    viewer.update(solution0);
    
    // Bin the whole data set
    BinCounter counter = new BinCounter(OpSolution.trivial(set));

    // Choose the top some percent of the bins and create a small target
    int[] topBins = BinCounter.getPercentTopBins(counter.count(BIN_COUNT),
        .005, COLLAPSE_BIN_COUNT);
    OpSample newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
    System.out.println("size of bins "+newBinned.size());

    // Refine the solution based on finding the samples that are most
    // similar to the small target
    BinRefiner refiner = new BinRefiner(newBinned);
    solution0 = refiner.genSolution(set); // mostly eliminates
                               // noise, we hope

    // Iterate by counting the bins for only the samples that remain
    // included
    OpSolution solution = solution0;
    viewer.update(solution);
    OpSolution nextSolution = solution;

    // TODO: Figure out how to cluster for the right percentage of bins
    int iterations = 10;
    for (int j = 0; j < iterations; j++) {
      counter = new BinCounter(solution);
      int targetCount = (int) (j + 1) * TARGET_BIN_COUNT / iterations;
      topBins = BinCounter.getTopBins(counter.count(BIN_COUNT),
          targetCount, COLLAPSE_BIN_COUNT);
      newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
      
      //OpSolution solution = new OpSolution(set);
      //solution.ideal = target;
    
      // get the min diff for each sample with the target
      // and record if flipped
      List<RankedOpSample> rankedSamples = new ArrayList<RankedOpSample>(set.size());
      for(int i = 0; i < set.size(); i++){
        OpSample s = set.get(i);
        s.flip(false);
        double diff = s.cosine(newBinned);
        s.flip(true);
        double flipDiff = s.cosine(newBinned);
        RankedOpSample ranked = new RankedOpSample();
        ranked.sample = s;
        ranked.sampleIndex = i;
        if(diff <= flipDiff){
          ranked.diff = diff;
        } else {
          ranked.diff = flipDiff;
          ranked.flipped = true;
        }
        rankedSamples.add(ranked);
      }
      
      // sort them by diff (descending)
      Collections.sort(rankedSamples, RankedOpSample.RANK_BY_DIFF);
      
      // choose the top x percent, then mark the others garbage
      // TODO: Consider doing Farthest First K means clustering in order to determine the split
      double keep = 0.7;
      for(int i = 0; i < set.size(); i++){
        solution.isTarget[i] = false;
      }
      for(int i = 0; i < keep * set.size(); i++){
        RankedOpSample s = rankedSamples.get(i);
        solution.isTarget[s.sampleIndex] = true;
        solution.isFlipped[s.sampleIndex] = s.flipped;
      }
      
      //refiner = new BinRefiner(newBinned);
      double digestionProbability = 0.7;
      if(set.problemType != 3) {
        digestionProbability = set.digestionProbability;
      }
      refiner.keepPortion = (j + 1) * digestionProbability / iterations;
      //OpSolution nextSolution = refiner.genSolution(set);

      viewer.update(nextSolution);
      // See how much the solution changed
      // Compare solution / nextSolution
      solution = nextSolution;
    }

    return solution;
  }
  
  private static class RankedOpSample{
    public double diff;
    public boolean flipped;
    public OpSample sample;
    public int sampleIndex;
    
    public static Comparator<RankedOpSample> RANK_BY_DIFF = new Comparator<RankedOpSample>() {

      @Override
      public int compare(RankedOpSample o1, RankedOpSample o2) {
        return (int)Math.signum(o1.diff - o2.diff);
      }
    };
  }

}
