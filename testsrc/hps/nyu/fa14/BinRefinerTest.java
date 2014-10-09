package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class BinRefinerTest {

	@Test
	public void testGenSolutionSample1() throws Exception {

		File sampleFile = new File("data/test_problem_0_num_0.txt");
		SampleSet set = SampleSet.parse(new FileInputStream(sampleFile));
		final int BIN_COUNT = 1000;
		BinCounter counter = new BinCounter(OpSolution.trivial(set));
		int[] topBins = BinCounter.getPercentTopBins(counter.count(BIN_COUNT), .1);

		OpSample newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
		
		BinRefiner refiner = new BinRefiner(newBinned);
		OpSolution solution0 = refiner.genSolution(set);
		
		// TODO: Assert something stronger
		assertNotNull(solution0);
	}

}
