package Main;

public class Perk {
	public int cost = 0;
	public String name = "";
	public boolean locked = true;
	public Perk(String name) {
		this.name = name;
		if(name.equalsIgnoreCase("Sword Damage")) {
			cost = 50;
		}
		if(name.equalsIgnoreCase("Better Torch")) {
			cost = 25;
		}
		if(name.equalsIgnoreCase("Health Up")) {
			cost = 100;
		}
		if(name.equalsIgnoreCase("Block Chance")) {
			cost = 40;
		}
		if(name.equalsIgnoreCase("Metabolism")) {
			cost = 60;
		}
		if(name.equalsIgnoreCase("Free Scroll")) {
			cost = 50;
		}
		if(name.equalsIgnoreCase("Carry Weight+")) {
			cost = 60;
		}
		
	}
	public static void populate() {
		// TODO Auto-generated method stub
		Main.perkshop.add(new Perk("Sword Damage"));
		Main.perkshop.add(new Perk("Better Torch"));
		Main.perkshop.add(new Perk("Health Up"));
		Main.perkshop.add(new Perk("Block Chance"));
		Main.perkshop.add(new Perk("Metabolism"));
		Main.perkshop.add(new Perk("Free Scroll"));
		Main.perkshop.add(new Perk("Carry Weight+"));
		
		//Main.perkshop.add(new Perk("Metabolism"));
	}
}
