package Main;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Enemy {

	String name = "";
	public int health, maxhealth;
	public double losthealth = 0;
	public int x, y, strength, moves = 0, maxmoves = 0;
	Sound hurt = new Sound("res/sounds/hit.wav");
	public int reward = 1;

	public Image img;
	Random rand = new Random();

	public Enemy(String name, int x, int y) throws SlickException {
		this.name = name;
		this.x = x;
		this.y = y;

		img = new Image("res/enemies/" + name + ".png");
		img.setFilter(Image.FILTER_NEAREST);
		
		if(name.equalsIgnoreCase("mouse")) {
			maxhealth = 12;
			health = maxhealth;
			strength = 1;
		}
		if(name.equalsIgnoreCase("bear")) {
			maxhealth = 35;
			health = maxhealth;
			strength = 3;
			reward = 5;
		}
		if(name.equalsIgnoreCase("troll")) {
			maxhealth = 400;
			health = maxhealth;
			strength = 3;
			reward = 40;
		}
		if(name.equalsIgnoreCase("sprite")) {
			maxhealth = 30;
			health = maxhealth;
			strength = 4;
			reward = 5;
		}
		if(name.equalsIgnoreCase("cyclops")) {
			maxhealth = 50;
			health = maxhealth;
			strength = 3;
			reward = 10;
		}
		if(name.equalsIgnoreCase("skeleton")) {
			maxhealth = 15;
			health = maxhealth;
			strength = 1;
		}
		if(name.equalsIgnoreCase("armored skelly")) {
			maxhealth = 30;
			health = maxhealth;
			strength = 2;
			reward = 2;
		}
		if(name.equalsIgnoreCase("bat")) {
			maxhealth = 12;
			health = maxhealth;
			strength = 1;
			maxmoves = 1;
		}
		

	}

	public void turn() throws SlickException {
		if ((Math.abs(Main.player.x - x) <= 1) && Math.abs(Main.player.y - y) <= 1) {
			Main.player.damage(strength);

		} else {
			if (Main.player.x > x && !Main.checkSolid(x + 1, y, false)) {
				x++;

			} else if (Main.player.x < x && !Main.checkSolid(x - 1, y, false)) {
				x--;
			} else if (Main.player.y > y && !Main.checkSolid(x, y + 1, false)) {
				y++;
			} else if (Main.player.y < y && !Main.checkSolid(x, y - 1, false)) {
				y--;
			}
		}

	}
	
	public void drop() throws SlickException {
		Main.money += reward;
		if(name.equalsIgnoreCase("mouse")) {
			if(rand.nextInt(3) == 0) {
				Main.world[Main.wx][Main.wy][Main.depth].items.add(new Item("raw meat",x,y));
			}
		}
	}

	public void damage(int amount) throws SlickException {
		health -= amount;
		losthealth += amount;
		if(health <= 0) {
			drop();
		}
		Main.damagenumbers.add(new DamageNumber(x * 72 + 36, y * 72+ (36), Integer.toString(amount), Color.white, 0));
		hurt.play();
		
	}

}
