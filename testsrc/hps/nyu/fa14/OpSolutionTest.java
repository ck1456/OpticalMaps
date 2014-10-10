package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class OpSolutionTest {

	@Test
	public void testGetSamples() {
		SampleSet set = new SampleSet();
		set.add(new OpSample(new ArrayList<Double>()));
		set.add(new OpSample(new ArrayList<Double>()));
		set.add(new OpSample(new ArrayList<Double>()));

		OpSolution solution = OpSolution.trivial(set);
		solution.isTarget[1] = false;
		solution.isFlipped[2] = true;
		
		int sampleCount = 0;
		for(OpSample s : solution.getTargetSamples()){
			++sampleCount;
			if(sampleCount == 1){
				assertFalse(s.isFlipped());
			}
			if(sampleCount == 2){
				assertTrue(s.isFlipped());
			}
		}
		assertEquals(2, sampleCount);
	}

}
