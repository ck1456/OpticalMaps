package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BinCounter {

	private final OpSolution solution;

	public BinCounter(OpSolution solution) {
		this.solution = solution;
	}

	public int[] count(int numBins) {
		int[] bins = new int[numBins];
		
		for(OpSample s : solution.getTargetSamples()){
			for(Double cut : s){
				int bin = (int)Math.floor(cut * numBins);
				bin = Math.min(bin, numBins - 1); // Make sure the bin is in range
				bins[bin]++;
			}
		}
		return bins;
	}
	
	public static OpSample newSampleFromBins(int binCount, int[] bins){
	
		double binSize = 1.0 / binCount;
		List<Double> cuts = new ArrayList<Double>();
		for(int b : bins){
			cuts.add((b * binSize) + (binSize / 2.0));
		}
		return new OpSample(cuts);
	}
	
	public static int[] getPercentTopBins(int[] bins, double frac){
		int binCount = (int)(bins.length * frac);
		return getTopBins(bins, binCount, 1);
	}
	
	/**
	 * For the specified bins, returns a fraction of the top ranked bins
	 * @param bins
	 * @param frac
	 * @return
	 */
	public static int[] getPercentTopBins(int[] bins, double frac, int collapseBins){
		int binCount = (int)(bins.length * frac);
		return getTopBins(bins, binCount, collapseBins);
	}
	
	/**
	 * For the specified bins, returns a number of the top ranked bins
	 * @param bins
	 * @param binCount
	 * @return
	 */
	public static int[] getTopBins(int[] bins, int binCount, int collapseBins){
		int[] topBins = new int[binCount];
		
		List<Integer> sortedBins = new ArrayList<Integer>();
		Map<Integer, List<Integer>> binCountMap = new HashMap<Integer, List<Integer>>();
		for(int i = 0; i < bins.length; ++i){
			if(!binCountMap.containsKey(bins[i])){
				binCountMap.put(bins[i], new ArrayList<Integer>());
			}
			binCountMap.get(bins[i]).add(i);
			sortedBins.add(bins[i]);
		}
		Collections.sort(sortedBins);
		Collections.reverse(sortedBins); // sort descending
		
		Set<Integer> countedBins = new HashSet<Integer>();
		int i = 0;
		int j = 0;
		while(i < binCount && j < sortedBins.size()){
//		for(int i = 0; i < binCount; i++){
			int binValue = sortedBins.get(j++);
			int binIndex = binCountMap.get(binValue).remove(0); 
			if(!countedBins.contains(binIndex)){
				topBins[i++] = binIndex;
				countedBins.add(binIndex);
				// Add the other ones to ignore
				for(int b = 1; b < collapseBins / 2; b++){
					if(binIndex + b < bins.length){
						countedBins.add(binIndex + b);
					}
					if(binIndex - b >= 0){
						countedBins.add(binIndex - b);
					}
				}
			}
		}
				
		return topBins;
	}
	
	// TODO: Implement a 2-means clustering algorithm to determine how many bins to return
	public static int[] getPercentTopBins(int[] bins){
		throw new  UnsupportedOperationException("Not Implemented");
		
	}
	public static String binString(int[] bins){
		StringBuilder sb = new StringBuilder();
		
		for(int b : bins){
			sb.append(String.format("%d ", b));
		}
		return sb.toString();
	}

}
