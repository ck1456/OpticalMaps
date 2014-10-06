package hps.nyu.fa14;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OpSample implements Iterable<Double> {

	/**
	 * A sorted list of the cuts in one sample
	 */
	private final List<Double> cuts = new ArrayList<Double>();
	private final List<Double> cutsFlipped = new ArrayList<Double>();
	
	public OpSample(List<Double> cuts){
		for(Double c : cuts){
			this.cuts.add(c);
			this.cutsFlipped.add(1.0 -c);
		}
		Collections.sort(this.cuts);
		Collections.sort(this.cutsFlipped);
	}
		
	private boolean flipped = false;
	
	public boolean isFlipped(){
		return flipped;
	}
	
	public boolean flip(){
		flipped = !flipped;
		return flipped;
	}
	
	public void flip(boolean flip){
		flipped = flip;
	}
	
	@Override
	public Iterator<Double> iterator() {
		if(!flipped){
			return cuts.iterator();
		} else {
			return cutsFlipped.iterator();
		}
	}
	
}
