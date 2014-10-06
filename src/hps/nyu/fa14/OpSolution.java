package hps.nyu.fa14;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class OpSolution {

	private final SampleSet set;

	private OpSample ideal;

	private final boolean[] isTarget;
	private final boolean[] isFlipped;

	public OpSolution(SampleSet set) {
		this.set = set;
		isTarget = new boolean[this.set.size()];
		isFlipped = new boolean[this.set.size()];
	}

	public void write(OutputStream output) throws IOException {

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
		// First line is idealized cut points
		boolean first = true;
		for (Double cut : ideal) {
			if(!first){
				bw.write(" ");
			}
			first = false;
			bw.write(String.format("%f", cut));
		}
		bw.write(String.format("%n"));
		
		for(int i = 0; i < set.size(); i++){
			bw.write(String.format("%d", (isTarget[i] ? 1 : 0)));
			if(isTarget[i]){
				bw.write(String.format(" %d", (isFlipped[i] ? 1 : 0)));
			}
			bw.write(String.format("%n"));
		}
		bw.close();
	}
	
	public static OpSolution generateRandom(SampleSet set){
	Random rand = new Random();
		OpSolution s = new OpSolution(set);
		s.ideal = set.get(rand.nextInt(set.size()));
		for(int i = 0; i < set.size(); i++){
			s.isTarget[i] = (rand.nextDouble() >= .5);
			s.isFlipped[i] = (rand.nextDouble() >= .5);
		}
		return s;
	}

}
