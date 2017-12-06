package Main;

import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Item {
	static Random rand = new Random();
	public int quantity = 1, stack = 1;
	String name = "";
	Image img;
	int x, y, osx = 0, osy = 0;
	int mindamage = 0, maxdamage = 0;
	int index = 0;
	public double weight = 1;

	boolean consumable = true;
	boolean weapon = false;
	boolean treecut = false;
	boolean spell = false;
	int manacost = 0;
	

	static Sound torch;
	static Sound equip;

	public Item(String name, int x, int y) throws SlickException {
		this.name = name;
		this.x = x;
		this.y = y;

		torch = new Sound("res/sounds/torch.wav");
		equip = new Sound("res/sounds/equip.wav");

		img = new Image("res/items/" + name + ".png");
		img.setFilter(Image.FILTER_NEAREST);

		
		if (name.equalsIgnoreCase("apple")) {
			stack = 99;
			osx = 20;
			osy = -5;
			weight = 0.5;
		}
		if (name.equalsIgnoreCase("torch")) {
			stack = 99;
			weight = 1;
		}
		if (name.equalsIgnoreCase("scroll")) {
			stack = 99;
			weight = 0.2;
		}
		if (name.equalsIgnoreCase("stick")) {
			stack = 99;
			consumable = false;
			weight = 1;
		}
		if (name.equalsIgnoreCase("raw meat")) {
			stack = 99;
			consumable = false;
			weight = 1;
		}
		if (name.equalsIgnoreCase("cooked meat")) {
			stack = 99;
			weight = 1;
		}
		if (name.equalsIgnoreCase("potion")) {
			stack = 99;
			weight = 0.5;
		}
		if (name.equalsIgnoreCase("axe")) {
			stack = 1;
			consumable = false;
			weapon = true;
			mindamage = 2;
			maxdamage = 3;
			treecut = true;
			weight = 5;
		}
		if (name.equalsIgnoreCase("sword")) {
			stack = 1;
			consumable = false;
			weapon = true;
			mindamage = 4;
			maxdamage = 6;
			weight = 5;
		}
		if (name.equalsIgnoreCase("shortsword")) {
			stack = 1;
			consumable = false;
			weapon = true;
			mindamage = 3;
			maxdamage = 4;
			weight = 3;
		}
		///////////
		if (name.equalsIgnoreCase("heal")) {
			stack = 10;
			consumable = false;
			spell = true;
			manacost = 8;
		}
		if (name.equalsIgnoreCase("return")) {
			stack = 10;
			consumable = false;
			spell = true;
			manacost = 8;
		}
	}

	public void tick(Input in) {

	}

	public void use() throws SlickException {
		if (consumable) {
			quantity--;
		}
		if (weapon) {
			if (Main.player.weapon != index) {
				Main.player.weapon = index;
				equip.play();
			}

		}
		if (name.equalsIgnoreCase("apple")) {
			Main.player.heal(1);
			Main.player.feed(6);

		}
		
		if (name.equalsIgnoreCase("cooked meat")) {
			Main.player.heal(2);
			Main.player.feed(35);

		}
		if (name.equalsIgnoreCase("potion")) {
			Main.player.heal(Main.player.maxhealth);

		}
		if (name.equalsIgnoreCase("torch")) {
			Main.player.fuel += 50;
			torch.play();
		}
		if (name.equalsIgnoreCase("scroll")) {
			Main.player.spells.add(Item.randomSpell());
			torch.play();
		}
		
		
		if (name.equalsIgnoreCase("heal") && Main.player.mana >= manacost) {
			Main.player.heal(10);
			Main.player.mana -= manacost;

		}
		if (name.equalsIgnoreCase("return") && Main.player.mana >= manacost) {
			Main.depth = 0;
			Main.player.mana -= manacost;
			torch.play();

		}
	}

	private static Item randomSpell() throws SlickException {
		int rs = rand.nextInt(2);
		switch(rs) {
		case 0:
			return new Item("heal",0,0);
		case 1:
			return new Item("return",0,0);
		}
		
		return null;
	}

}
