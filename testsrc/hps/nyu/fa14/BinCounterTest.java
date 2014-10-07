package hps.nyu.fa14;

import static org.junit.Assert.*;

import org.junit.Test;

public class BinCounterTest {

	@Test
	public void testBinString() {
		SampleSet set = SampleCutter.generate(40, 200);
		
		OpSolution solution = OpSolution.trivial(set);
		BinCounter counter = new BinCounter(solution);
		int[] bins = counter.count(40);
		
		String print = BinCounter.binString(bins);
		assertTrue(print.length() > 0);
	}

}
