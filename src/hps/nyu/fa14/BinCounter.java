package hps.nyu.fa14;

public class BinCounter {

	private final OpSolution solution;

	public BinCounter(OpSolution solution) {
		this.solution = solution;
	}

	public int[] count(int numBins) {
		int[] bins = new int[numBins];
		
		for(OpSample s : solution.getSamples()){
			for(Double cut : s){
				int bin = (int)Math.floor(cut * numBins);
				bin = Math.min(bin, numBins - 1); // Make sure the bin is in range
				bins[bin]++;
			}
		}
		
		return bins;
	}
	
	public static String binString(int[] bins){
		StringBuilder sb = new StringBuilder();
		
		for(int b : bins){
			sb.append(String.format("%d ", b));
		}
		return sb.toString();
	}

}
