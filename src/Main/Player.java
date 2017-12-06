package Main;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Player {
	Random rand = new Random();
	Sound hurt = new Sound("res/sounds/hurt.wav");
	Sound step = new Sound("res/sounds/step.wav");
	Sound eat = new Sound("res/sounds/eat.wav");
	Sound equip = new Sound("res/sounds/equip.wav");
	Sound block = new Sound("res/sounds/block.wav");
	public double maxhealth = 25, health = maxhealth;
	public int mana = 27, maxmana = 27;
	public double xp = 0, hunger = 100, carryweight = 25;
	public int x = 0, y = 0;
	public double losthealth = 0;
	public int regen = 5, rest = 0, weapon = 0;
	public int page = 1, maxpages = 3;
	public double fuel = 0;
	public int blockchance = 0;
	// PAGES ////////
	// 0 - Items
	// 1 - Weapons
	// 2 - Spells

	public ArrayList<Item> weapons = new ArrayList<Item>();
	public ArrayList<Item> inventory = new ArrayList<Item>();
	public ArrayList<Item> spells = new ArrayList<Item>();

	public Image img;

	public Player(int x, int y) throws SlickException {
		this.x = x;
		this.y = y;

		img = new Image("res/player.png");
		img.setFilter(Image.FILTER_NEAREST);

		if (Main.hasPerk("Health Up")) {
			maxhealth += 10;
			health = maxhealth;
		}
		if (Main.hasPerk("Block Chance")) {
			blockchance += 15;
		}
		if (Main.hasPerk("Carry Weight+")) {
			carryweight += 15;
		}

		addItem(new Item("axe", 0, 0));
		addItem(new Item("shortsword", 0, 0));
		addItem(new Item("torch", 0, 0), 2);
		
		addItem(new Item("scroll", 0, 0), 5);
		if(Main.hasPerk("Free Scroll")) {
			addItem(new Item("scroll", 0, 0), 1);
		}
		
		//spells.add(new Item("heal", 0, 0));
	}

	public void tick(Input in) throws SlickException {
		if (rest > 0) {
			rest--;
		}
		if (in.isKeyDown(Input.KEY_SPACE)) {
			if (rest <= 0) {
				rest = 5;
				passTime(true);
			}
		}
		if (in.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mx = in.getMouseX();
			int my = in.getMouseY();
			if (page == 0) {
				if (mx > 748 && mx < 1040 && my > 277) {
					int index = 0;
					index = (int) ((my - 277) / 50);
					if (index < inventory.size()) {
						inventory.get(index).use();
						Main.turn = 1;
					}

					for (Item i : inventory) {
						if (i.quantity <= 0) {
							inventory.remove(i);
							break;
						}
					}

				}
			}
			if (page == 1) {
				if (mx > 748 && mx < 1040 && my > 277) {
					int index = 0;
					index = (int) ((my - 277) / 50);
					if (index < weapons.size()) {
						weapons.get(index).use();
						Main.turn = 1;
					}
				}
			}
			if (page == 2) {
				if (mx > 748 && mx < 1040 && my > 277) {
					int index = 0;
					index = (int) ((my - 277) / 50);
					if (index < spells.size()) {
						spells.get(index).use();
						Main.turn = 1;
					}
				}
			}

			if (mx > 760 && mx < 820 && my > 224 && my < 262) {
				page++;
				if (page == maxpages) {
					page = 0;
				}
			}

			if (mx > 983 && mx < 1040 && my > 224 && my < 262) {
				page--;
				if (page < 0) {
					page = maxpages - 1;
				}
			}

		}

		if (fuel > 150) {
			fuel = 150;
		}

		if (in.isKeyPressed(Input.KEY_W)) {
			if (y == 0) {
				y = 9;
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.wy--;
			} else if (!Main.checkSolid(x, y - 1, true)) {
				y--;
				move();
			}
		}
		if (in.isKeyPressed(Input.KEY_S)) {
			if (y == 9) {
				y = 0;
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.wy++;
				

			} else if (!Main.checkSolid(x, y + 1, true)) {
				y++;
				move();
			}
		}
		if (in.isKeyPressed(Input.KEY_A)) {
			if (x == 0) {
				x = 9;
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.wx--;
				
			} else if (!Main.checkSolid(x - 1, y, true)) {
				x--;
				move();
			}
		}
		if (in.isKeyPressed(Input.KEY_D)) {
			if (x == 9) {
				x = 0;
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.wx++;
				
			} else if (!Main.checkSolid(x + 1, y, true)) {
				x++;
				move();
			}
		}
		if (in.isKeyPressed(Input.KEY_ENTER)) {
			if (Main.world[Main.wx][Main.wy][Main.depth].tiles[x][y].name.equalsIgnoreCase("cave")) {
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.depth++;
				Main.world[Main.wx][Main.wy][Main.depth].tiles[x][y] = new Tile("stairsup");
			} else if (Main.world[Main.wx][Main.wy][Main.depth].tiles[x][y].name.equalsIgnoreCase("stairsdown")) {
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.depth++;
				Main.world[Main.wx][Main.wy][Main.depth].tiles[x][y] = new Tile("stairsup");
			} else if (Main.world[Main.wx][Main.wy][Main.depth].tiles[x][y].name.equalsIgnoreCase("stairsup")) {
				Main.world[Main.wx][Main.wy][Main.depth].leave();
				Main.depth--;
			}
		}

	}

	public void move() {
		step.play();
		passTime(true);

	}

	public void passTime(boolean heal) {
		Main.turn = 1;
		if (hunger < 0) {
			heal((maxhealth / 50) * (hunger / 100));
			heal((maxhealth / 50) * (hunger / 100));
		}
		if (heal) {
			heal((maxhealth / 50) * (hunger / 100));
		}
		if (Main.delay <= 0) {
			if (Main.day) {
				Main.time++;
				if (Main.time == 160) {
					Main.day = false;
					Main.delay = 50;
				}
			} else {
				Main.time--;
				if (Main.time == 0) {
					Main.day = true;
					Main.delay = 50;
				}
			}
		} else {
			Main.delay--;
		}
		fuel--;
		if (Main.hasPerk("Better Torch")) {
			fuel += 0.4;
		}
		if (fuel < 0) {
			fuel = 0;
		}
		if (health <= 0) {
			Main.state = "dead";
		}
	}

	public void heal(double amount) {
		if (amount + health > maxhealth) {
			amount = maxhealth - health;
		}

		losthealth -= amount;
		health += amount;

		if (health > maxhealth) {
			health = maxhealth;
		}
	}

	public void restore(int amount) {
		if (amount + mana > maxmana) {
			amount = maxmana - mana;
		}
		mana += amount;
		if (mana > maxmana) {
			mana = maxmana;
		}
	}

	public void attack(Enemy e) throws SlickException {
		if (weapons.size() == 0) {
			e.damage(2);
			Main.turn = 1;
			passTime(false);
		} else {
			int calc = rand.nextInt((weapons.get(weapon).maxdamage - weapons.get(weapon).mindamage) + 1)
					+ weapons.get(weapon).mindamage;
			if (weapons.get(weapon).name.contains("sword") && Main.hasPerk("Sword Damage")) {
				calc++;
			}
			e.damage(calc);
			Main.turn = 1;
			passTime(false);
		}

	}

	public void damage(int amount) {
		if (rand.nextInt(100) < blockchance) {
			block.play();
			Main.damagenumbers.add(new DamageNumber(x * 72 + 36, y * 72 + (36), "blocked", Color.gray, 1));
		} else {
			losthealth += amount;
			health -= amount;
			hurt.play();
			// Main.damagenumbers.add(new DamageNumber(x * 72 + 36, y * 72 + (36),
			// Integer.toString(amount), Color.red));
			if (health <= 0) {
				Main.state = "dead";
			}
		}

	}

	public void addItem(Item it) throws SlickException {
		if(getWeight() + (it.weight) <= carryweight) {
			Main.damagenumbers.add(new DamageNumber(x * 72 + 36, y * 72 + (36), it.name, Color.white, 1));
			if (it.weapon) {

				weapons.add(it);
			} else {
				for (Item find : inventory) {
					if (it.name.equalsIgnoreCase(find.name) && find.quantity < find.stack) {
						find.quantity++;
						return;
					}
				}

				inventory.add(it);
			}
		}else {
			Main.world[Main.wx][Main.wy][Main.depth].items.add(new Item(it.name, x, y));
			Main.damagenumbers.add(new DamageNumber(x * 72 + 36, y * 72 + (36), "Inventory Full", Color.white, 1));
		}

	}

	public void addItem(Item it, int quant) throws SlickException {
		for (int i = 0; i < quant; i++) {
			addItem(new Item(it.name, 0, 0));
		}
	}

	public void feed(double amount) {
		if (hunger < 0) {
			hunger = 0;
		}
		hunger += amount;
		if (hunger > 100) {
			hunger = 100;
		}
		eat.play();

	}
	
	public double getWeight() {
		double wt = 0;
		for(Item it : inventory) {
			wt += it.weight * it.quantity;
		}
		for(Item it : weapons) {
			wt += it.weight;
		}
		return wt;
	}

}
