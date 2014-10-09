package hps.nyu.fa14.solver;

import hps.nyu.fa14.ISolutionFinder;
import hps.nyu.fa14.OpSolution;
import hps.nyu.fa14.SampleSet;

public class TrivialSolver implements ISolutionFinder {

	@Override
	public OpSolution generateSolution(SampleSet set) {
		return OpSolution.trivial(set);
	}

}
