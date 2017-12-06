package Main;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Tile {
	public Image img;
	public String name = "";
	
	public boolean solid = false, tree = false;
	public double maxhealth = 1, health = maxhealth;
	static Random rand = new Random();
	static Sound chest;
	
	
	
	public Tile(String name) throws SlickException {
		this.name = name;
		chest = new Sound("res/sounds/chest.wav");
		img = new Image("res/tiles/"+name+".png");
		img.setFilter(Image.FILTER_NEAREST);
		
		if(name.equalsIgnoreCase("tree")) {
			solid = true;
			tree = true;
			maxhealth = 10;
			health = maxhealth;
			
		}
		if(name.equalsIgnoreCase("rock")) {
			solid = true;
			
		}
		if(name.equalsIgnoreCase("upwall")) {
			solid = true;
			
		}
		if(name.equalsIgnoreCase("sidewall")) {
			solid = true;
			
		}
		if(name.equalsIgnoreCase("tree2")) {
			solid = true;
			tree = true;
			maxhealth = 15;
			health = maxhealth;
			
		}
		if(name.equalsIgnoreCase("deadtree")) {
			solid = true;
			tree = true;
			maxhealth = 5;
			health = maxhealth;
			
		}
		if(name.equalsIgnoreCase("log")) {
			solid = true;
			
			
		}
		if(name.equalsIgnoreCase("fire")) {
			solid = true;
		}
		if(name.equalsIgnoreCase("chest")) {
			solid = true;
		}
		if(name.equalsIgnoreCase("openchest")) {
			
		}
		
	}
	
	public static void chest() throws SlickException {
		chest.play();
		int rng = rand.nextInt(4);
		switch(rng) {
		case 0:
			Main.money += rand.nextInt(11)+10;
			break;
		case 1:
			Main.player.addItem(new Item("potion",0,0),rand.nextInt(2)+1);
			
			break;
		case 2:
			Main.player.addItem(new Item("sword",0,0));
			break;
		case 3:
			Main.player.addItem(new Item("scroll",0,0));
			break;
		}
	}
}
