package hps.nyu.fa14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SampleSet {

	private final List<OpSample> samples = new ArrayList<OpSample>();

	public int problemType;
	public double digestionProbability;

	public int size(){
		return samples.size();
	}
	
	public void add(OpSample sample) {
		samples.add(sample);
	}

	public OpSample get(int index){
		return samples.get(index);
	}
	
	public static SampleSet parse(InputStream input)
			throws NumberFormatException, IOException {

		SampleSet newSet = new SampleSet();

		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		// read the first two lines
		int type = Integer.parseInt(br.readLine());
		newSet.problemType = type;
		double pC = Double.parseDouble(br.readLine());
		newSet.digestionProbability = pC;

		String line;
		while ((line = br.readLine()) != null) {
			List<Double> cuts = new ArrayList<Double>();
			// Split cuts on whitespace
			for (String c : line.split("\\s")) {
				cuts.add(Double.parseDouble(c));
			}
			newSet.add(new OpSample(cuts));
		}

		return newSet;
	}
}
