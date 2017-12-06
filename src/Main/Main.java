package Main;

import java.awt.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.*;

public class Main extends BasicGame {

	static Random rand;

	public static AngelCodeFont font, smallfont;
	public static int worldx = 20, worldy = 20, worldz = 10;
	public static Map[][][] world = new Map[worldx][worldy][worldz];

	public static int lrx = 80, lry = lrx, resolutionx = 1280, resolutiony = 720;
	public static boolean fullscreen = true;
	public static int[][] lighting = new int[lrx][lry];
	public static int wx = 5, wy = 5, turncnt = 0;
	public static Player player;
	public static int turn = 0, depth = 0, money = 0;
	public static float time = 20, delay = 50;
	public static boolean day = false;
	public static Image ehealth, healthbar, bg, box, hunger, mana, nomana, night, fuel, perk;
	public static int perkshopx = 200, perkshopy = 300;
	public static int perksx = 700, perksy = 300;
	
	public static String state = "upgrade";

	static Sound chop;

	public static ArrayList<DamageNumber> damagenumbers = new ArrayList<DamageNumber>();
	public static ArrayList<Perk> perkshop = new ArrayList<Perk>();
	public static ArrayList<Perk> perks = new ArrayList<Perk>();
	public static int perklimit = 3, slotcost = 100;

	public Main() {
		super("platformer");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {

		rand = new Random();
		font = new AngelCodeFont("res/fonts/plainfont.fnt", "res/fonts/plainfont_00.png");
		smallfont = new AngelCodeFont("res/fonts/smallfont.fnt", "res/fonts/smallfont_00.png");

		ehealth = new Image("res/ehealth.png");
		ehealth.setFilter(Image.FILTER_NEAREST);
		healthbar = new Image("res/healthbar.png");
		healthbar.setFilter(Image.FILTER_NEAREST);
		box = new Image("res/box.png");
		box.setFilter(Image.FILTER_NEAREST);
		bg = new Image("res/bg.png");
		bg.setFilter(Image.FILTER_NEAREST);
		hunger = new Image("res/hunger.png");
		hunger.setFilter(Image.FILTER_NEAREST);
		mana = new Image("res/mana.png");
		mana.setFilter(Image.FILTER_NEAREST);
		nomana = new Image("res/nomana.png");
		nomana.setFilter(Image.FILTER_NEAREST);
		night = new Image("res/night.png");
		night.setFilter(Image.FILTER_NEAREST);
		fuel = new Image("res/torch.png");
		fuel.setFilter(Image.FILTER_NEAREST);
		perk = new Image("res/perk.png");
		perk.setFilter(Image.FILTER_NEAREST);

		chop = new Sound("res/sounds/chop.wav");

		Perk.populate();

	}

	public void resetGame() throws SlickException {
		player = new Player(5, 5);
		wx = (int) (worldx / 2);
		wy = (int) (worldy / 2);
		depth = 0;
		time = 20;
		day = false;
		turncnt = 0;
		turn = 0;
		for (int i = 0; i < worldx; i++) {
			for (int j = 0; j < worldy; j++) {
				if(j < 4) {
					world[i][j][depth] = new Map("Tundra", i, j, 0);
				}else {
					world[i][j][depth] = new Map("Forest", i, j, 0);
				}
				
			}
		}
		updateLighting();

	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input in = gc.getInput();
		if (state.equalsIgnoreCase("game")) {
			player.losthealth = player.losthealth * 0.95;

			for (DamageNumber dn : damagenumbers) {
				dn.tick();
				if (dn.life <= 0) {
					damagenumbers.remove(dn);
					break;
				}
			}
			int incr = 0;
			for (Item it : player.weapons) {
				it.index = incr;
				incr++;
			}
			for (Enemy e : world[wx][wy][depth].enemies) {
				if (e.losthealth > 0)
					e.losthealth = e.losthealth * 0.95;

			}
			for (Light lt : world[wx][wy][depth].lights) {
				lt.tick();
			}
			updateLighting();
			for (Item i : player.inventory) {
				if (i.quantity <= 0) {
					player.inventory.remove(i);
					break;
				}
			}
			if (turn == 0) {
				player.tick(in);
				for (int i = 0; i < world[wx][wy][depth].enemies.size(); i++) {
					world[wx][wy][depth].enemies.get(i).moves = world[wx][wy][depth].enemies.get(i).maxmoves;
					// System.out.println("for loop");
					if (world[wx][wy][depth].enemies.get(i).health <= 0) {
						System.out.println("enemy died");
						world[wx][wy][depth].tiles[world[wx][wy][depth].enemies.get(i).x][world[wx][wy][depth].enemies
								.get(i).y] = new Tile("blood");
						world[wx][wy][depth].enemies.remove(i);

						break;
					}
				}

				// System.out.println("player ticked");
			}
			int turnlength = 10;
			if (turn == 1) {
				if (turncnt / turnlength == world[wx][wy][depth].enemies.size()) {
					turn = 0;
					player.hunger -= 0.3;
					if (hasPerk("Metabolism")) {
						player.hunger += 0.1;
					}
					turncnt = 0;
				} else {
					turncnt++;
					if (turncnt % turnlength == 0) {
						world[wx][wy][depth].enemies.get((turncnt / turnlength) - 1).turn();
						if (world[wx][wy][depth].enemies.get((turncnt / turnlength) - 1).moves > 0) {
							world[wx][wy][depth].enemies.get((turncnt / turnlength) - 1).moves--;
							turncnt -= turnlength - 1;
						}
					}

				}

			}
		} else if (state.equalsIgnoreCase("upgrade")) {
			double mx = in.getMouseX();
			double my = in.getMouseY();

			if (in.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				if (mx > perkshopx && mx < perkshopx + perk.getWidth() && my > perkshopy) {
					int index = 0;
					index = (int) ((my - perkshopy) / 50);
					if (index < perkshop.size()) {
						if (perkshop.get(index).locked) {
							if (perkshop.get(index).cost <= money) {
								money -= perkshop.get(index).cost;
								perkshop.get(index).locked = false;
							}
						} else if (!perkshop.get(index).locked && perks.size() < perklimit) {
							perks.add(perkshop.get(index));
							perkshop.remove(index);

						}

					}

				}
				if (mx > perksx && mx < perksx + perk.getWidth() && my > perksy) {
					int index = 0;
					index = (int) ((my - perksy) / 50);
					if (index < perks.size()) {
						perkshop.add(0, perks.get(index));
						perks.remove(index);
					}

				}
			}

			if (in.isKeyPressed(Input.KEY_ENTER)) {
				state = "load";
			}
			if (in.isKeyPressed(Input.KEY_SPACE) && money >= slotcost) {
				money -= slotcost;
				perklimit++;
				slotcost += 100;
			}

		} else if (state.equalsIgnoreCase("dead")) {
			if (in.isKeyPressed(Input.KEY_ENTER)) {
				state = "upgrade";
			}

		} else if (state.equalsIgnoreCase("load")) {
			state = "game";
			resetGame();
		}

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setAntiAlias(false);
		//g.scale((float) ((double) resolutionx / 1080.0), (float) ((double) resolutiony / 720.0));

		if (state.equalsIgnoreCase("game")) {
			g.setColor(world[wx][wy][depth].bg);
			g.fillRect(0, 0, 720, 720);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					world[wx][wy][depth].tiles[i][j].img.draw((72 * i) + 0, 72 * j, 72, 72);
					if (world[wx][wy][depth].tiles[i][j].health < world[wx][wy][depth].tiles[i][j].maxhealth) {
						g.setColor(Color.black);
						g.fillRect((i * 72) + 19, (j * 72) + 55, 42, 14);
						g.setColor(new Color(87, 164, 219));
						g.fillRect((i * 72) + 19, (j * 72) + 55,
								(float) (42 * ((double) world[wx][wy][depth].tiles[i][j].health
										/ world[wx][wy][depth].tiles[i][j].maxhealth)),
								14);

						ehealth.draw((i * 72) + 17, (j * 72) + 55, 46, 14);
					}
				}
			}

			player.img.draw((72 * player.x) + 0, 72 * player.y, 72, 72);

			for (Enemy e : world[wx][wy][depth].enemies) {
				e.img.draw((72 * e.x) + 0, 72 * e.y, 72, 72);
				if (e.health < e.maxhealth) {
					g.setColor(Color.black);
					g.fillRect((e.x * 72) + 19, (e.y * 72) + 55, 42, 14);
					g.setColor(new Color(255, 222, 58));
					g.fillRect((e.x * 72) + 19, (e.y * 72) + 55, (float) (42 * ((double) e.health / e.maxhealth)), 14);
					g.setColor(new Color(255, 48, 48));
					g.fillRect((float) ((e.x * 72) + 19 + (42 * ((double) e.health / e.maxhealth))), (e.y * 72) + 55,
							(float) (42 * ((double) e.losthealth / e.maxhealth)), 14);
					ehealth.draw((e.x * 72) + 17, (e.y * 72) + 55, 46, 14);
				}

			}
			for (Item it : world[wx][wy][depth].items) {
				it.img.draw((72 * it.x) + it.osx + (72 / 4), 72 * it.y + it.osy + (72 / 4), 72 / 2, 72 / 2);

			}

			// TIME FILTER
			// g.setColor(new Color(25,10,68,(int)(time)));
			// g.fillRect(0, 0, 720, 720);
			// night.setAlpha(time / 160);
			// night.draw(-775 + (player.x * 72),-760+ (player.y * 72),4);

			double lw = 720 / lrx;
			for (double i = 0; i < lrx; i++) {
				for (double j = 0; j < lry; j++) {
					g.setColor(new Color(0, 0, 0, lighting[(int) i][(int) j]));
					g.fillRect((float) (i * lw), (float) (j * lw), (float) lw, (float) lw);
				}
			}

			for (DamageNumber dn : damagenumbers) {
				font.drawString((float) dn.x, (float) dn.y, dn.text, dn.color);
			}

			bg.draw(720, 0);

			font.drawString(770, 5, "Starlink");
			int hbx = 760;
			int hby = 27;
			g.setColor(new Color(50, 50, 50));
			g.fillRect(hbx + 3, hby + 3, (float) (279), 27);
			g.setColor(new Color(255, 184, 43));
			g.fillRect(hbx + 3, hby + 3, (float) (279 * ((double) player.health / player.maxhealth)), 27);
			if (player.losthealth < 0) {
				g.setColor(new Color(48, 163, 60));
			} else {
				g.setColor(new Color(255, 48, 48));
			}
			g.fillRect(hbx + 3 + ((float) (279 * ((double) player.health / player.maxhealth))), hby + 3,
					(float) (279 * ((double) player.losthealth / player.maxhealth)), 27);

			healthbar.draw(hbx, hby, 3);
			font.drawString(hbx + 130, hby + 5, "" + (int) player.health);
			int mbx = 768, mby = 70;
			for (int i = 0; i < 27; i++) {
				nomana.draw(mbx + 10 * i, mby);
			}
			for (int i = 0; i < player.mana; i++) {
				mana.draw(mbx + 10 * i, mby);
			}

			hunger.draw(760, 150, 40, 40);
			g.setColor(new Color(50, 50, 50));
			g.fillRect(805, 170, (float) (225 * (1)), 10);
			g.setColor(new Color(124, 64, 23));
			if (player.hunger > 0) {
				g.fillRect(805, 170, (float) (225 * (player.hunger / 100)), 10);
			}
			g.setColor(Color.gray);
			g.fillRect(805, 700, (float) (225), 10);
			g.setColor(Color.white);
			g.fillRect(805, 700, (float) (225.0 * (player.getWeight() / player.carryweight)), 10);
			if (player.fuel > 0) {
				fuel.draw(20, 630, 60, 60);
				g.setColor(new Color(50, 50, 50));
				g.fillRect(90, 650, (float) (150), 15);
				g.setColor(new Color(255, 224, 71));
				g.fillRect(90, 650, (float) player.fuel, 15);

			}

			if (player.page == 0) {
				font.drawString(895 - (font.getWidth("Items") / 2), 235, "Items");
				int inc = 0;
				for (Item it : player.inventory) {
					box.draw(759, 277 + (inc * 50));
					if (it.quantity > 1) {
						font.drawString(770, 290 + (inc * 50), it.name + " x" + Integer.toString(it.quantity));
					} else {
						font.drawString(770, 290 + (inc * 50), it.name);
					}
					it.img.draw(996, 282 + (inc * 50), 40, 40);
					inc++;
				}
			}
			if (player.page == 1) {
				font.drawString(895 - (font.getWidth("Items") / 2), 235, "Weapons");
				int inc = 0;
				for (Item it : player.weapons) {
					box.draw(759, 277 + (inc * 50));
					if (it.quantity > 1) {
						font.drawString(770, 290 + (inc * 50), it.name + " x" + Integer.toString(it.quantity));
					} else {
						font.drawString(770, 290 + (inc * 50), it.name);
					}
					if (player.weapon == inc) {
						font.drawString(960, 290 + (inc * 50), "E", Color.yellow);
					}
					it.img.draw(996, 282 + (inc * 50), 40, 40);
					inc++;
				}
			}
			if (player.page == 2) {
				font.drawString(895 - (font.getWidth("Spells") / 2), 235, "Spells");
				int inc = 0;
				for (Item it : player.spells) {
					box.draw(759, 277 + (inc * 50));
					font.drawString(770, 290 + (inc * 50), it.name);
					it.img.draw(996, 282 + (inc * 50), 40, 40);
					inc++;
				}
			}
			font.drawString(650 - (font.getWidth("$" + money) / 2), 680, "$" + money, Color.yellow);
			font.drawString(340 - (font.getWidth(world[wx][wy][depth].name) / 2), 680,
					"" + world[wx][wy][depth].name + wx + wy + depth);
		} else if (state.equalsIgnoreCase("upgrade")) {
			int inc = 0;
			for (Perk p : perkshop) {
				perk.draw(perkshopx, perkshopy + (inc * 50));
				font.drawString(perkshopx + 20, perkshopy + 6 + (inc * 50), p.name);
				if (p.locked) {
					font.drawString(perkshopx + 200, perkshopy + 6 + (inc * 50), "$" + p.cost, Color.yellow);
				}

				inc++;
			}
			for(int y = 0; y < perklimit; y++) {
				perk.draw(perksx, perksy + (y * 50));
				g.setColor(new Color(0,0,0,150));
				g.fillRect(perksx, perksy + (y * 50), perk.getWidth(), perk.getHeight());
				font.drawString(perksx + 20, perksy + 6 + (y * 50), "    -empty-");
			}
			inc = 0;
			for (Perk p : perks) {
				perk.draw(perksx, perksy + (inc * 50));
				font.drawString(perksx + 20, perksy + 6 + (inc * 50), p.name);
				inc++;
			}
			font.drawString(650 - (font.getWidth("$" + money) / 2), 680, "$" + money, Color.yellow);
			font.drawString(830 - (font.getWidth("Buy perk slot: $" + slotcost) / 2), 600, "Buy perk slot: $" + slotcost, Color.yellow);
		} else if (state.equalsIgnoreCase("dead")) {
			font.drawString(640 - (font.getWidth("you died") / 2), 360, "you died", Color.red);
		} else if (state.equalsIgnoreCase("load")) {
			font.drawString(640 - (font.getWidth("Loading...") / 2), 360, "Loading...", Color.white);
		}

	}

	public void mouseWheelMoved(int change) {

		if (change > 1) {

		}
		if (change < 1) {

		}

	}

	public static boolean checkSolid(int x, int y, boolean pl) throws SlickException {
		if (x == player.x && y == player.y) {
			return true;
		}

		for (Enemy e : world[wx][wy][depth].enemies) {
			if (e.x == x && e.y == y) {
				if (pl)
					player.attack(e);
				return true;
			}
		}

		for (Item it : world[wx][wy][depth].items) {
			if (it.x == x && it.y == y) {
				if (pl) {
					player.addItem(it);
					world[wx][wy][depth].items.remove(it);
				}

				return world[wx][wy][depth].tiles[it.x][it.y].solid;
			}
		}
		if (pl && world[wx][wy][depth].tiles[x][y].tree) {
			if (player.weapons.size() > 0 && player.weapons.get(player.weapon).treecut) {
				world[wx][wy][depth].tiles[x][y].health -= 2;
				if (rand.nextInt(5) == 0) {
					player.addItem(new Item("stick", 0, 0));
				}
				chop.play();
				player.passTime(true);
				if (world[wx][wy][depth].tiles[x][y].health <= 0) {
					world[wx][wy][depth].tiles[x][y] = new Tile("log");
				}
			}
			return true;
		}
		if (pl && world[wx][wy][depth].tiles[x][y].name.equalsIgnoreCase("log")) {
			if (player.fuel > 0) {
				Item.torch.play();
				world[wx][wy][depth].tiles[x][y] = new Tile("fire");
				world[wx][wy][depth].lights.add(new Light(72 * x + 31, 72 * y + 31, 200, "fire"));
			}
			return true;
		} else if (pl && world[wx][wy][depth].tiles[x][y].name.equalsIgnoreCase("fire")) {
			player.restore(4);
			player.passTime(true);
			for (Item it : player.inventory) {
				if (it.name.equalsIgnoreCase("stick")) {
					player.addItem(new Item("torch", 0, 0), it.quantity);
					it.quantity = 0;
					break;
				}
				if (it.name.equalsIgnoreCase("raw meat")) {
					player.addItem(new Item("cooked meat", 0, 0), it.quantity);
					it.quantity = 0;
					break;
				}
			}
			return true;
		} else if (pl && world[wx][wy][depth].tiles[x][y].name.equalsIgnoreCase("chest")) {
			player.passTime(true);
			Tile.chest();
			world[wx][wy][depth].tiles[x][y] = new Tile("openchest");
			return true;
		}
		return world[wx][wy][depth].tiles[x][y].solid;
	}

	public static void updateLighting() {
		for (int i = 0; i < lrx; i++) {
			for (int j = 0; j < lry; j++) {
				lighting[i][j] = (int) (time / 160 * 255);
				if (lighting[i][j] > 200) {
					lighting[i][j] = 200;
				}
				if (depth > 0) {
					lighting[i][j] = 255;
				}
				double pxx = (720 / lrx) * i + ((720 / lrx) / 2);
				double pxy = (720 / lry) * j + ((720 / lry) / 2);
				for (Light lt : world[wx][wy][depth].lights) {
					if (getDistance(pxx, pxy, lt.x, lt.y) <= lt.range) {
						lighting[i][j] = (int) ((time / 160 * 255) / 2);
						if (depth > 0) {
							lighting[i][j] = (int) ((255) / 2);
						}
					}

				}
			}
		}
		for (int i = 0; i < lrx; i++) {
			for (int j = 0; j < lry; j++) {
				double pxx = (720 / lrx) * i + ((720 / lrx) / 2);
				double pxy = (720 / lry) * j + ((720 / lry) / 2);
				for (Light lt : world[wx][wy][depth].lights) {
					if (getDistance(pxx, pxy, lt.x, lt.y) <= lt.range / 1.5) {
						lighting[i][j] = (int) ((time / 160 * 255) / 4);
						if (depth > 0) {
							lighting[i][j] = (int) ((255) / 4);
						}
					}
				}
			}
		}
	}

	public static boolean hasPerk(String name) {
		for (Perk p : perks) {
			if (p.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}

	public static void main(String[] args) throws SlickException, LWJGLException {
		AppGameContainer app = new AppGameContainer(new ScalableGame(new Main(),resolutionx,resolutiony));
		
		if(fullscreen) {
			app.setDisplayMode(resolutionx, resolutiony, fullscreen);
		}else {
			app.setDisplayMode(1280, 720, fullscreen);
		}
		app.setTargetFrameRate(60);
		app.setMaximumLogicUpdateInterval(60);
		app.setShowFPS(false);
		DisplayMode[] modes = Display.getAvailableDisplayModes();

		for (int i = 0; i < modes.length; i++) {
			DisplayMode current = modes[i];
			System.out.println(current.getWidth() + "x" + current.getHeight() + "x" + current.getBitsPerPixel() + " "
					+ current.getFrequency() + "Hz");
		}

		app.start();
	}
}