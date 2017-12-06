package Main;

import java.util.Random;

import org.newdawn.slick.Color;

public class DamageNumber {
	Random rand = new Random();
	String text = "";
	int life = 80;
	double xv = 0, yv = 0;
	double x = 0, y = 0;
	Color color = Color.white;
	int behavior = 0;

	public DamageNumber(double x, double y, String text, Color color, int behavior) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.behavior = behavior;
		xv = rand.nextInt(5) - 2.5;
		yv = rand.nextInt(5) - 10;
	}

	public void tick() {
		if (behavior == 0) {
			yv += 0.5;
			if (life > 50) {
				x += xv;
				y += yv;
			}
		} else if (behavior == 1) {
			if (life > 50) {
				y -= 1;
			}
		}

		life--;

	}
}
