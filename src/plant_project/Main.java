package plant_project;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Main {

	/**
	 * Water value in percent
	 */
	public static int water = 50;

	/**
	 * Sun value in percent
	 */
	public static int sun = 50;

	/**
	 * Soil nutrition
	 */
	public static int nutrition = 50;

	/**
	 * Mutation probability
	 */
	public static int mutation = 50;

	/**
	 * Plants in world
	 */
	public ArrayList<Plant> plants;

	/**
	 * Drawing window
	 */
	public Drawer drawer;

	/**
	 * Use input from GL
	 */
	public void keyPress(char c, String in)
	{
		switch (c) {
			case 'p':
				// plant p=(probability:find:replace; ...), reproducecount, maturityage
				if (in.length() < 3) {
					plants.add(new Plant(1, 0, new Gene(15,
						new LRule[] { new LRule(0.5, 'X', "Gs[[G]eX]nG[wGX]G"),
						              new LRule(0.5, 'G', "GG") },
						new Color(0.2f, 0.9f, 0.0f),
						Color.BLUE,
						2, Plant.MAX_ITERATION,
						0.01f, 0.1f)));
				} else {
					String[] plantElements = in.split("=")[1].split(";");
					String[] ruleStrings = plantElements[0].substring(1,
							plantElements[0].length()-2).split(",");
					LRule rules[] = new LRule[ruleStrings.length];
					for (int i = 0; i < ruleStrings.length; i++)
						rules[i] = new LRule(ruleStrings[i]);
					plants.add(new Plant(0, 0, new Gene(15,
						rules, Color.GREEN, Color.BLUE,
						Integer.parseInt(plantElements[1]),
						Integer.parseInt(plantElements[2]),
						0.01f, 0.1f)));
				}
				System.out.println("planted in 0, 0!");
				break;
			case 's':
				sun = Integer.parseInt(in.split("=")[1]);
				System.out.println(String.format("%d%% sun", sun));
				break;
			case 'w':
				water = Integer.parseInt(in.split("=")[1]);
				System.out.println(String.format("%d%% water", water));
				break;
			case 'm':
				mutation = Integer.parseInt(in.split("=")[1]);
				System.out.println(String.format("%d%% mutation probability",
							mutation));
				break;
			case 'n':
				nutrition = Integer.parseInt(in.split("=")[1]);
				System.out.println(String.format("%d%% nutrition ", nutrition));
				break;
			case 'q':
				this.drawer.quit(); // does not return
				break;
			case 'c':
				this.screenshot(); // take screen capture
				break;
			case ' ':
				this.drawer.toggle();
			default:
				// System.out.println("tick");
				this.tick();
				drawer.draw(plants);
				break;
		}
	}

	public void screenshot() {
		final Date time = new Date();
		final String path = String.format("img/screenshot-%tY%tm%td_%tH%tM%tS_%tL.png",
				time, time, time, time, time, time, time);
		System.out.println("Saving - " + path);
		try {
			File file = new File(path);
			file.mkdirs();
			BufferedImage img = this.drawer.toImage(true);
			ImageIO.write(img, "png", file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void tick() {
		// System.out.println("tick");

		// TODO - use an interator or something instead of
		//        doing an array copies of plants.
		ArrayList<Plant> tmpPlants;

		// TODO - make this a global
		//        is this event the best way to do this?
		float max_plants = 50;

		// Kill off old plants
		tmpPlants = new ArrayList<Plant>(plants);
		for (Plant plant : tmpPlants)
			if (plant.getAge() >= Plant.MAX_AGE)
				plants.remove(plant);

		// Run fitness function
		//float num = (float)plants.size();
		//for (Plant plant : plants)
		//	plant.update(water/num, sun/num, nutrition/num);
		for (Plant plant : plants)
			plant.update(water, sun, nutrition);

		// Sort plants and remove unfit ones
		Collections.shuffle(plants);
		Collections.sort(plants);
		while (plants.size() > max_plants)
			plants.remove(0);

		// Add offspring
		tmpPlants = new ArrayList<Plant>(plants);
		for (Plant plant : tmpPlants)
			plants.addAll(plant.grow());
	}

	/**
	 * Main constructor
	 */
	public Main() {
		this.plants = new ArrayList<Plant>();
		this.drawer = new Drawer(this);

		/* Wikipedia example L-System */
		//plants.add(new Plant(0, 0, new Gene(15,
		//	new LRule[] { new LRule(1, 'X', "Gn[[X]sX]sG[sGX]nX"),
		//	              new LRule(1, 'G', "GG") },
		//	new Color(0.0f, 0.8f, 0.2f),
		//	new Color(0.3f, 0.3f, 0.8f),
		//	2, Plant.MAX_ITERATION)));

		plants.add(new Plant(0, 0, new Gene(15,
			new LRule[] { new LRule(0.75, 'X', "Gn[[X]sXL]eG[sGXF]X"),
			              new LRule(0.75, 'G', "GG") },
			new Color(0.0f, 0.8f, 0.2f),
			new Color(0.3f, 0.3f, 0.8f),
			2, Plant.MAX_ITERATION, 0.01f, 0.1f)));

		plants.add(new Plant(1, 0, new Gene(15,
			new LRule[] { new LRule(0.75, 'X', "Gs[[GL]eX]nG[wGXL]G"),
			              new LRule(0.75, 'G', "GG") },
			new Color(0.2f, 0.9f, 0.9f),
			new Color(0.8f, 0.3f, 0.3f),
			2, Plant.MAX_ITERATION, 0.01f, 0.1f)));

		System.out.format("%-16s %3d%% (s=?)\n", "Sun:", sun);
		System.out.format("%-16s %3d%% (w=?)\n", "Water:", water);
		System.out.format("%-16s %3d%% (m=?)\n", "Mutation Prob:", mutation);
	}

	/**
	 * Main loop
	 */
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String in = sc.nextLine();
			char c = in.length() > 0 ? in.charAt(0) : ' ';
			keyPress(c, in);
		}
	}

	/**
	 * Main..
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}
}
