package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class BinCounterTest {

	@Test
	public void testBinString() {
		SampleSet set = SampleCutter.generate(40, 200);
		
		OpSolution solution = OpSolution.trivial(set);
		BinCounter counter = new BinCounter(solution);
		int[] bins = counter.count(1000);
		
		String print = BinCounter.binString(bins);
		assertTrue(print.length() > 0);
	}
	
	@Test
	public void testBinSampleSet1() throws Exception {
		File sampleFile = new File("data/test_problem_0_num_0.txt");
		
		SampleSet set = SampleSet.parse(new FileInputStream(sampleFile));
		
		final int BIN_COUNT = 1000;
		OpSolution solution = OpSolution.trivial(set);
		BinCounter counter = new BinCounter(solution);
		int[] bins = counter.count(BIN_COUNT);
		
		String print = BinCounter.binString(bins);
		assertTrue(print.length() > 0);
		
		int[] topBins = BinCounter.getPercentTopBins(bins, .1);
		
		assertEquals(963, topBins[0]);
		
		OpSample newBinned = BinCounter.newSampleFromBins(BIN_COUNT, topBins);
		
		assertEquals(100, newBinned.size());
	}

}
