package hps.nyu.fa14.view;

import hps.nyu.fa14.ISolutionViewer;
import hps.nyu.fa14.MapResolver;
import hps.nyu.fa14.OpSample;
import hps.nyu.fa14.OpSolution;
import hps.nyu.fa14.SampleSet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;

public class OpticalMapplet extends Applet implements Runnable, ISolutionViewer {

	private static final long serialVersionUID = 4901830364284199595L;

	private Thread t;
	private Graphics buffer;
	private Image image;
	private int width = 0;
	private int height = 0;

	@Override
	public void update(Graphics g) {
		if (width != getWidth() || height != getHeight()) {
			width = getWidth();
			height = getHeight();
			image = createImage(width, height);
			buffer = image.getGraphics();
		}
		render(buffer);
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public void start() {
		if (t == null) {
			(t = new Thread(this)).start();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				repaint();
				Thread.sleep(30);
			}
		} catch (Exception ex) {
		}
	}

	private OpSolution solution;
	private final Thread calcThread;

	public OpticalMapplet() throws Exception {
		SampleSet set = SampleSet.parse(new FileInputStream(new File(
				"../data/test_problem_0_num_0.txt")));

		MapResolver resolver = new MapResolver(set);
		resolver.viewer = this;
		(calcThread = new Thread(resolver)).start();
	}

	/**
	 * The main render routine
	 * 
	 * @param g
	 */
	private void render(Graphics g) {

		OpSolution sol = solution;
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		// render the ideal solution
		int y = 0;
		g.setColor(Color.cyan);
		for (Double c : sol.ideal) {
			int x = (int) (c * width);
			g.drawLine(x, y, x, y + 4);
		}
		y+=5;
		
		// render the target samples
		for (int i = 0; i < sol.set.size(); i++) {
			if (sol.isTarget[i] && !sol.isFlipped[i]) {
				g.setColor(Color.green);

				y += 2;
				OpSample s = sol.set.get(i);
				s.flip(sol.isFlipped[i]);
				for (Double c : s) {
					int x = (int) (c * width);
					g.drawLine(x, y, x, y + 1);
				}
			}
		}
		
		for (int i = 0; i < sol.set.size(); i++) {
			if (sol.isTarget[i] && sol.isFlipped[i]) {
				g.setColor(Color.yellow);

				y += 2;
				OpSample s = sol.set.get(i);
				s.flip(sol.isFlipped[i]);
				for (Double c : s) {
					int x = (int) (c * width);
					g.drawLine(x, y, x, y + 1);
				}
			}
		}

		// render noise
		for (int i = 0; i < sol.set.size(); i++) {
			if (!sol.isTarget[i]) {

				g.setColor(Color.red);
				// figure out how to mark flipped
				y += 2;
				for (Double c : sol.set.get(i)) {
					int x = (int) (c * width);
					g.drawLine(x, y, x, y + 1);
				}
			}
		}

		// TODO: Figure out how to sort the molecules in order of correlation
		
		// Draw a white flip line down the middle
		g.setColor(Color.white);
		int x = (int) (0.5 * width);
		g.drawLine(x, 0, x, height);
	}

	@Override
	public void update(OpSolution newSolution) {
		solution = newSolution;
	}

}
