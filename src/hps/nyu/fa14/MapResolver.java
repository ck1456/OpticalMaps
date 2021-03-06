package hps.nyu.fa14;

import hps.nyu.fa14.solver.OpticalMapSolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MapResolver implements Runnable {

	private final SampleSet set;

	public MapResolver(SampleSet set) {
		this.set = set;
	}

	public ISolutionViewer viewer;
	
	public OpSolution resolve(){
		//long start = System.currentTimeMillis();
		if(viewer == null){
			viewer = new NullSolutionViewer();
		}
		// Instantiate whatever type of solver you want here
		// ISolutionFinder solver = new RandomSolver();
		ISolutionFinder solver = new OpticalMapSolver(viewer);
		
		// Generate and return an OpSolution
		OpSolution solution = solver.generateSolution(set);
		//System.out.println("Time to solve: " + ((System.currentTimeMillis() - start) / 1000));
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
		
		// Make directory for the output file if it does not exist
		File outFile = new File(outputFile);
		outFile.getParentFile().mkdirs();
		solution.write(new FileOutputStream(outFile));
	}

	private static void usage() {
		// How to use it
		System.out.println("java -jar MapResolver <input> <output>");
		System.exit(1);
	}

	@Override
	public void run() {
		resolve();
	}
}
