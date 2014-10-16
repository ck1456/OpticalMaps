package hps.nyu.fa14.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hps.nyu.fa14.BinCounter;
import hps.nyu.fa14.BinRefiner;
import hps.nyu.fa14.CosineScorer;
import hps.nyu.fa14.ISolutionFinder;
import hps.nyu.fa14.ISolutionViewer;
import hps.nyu.fa14.OpSample;
import hps.nyu.fa14.OpSolution;
import hps.nyu.fa14.SampleNeighborhood;
import hps.nyu.fa14.SampleSet;

public class OpticalMapSolver implements ISolutionFinder {

  private final int BIN_COUNT = 1000;
  private final int COLLAPSE_BIN_COUNT = 15;
  private final int TARGET_BIN_COUNT = 40;

  private final ISolutionViewer viewer;

  public OpticalMapSolver(ISolutionViewer viewer) {
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
    //System.out.println("size of bins " + newBinned.size());

    // Refine the solution based on finding the samples that are most
    // similar to the small target
    BinRefiner refiner = new BinRefiner(newBinned);
    solution0 = refiner.genSolution(set); // mostly eliminates noise, we hope

    // Iterate by counting the bins for only the samples that remain
    // included
    OpSolution solution = solution0;
    viewer.update(solution);

    int iterations = 10;
    for (int j = 0; j < iterations; j++) {
      counter = new BinCounter(solution);
      int targetCount = (int) (j + 1) * TARGET_BIN_COUNT / iterations;
      topBins = BinCounter.getTopBins(counter.count(BIN_COUNT),
          targetCount, COLLAPSE_BIN_COUNT);
      newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
      
      OpSolution nextSolution = new OpSolution(set);
      nextSolution.ideal = newBinned;

      // and record if flipped
      List<RankedOpSample> rankedSamples = new ArrayList<RankedOpSample>(
          set.size());
      for (int i = 0; i < set.size(); i++) {
        OpSample s = set.get(i);
        s.flip(false);
        double diff = s.cosine(newBinned);
        s.flip(true);
        double flipDiff = s.cosine(newBinned);
        RankedOpSample ranked = new RankedOpSample();
        ranked.sample = s;
        ranked.sampleIndex = i;
        if (diff < flipDiff) {
          ranked.diff = flipDiff;
          ranked.flipped = true;
        } else {
          ranked.diff = diff;
        }
        rankedSamples.add(ranked);
      }

      // sort them by similarity (descending)
      Collections.sort(rankedSamples, RankedOpSample.RANK_BY_DIFF);
      Collections.reverse(rankedSamples);

      // Calculate the second derivative
      List<Double> diffs = new ArrayList<Double>();
      List<Integer> rankOrder = new ArrayList<Integer>();
      for (RankedOpSample s : rankedSamples) {
        diffs.add(s.diff);
        rankOrder.add(s.sampleIndex);
      }
      nextSolution.rankedOrder = rankOrder;
      StringBuilder sb = new StringBuilder();
      for(Double d : diffs){
        sb.append("" + d + ",");
      }
      String data = sb.toString();
      List<Double> rankDt = dt(dt(diffs));
      // Find the maximum/minimum and set the cut off
      
      //We need to find where the derivative of the slope is decreasing fastest
      //so we need dt(dt()) to be lowest
      int cutoff = minIndex(rankDt, (int)(rankDt.size() * .7), (int)(rankDt.size() *.95)); // We know the error will be in the last 30% (at most)
      if(nextSolution.set.problemType <= 1) {
        // According to the spec, there are no noise molecules in these problem types.
        cutoff = rankDt.size();
      } else {
        //System.out.println("Cut off "+cutoff);
      }
      //System.out.println("Cut off "+cutoff);
      
      // choose the top x percent, then mark the others garbage
      for (int i = 0; i < set.size(); i++) {
        nextSolution.isTarget[i] = false;
      }
      for (int i = 0; i < set.size(); i++) {
        RankedOpSample s = rankedSamples.get(i);
        if (i < cutoff) {
          nextSolution.isTarget[s.sampleIndex] = true;
          nextSolution.isFlipped[s.sampleIndex] = s.flipped;
        }
      }

      nextSolution.ideal = newBinned;

      viewer.update(nextSolution);
      // See how much the solution changed
      // Compare solution / nextSolution
      solution = nextSolution;
    }

    if(solution.set.problemType > 0){
      // Don't merge close cuts for the trivial problem type
      solution = purgeCloseCuts(solution, 0.009);
    }
    //solution = localSearchSolution(solution);
    
    viewer.update(solution);
    
    return solution;
  }
  
  // Implement Local search here to find the best scoring ideal target
  private OpSolution localSearchSolution(OpSolution guess) {

    // TODO: Should probably clone the solution here
    OpSolution bestSolution = guess;
    OpSample bestTarget = bestSolution.ideal;
    CosineScorer scorer = new CosineScorer();
    double nDist = 0.05;
    double gain = 1.0;
    int gIter = 0;
    while (gain > 0.0) {
      gain = 0.0;
      gIter++;
      bestSolution.ideal = bestTarget;
      double best = scorer.score(bestSolution);
      SampleNeighborhood neighborhood = new SampleNeighborhood(bestTarget);
      // double bestNeighbor = 0.0;
      for (OpSample t : neighborhood.genNeighbors(nDist)) {
        bestSolution.ideal = t;
        double nBest = scorer.score(bestSolution);
        if (nBest > best) {
          // Consider a shortcut here - if this is an improvement, short circuit
          gain = nBest - best;
          best = nBest;
          bestTarget = t;
//          break;
        }
      }
      System.out.println("Best: " + best + " gain: " + gain);
    }
    System.out.println("Optimize over " + gIter + " iterations");
    bestSolution.ideal = bestTarget;
    return bestSolution;
  }

  // Remove cuts that are really too close to each other
  private OpSolution purgeCloseCuts(OpSolution guess, double eps) {

    OpSample target = guess.ideal;
    List<Double> newCuts = new ArrayList<Double>(target.size());
    double last = -1;
    for(Double c : target){
      if(c - last < eps){
        // suppress this point by averaging the two
        //newCuts.add((c + last)/ 2.0);
        if(getCutPointToRemove(last, c, guess.set) == last) {
          //remove last
          newCuts.add(c);
        }
        else {
          //remove c
          newCuts.add(last);
        }
        last = -1;
      } else {
        if(last >= 0){
          newCuts.add(last);
        }
        last = c;
      }
    }
    if(last >= 0){
      newCuts.add(last);
    }

    System.out.println("Reduced cut points " + (target.size() - newCuts.size()));
    
    guess.ideal = new OpSample(newCuts);
    return guess;
  }
  
  private double getCutPointToRemove(double c,double n,SampleSet set) {
    List<Double> cList = new ArrayList<Double>();
    cList.add(c);
    List<Double> nList = new ArrayList<Double>();
    cList.add(n);
    double totalC = 0.0;
    double totalN = 0.0;
    for (int i = 0; i < set.size(); i++) {
      OpSample s = set.get(i);
      OpSample other = new OpSample(cList);
      totalC += s.diff(other);
      other = new OpSample(nList);
      totalN += s.diff(other);
    }
    if(totalC <= totalN)
      return c;
    else
      return n;
  }
  
  // Remove cuts that aren't supported by the data
  private OpSolution purgeUnSupportedCuts(OpSolution guess) {

    return guess;
  }

  private static List<Double> dt(List<Double> points) {
    int window = 1;
    List<Double> derivatives = new ArrayList<Double>();
    for (int i = 0; i < points.size(); i++) {
      if (i < (points.size() - window)) { // Make sure to return a vector of the same length
        double d = (points.get(i+window) - points.get(i));
        derivatives.add(d);
      }
      else {
        double d = (points.get(i) - points.get(i - window));
        derivatives.add(d);
      }
    }
    return derivatives;
  }

  private static int maxIndex(List<Double> points, int start, int end) {
    double max = points.get(end);
    int maxIndex = end;
    for (int i = start; i < points.size() && i < end; i++) {
      if ((points.get(i)) > max && (points.get(i) != 0)) {
        maxIndex = i;
        max = points.get(i);
      }
    }
    return maxIndex;
  }
  
  private static int minIndex(List<Double> points, int start, int end) {
    double min = points.get(end);
    int minIndex = end;
    for (int i = start; i < points.size() && i < end; i++) {
      if ((points.get(i)) < min) {
        minIndex = i;
        min = points.get(i);
      }
    }
    return minIndex;
  }

  private static class RankedOpSample {
    public double diff;
    public boolean flipped;
    public OpSample sample;
    public int sampleIndex;

    public static Comparator<RankedOpSample> RANK_BY_DIFF = new Comparator<RankedOpSample>() {

      @Override
      public int compare(RankedOpSample o1, RankedOpSample o2) {
        return (int) Math.signum(o1.diff - o2.diff);
      }
    };
  }

}
