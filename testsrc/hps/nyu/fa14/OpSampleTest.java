package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class OpSampleTest {

	private static final double eps = 1e-10;

	@Test
	public void testDiff() {
		SampleSet set = SampleCutter.generate(40, 200);
		OpSample sample0 = set.get(0);
		assertEquals(0.0, sample0.diff(sample0), eps);

		for (int i = 1; i < set.size(); i++) {
			OpSample sample = set.get(i);
			assertEquals("samples aren't equal", 0.0, sample.diff(sample), eps);
			assertTrue("samples aren't different", sample.diff(sample0) > eps);
//			if (sample0.size() == sample.size()) {
//				assertEquals("sample diff isn't symmetric",
//						sample0.diff(sample), sample.diff(sample0), eps);
//			}
		}
	}

	@Test
	public void testDiffValue() {
		OpSample sample0 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.25,
				0.75 }));
		OpSample sample1 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.35,
				0.75 }));
		assertEquals("sample diff isn't symmetric", sample0.diff(sample1),
				sample1.diff(sample0), eps);
		assertEquals("sample diff isn't expected", .1, sample1.diff(sample0),
				eps);
	}

	@Test
	public void testDiffValueLength() {
		OpSample sample0 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.25,
				0.75 }));
		OpSample sample1 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.35,
				0.75, .9 }));
		assertEquals("sample diff isn't expected", .1, sample1.diff(sample0),
				eps);
		assertEquals("sample diff isn't expected", .25, sample0.diff(sample1),
				eps);
	}
	
	@Test
	public void testDiffValueLength2() {
		OpSample sample0 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.25,
				0.9 }));
		OpSample sample1 = new OpSample(Arrays.asList(new Double[] { 0.1, 0.35,
				0.75, 0.85 }));
		assertEquals("sample diff isn't expected", .15, sample1.diff(sample0),
				eps);
		assertEquals("sample diff isn't expected", .30, sample0.diff(sample1),
				eps);
	}
}
