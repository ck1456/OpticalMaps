package hps.nyu.fa14;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class OpSolution {

	public final SampleSet set;

	public OpSample ideal;
	public List<Integer> rankedOrder;
	
	public final boolean[] isTarget;
	public final boolean[] isFlipped;

	public OpSolution(SampleSet set) {
		this.set = set;
		isTarget = new boolean[this.set.size()];
		isFlipped = new boolean[this.set.size()];
		rankedOrder = new ArrayList<Integer>();
		for(int i = 0; i < set.size(); i++){
			rankedOrder.add(i);
		}
	}

	public Iterable<OpSample> getTargetSamples() {

		return new Iterable<OpSample>() {
			@Override
			public Iterator<OpSample> iterator() {
				return new TargetSampleIterator();
			}
		};
	}

	/**
	 * returns all of the samples that have not been marked as noise, in their
	 * correct flip orientation
	 */
	private class TargetSampleIterator implements Iterator<OpSample> {

		int nextPosition;

		OpSample next = null;

		TargetSampleIterator() {
			setNext();
		}

		private void setNext() {
			next = null;
			while (nextPosition < set.size()) {
				if (isTarget[nextPosition]) {
					next = set.get(nextPosition);
					next.flip(isFlipped[nextPosition]);
				}
				nextPosition++;
				if (next != null) {
					return;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public OpSample next() {
			if (next == null) {
				throw new ArrayIndexOutOfBoundsException();
			}
			OpSample toReturn = next;
			setNext();
			return toReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public void write(OutputStream output) throws IOException {

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
		// First line is idealized cut points
		boolean first = true;
		for (Double cut : ideal) {
			if (!first) {
				bw.write(" ");
			}
			first = false;
			bw.write(String.format("%f", cut));
		}
		bw.write(String.format("%n"));

		for (int i = 0; i < set.size(); i++) {
			bw.write(String.format("%d", (isTarget[i] ? 1 : 0)));
			if (isTarget[i]) {
				bw.write(String.format(" %d", (isFlipped[i] ? 1 : 0)));
			}
			bw.write(String.format("%n"));
		}
		bw.close();
	}

	public static OpSolution generateRandom(SampleSet set) {
		Random rand = new Random();
		OpSolution s = new OpSolution(set);
		s.ideal = set.get(rand.nextInt(set.size()));
		for (int i = 0; i < set.size(); i++) {
			s.isTarget[i] = (rand.nextDouble() >= .5);
			s.isFlipped[i] = (rand.nextDouble() >= .5);
		}
		return s;
	}

	public static OpSolution trivial(SampleSet set) {
		OpSolution s = new OpSolution(set);
		s.ideal = set.get(0);
		for (int i = 0; i < set.size(); i++) {
			s.isTarget[i] = true;
			s.isFlipped[i] = false;
		}
		return s;
	}

}
