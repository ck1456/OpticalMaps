package hps.nyu.fa14;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MapResolver {

	private final SampleSet set;

	public MapResolver(SampleSet set) {
		this.set = set;
	}

	public OpSolution resolve(){
		// Generate and return an OpSolution
		OpSolution solution = OpSolution.generateRandom(this.set);
		
		// TODO: This needs to be implemented better
		
		return solution;
	}
	
	
	public static void main(String[] args) throws NumberFormatException,
			FileNotFoundException, IOException {
		if (args.length != 2) {
			usage();
		}
		// first parameter is input
		String inputFile = args[0];
		String outputFile = args[1];

		SampleSet inputSet = SampleSet.parse(new FileInputStream(new File(
				inputFile)));

		MapResolver resolver = new MapResolver(inputSet);
		OpSolution solution = resolver.resolve();
		
		solution.write(new FileOutputStream(new File(outputFile)));
	}

	private static void usage() {
		// How to use it
		System.out.println("java -jar MapResolver <input> <output>");
		System.exit(1);
	}

}
