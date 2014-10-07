package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleCutter {

	// Partial cut efficiency
	private static final double pC = 0.9;

	// Spurious cut efficiency
	private static final double sC = 0.01;

	// Epsilon jiggle
	private static final double eps = 0.05;
	
	// Garbage
	private static final double pG = 0.25;

	public static SampleSet generate(int cuts, int samples){
		
		Random rand = new Random();
		List<Double> genCuts = new ArrayList<Double>();
		for(int i = 0; i < cuts; i++){
			genCuts.add(rand.nextDouble());
		}
		OpSample baseSample = new OpSample(genCuts);
		SampleSet newSet = new SampleSet();

		for(int s = 0; s < samples; s++){
			List<Double> sampleCuts = new ArrayList<Double>();
			for(Double d : baseSample){
				if(rand.nextDouble() < pC){
					// Simulate partial digestion
					sampleCuts.add(jiggle(d));
				}
				if(rand.nextDouble() < sC){
					// Simulate spurious digestion
					sampleCuts.add(rand.nextDouble());
				}
			}
			if(rand.nextDouble() < 0.5){
				flip(sampleCuts);
			}
			newSet.add(new OpSample(sampleCuts));
			// With some probability, generate total garbage
		}

		return newSet;
	}
	
	private static final Random RAND = new Random();
	private static double jiggle(double p){
		// ensure the jiggled value is still in the right range
		return Math.min(1, Math.max(0, p + ((RAND.nextDouble() - 0.5) * eps)));
	}
	
	private static void flip(List<Double> cuts){
		for(int i = 0; i < cuts.size(); i++){
			cuts.set(i, 1.0 - cuts.get(i));
		}
	}
}
