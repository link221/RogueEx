package Main;

public class Light {
	public int x, y;

	String parent = "";
	double range = 100, ogrange = range;
	boolean glow = true;
	double life = 0;

	public Light(int x, int y, double range, String parent) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.range = range;
		this.ogrange = range;

	}

	public void tick() {
		life += 0.1;
		if (parent.equalsIgnoreCase("player")) {
			range = 90;
			ogrange = 50;
			glow = false;
			x = Main.player.x * 72 + (72 / 2);
			y = Main.player.y * 72 + (72 / 2);
			if(Main.player.fuel > 0) {
				glow = true;
				range = 150 + Main.player.fuel;
				ogrange = 150 + Main.player.fuel;
			}
		}
		if(glow) {
			range = ogrange + (Math.cos(life) * 5);
		}
		
	}
}
