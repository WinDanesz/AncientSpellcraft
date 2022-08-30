package com.windanesz.ancientspellcraft.entity.construct;

public class Solution {

	double radius, x, y;
	public Solution(double radius, double x, double y) {
		this.radius = radius;
		this.x = x;
		this.y = y;
	}

	public double[] randPoint() {
		double ang = Math.random() * 2 * Math.PI,
				hyp = Math.sqrt(Math.random()) * radius,
				adj = Math.cos(ang) * hyp,
				opp = Math.sin(ang) * hyp;
		return new double[]{x + adj, y + opp};
	}
}