package hps.nyu.fa14;

import static org.junit.Assert.*;

import hps.nyu.fa14.solver.CutClusterSolver;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class CutClusterSolverTest {

	@Test
	public void testGenerateSolution() throws Exception {
		File sampleFile = new File("data/test_problem_0_num_0.txt");
		SampleSet set = SampleSet.parse(new FileInputStream(sampleFile));
		
		CutClusterSolver solver = new CutClusterSolver(new NullSolutionViewer());
		OpSolution solution = solver.generateSolution(set);
		
		// TODO: Assert something stronger
		assertNotNull(solution);
		
		// maybe how long it takes?
	}

}
