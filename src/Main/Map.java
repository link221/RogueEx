package Main;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

public class Map {
	public Tile[][] tiles = new Tile[10][10];
	String name = "";
	Color bg;
	Random rand = new Random();

	public ArrayList<Enemy> enemies = new ArrayList<>();
	public ArrayList<Item> items = new ArrayList();
	public ArrayList<Light> lights = new ArrayList<Light>();

	int wx, wy, wz;

	public Map(String name, int wx, int wy, int wz) throws SlickException {
		this.name = name;
		this.wx = wx;
		this.wy = wy;
		this.wz = wz;

		lights.add(new Light(0, 0, 200, "player"));

		if (name.equalsIgnoreCase("forest")) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					tiles[i][j] = new Tile("blank");
					if (rand.nextInt(5) == 0) {
						tiles[i][j] = new Tile("grass");
					}
					if (rand.nextInt(10) == 0) {
						if (rand.nextBoolean()) {
							tiles[i][j] = new Tile("tree2");
							if (rand.nextInt(3) == 0) {
								items.add(new Item("apple", i, j));
							}
						} else {
							tiles[i][j] = new Tile("tree");

						}

					}
				}
			}
			if (rand.nextInt(5) == 0) {
				tiles[rand.nextInt(8) + 1][rand.nextInt(8) + 1] = new Tile("cave");
				cave();
			}
			bg = new Color(33, 91, 39);

			for (int i = 0; i < 3; i++) {
				int rx = rand.nextInt(10);
				int ry = rand.nextInt(10);

				if (!tiles[rx][ry].solid) {
					if (rand.nextInt(10) == 0) {
						if (rand.nextInt(3) == 0) {
							enemies.add(new Enemy("cyclops", rx, ry));
						} else {
							enemies.add(new Enemy("bear", rx, ry));
						}

					} else {
						enemies.add(new Enemy("mouse", rx, ry));
					}

				}

			}
		}
		if (name.equalsIgnoreCase("tundra")) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					tiles[i][j] = new Tile("blank");
					if (rand.nextInt(5) == 0) {
						tiles[i][j] = new Tile("grass");
					}
					if (rand.nextInt(10) == 0) {
						if (rand.nextBoolean()) {
							tiles[i][j] = new Tile("deadtree");
						} else {
							tiles[i][j] = new Tile("deadtree");

						}

					}
				}
			}
			bg = new Color(160, 200, 229);
			for (int i = 0; i < 3; i++) {
				int rx = rand.nextInt(10);
				int ry = rand.nextInt(10);
				if (!tiles[rx][ry].solid) {
					if (rand.nextInt(10) == 0) {
						if (rand.nextInt(3) == 0) {
							enemies.add(new Enemy("cyclops", rx, ry));
						} else {
							enemies.add(new Enemy("troll", rx, ry));
						}

					} else {
						enemies.add(new Enemy("sprite", rx, ry));
					}

				}

			}
		}
		if (name.equalsIgnoreCase("cave")) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					tiles[i][j] = new Tile("blank");
					if (rand.nextInt(5) == 0) {
						tiles[i][j] = new Tile("dirt");
					}
					if (rand.nextInt(10) == 0) {
						if (rand.nextBoolean()) {
							tiles[i][j] = new Tile("rock");
						} else {
							tiles[i][j] = new Tile("rock");

						}

					}
				}
			}
			walls();

			bg = new Color(53, 27, 17);

			for (int i = 0; i < 3; i++) {
				int rx = rand.nextInt(10);
				int ry = rand.nextInt(10);

				if (!tiles[rx][ry].solid) {
					if (rand.nextInt(10) == 0) {
						enemies.add(new Enemy("bat", rx, ry));
					} else {
						if (wz > 4 && rand.nextBoolean()) {
							enemies.add(new Enemy("armored skelly", rx, ry));
						} else {
							enemies.add(new Enemy("skeleton", rx, ry));
						}

					}

				}

			}
		}

	}

	public void walls() throws SlickException {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (i == 0 || i == 9) {
					tiles[i][j] = new Tile("upwall");
				}
				if (j == 0 || j == 9) {
					tiles[i][j] = new Tile("sidewall");
				}
				if (i == 0 && j == 0) {
					tiles[i][j] = new Tile("tlwall");
				}
				if (i == 0 && j == 9) {
					tiles[i][j] = new Tile("blwall");
				}
				if (i == 9 && j == 0) {
					tiles[i][j] = new Tile("trwall");
				}
				if (i == 9 && j == 9) {
					tiles[i][j] = new Tile("brwall");
				}
			}
		}

	}

	public void cave() throws SlickException {
		for (int i = 1; i < Main.worldz; i++) {
			Main.world[wx][wy][i] = new Map("cave", wx, wy, i);
			int rx = rand.nextInt(8) + 1;
			int ry = rand.nextInt(8) + 1;
			if (i != Main.worldz - 1 && !Main.world[wx][wy][i].tiles[rx][ry].name.equalsIgnoreCase("stairsup")) {
				Main.world[wx][wy][i].tiles[rx][ry] = new Tile("stairsdown");
			}
			rx = rand.nextInt(8) + 1;
			ry = rand.nextInt(8) + 1;
			if (i > 4 && i != Main.worldz - 1
					&& !Main.world[wx][wy][i].tiles[rx][ry].name.equalsIgnoreCase("stairsup")) {
				Main.world[wx][wy][i].tiles[rx][ry] = new Tile("chest");
			}

		}
	}

	public void leave() throws SlickException {
		for (Enemy e : enemies) {
			e.health = e.maxhealth;
		}
		for (Light lt : lights) {
			if (lt.parent.equalsIgnoreCase("fire")) {
				lt.range = 0;
				lt.glow = false;
				break;
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (tiles[i][j].name.equalsIgnoreCase("fire")) {
					tiles[i][j] = new Tile("blank");
				}
			}
		}

	}

}
