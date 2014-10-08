package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BinRefiner {

	public final OpSample target;
	
	BinRefiner(OpSample target){
		this.target = target;
	}
	
	public OpSolution genSolution(SampleSet set){
		OpSolution solution = new OpSolution(set);
	
		// get the min diff for each sample with the target
		// and record if flipped
		List<RankedOpSample> rankedSamples = new ArrayList<RankedOpSample>(set.size());
		for(int i = 0; i < set.size(); i++){
			OpSample s = set.get(i);
			s.flip(false);
			double diff = s.diff(target);
			s.flip(true);
			double flipDiff = s.diff(target);
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
		double keep = .7;
		for(int i = 0; i < set.size(); i++){
			solution.isTarget[i] = false;
		}
		for(int i = 0; i < keep * set.size(); i++){
			RankedOpSample s = rankedSamples.get(i);
			solution.isTarget[s.sampleIndex] = true;
			solution.isFlipped[s.sampleIndex] = s.flipped;
		}
		
		return solution;
	}
	
	private static class RankedOpSample{
		public double diff;
		public boolean flipped;
		public OpSample sample;
		public int sampleIndex;
		
		public static Comparator<RankedOpSample> RANK_BY_DIFF = new Comparator<BinRefiner.RankedOpSample>() {

			@Override
			public int compare(RankedOpSample o1, RankedOpSample o2) {
				return (int)Math.signum(o1.diff - o2.diff);
			}
		};
	}
	
}
