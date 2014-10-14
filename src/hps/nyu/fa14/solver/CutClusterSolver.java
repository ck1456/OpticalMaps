package hps.nyu.fa14.solver;

import hps.nyu.fa14.BinCounter;
import hps.nyu.fa14.BinRefiner;
import hps.nyu.fa14.ISolutionFinder;
import hps.nyu.fa14.ISolutionViewer;
import hps.nyu.fa14.OpSample;
import hps.nyu.fa14.OpSolution;
import hps.nyu.fa14.SampleSet;

public class CutClusterSolver implements ISolutionFinder {

	private final int BIN_COUNT = 500;
	private final int COLLAPSE_BIN_COUNT = 11;
	private final int TARGET_BIN_COUNT = 40;

	private final ISolutionViewer viewer;
	public CutClusterSolver(ISolutionViewer viewer){
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

		// Refine the solution based on finding the samples that are most
		// similar to the small target
		BinRefiner refiner = new BinRefiner(newBinned);
		solution0 = refiner.genSolution(set); // mostly eliminates
													     // noise, we hope

		// Iterate by counting the bins for only the samples that remain
		// included
		OpSolution solution = solution0;
		viewer.update(solution);

		// TODO: Figure out how to cluster for the right percentage of bins
		int iterations = 10;
		for (int i = 0; i < iterations; i++) {
			counter = new BinCounter(solution);
			int targetCount = (int) (i + 1) * TARGET_BIN_COUNT / iterations;
			topBins = BinCounter.getTopBins(counter.count(BIN_COUNT),
					targetCount, COLLAPSE_BIN_COUNT);
			newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
			refiner = new BinRefiner(newBinned);
			refiner.keepPortion = (i + 1) * 0.7 / iterations;
			OpSolution nextSolution = refiner.genSolution(set);

			viewer.update(nextSolution);
			// See how much the solution changed
			// Compare solution / nextSolution
			solution = nextSolution;
		}

		return solution;
	}

}
