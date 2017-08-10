package com.github.steevedroz.genetichedgehog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Hedgehog {
    private static final Random RANDOM = new Random();
    private static final int EDGE_COUNT = 100;
    private static final double EDGE_LENGTH = 2.0;
    private static final double MUTATION = 0.01;

    private List<Double> angles;
    private Color color;

    private Polygon cachedPolygon;

    public Hedgehog() {
	angles = new ArrayList<Double>();
	for (int i = 0; i < EDGE_COUNT; i++) {
	    angles.add(generateAngle());
	}
	color = generateColor();
    }

    public static double maxArea() {
	double length = EDGE_COUNT * EDGE_LENGTH;
	return length * length / Math.PI / 2;
    }

    public Hedgehog breed(Hedgehog mate) {
	Hedgehog hedgehog = new Hedgehog();
	hedgehog.angles = new ArrayList<Double>();
	for (int i = 0; i < EDGE_COUNT; i++) {
	    if (RANDOM.nextDouble() < MUTATION) {
		hedgehog.angles.add(generateAngle());
		continue;
	    }

	    if (RANDOM.nextBoolean()) {
		hedgehog.angles.add(this.angles.get(i));
	    } else {
		hedgehog.angles.add(mate.angles.get(i));
	    }
	}

	if (RANDOM.nextDouble() < MUTATION) {
	    hedgehog.color = generateColor();
	} else {
	    color = this.color.interpolate(mate.color, RANDOM.nextDouble());
	}

	return hedgehog;
    }

    public int attack(Hedgehog other) {
	double thisLife = this.getArea();
	double otherLife = other.getArea();
	for (int i = 0; i < EDGE_COUNT; i++) {
	    thisLife -= 3 * EDGE_LENGTH * Math.abs(other.angles.get(i));
	    otherLife -= 3 * EDGE_LENGTH * Math.abs(this.angles.get(i));
	}
	if (thisLife > otherLife)
	    return 1;
	if (thisLife < otherLife)
	    return -1;
	return 0;
    }

    public Polygon getPolygon(double x, double y) {
	if (cachedPolygon == null || cachedPolygon.getPoints().get(0) != x || cachedPolygon.getPoints().get(1) != y) {
	    double currentAngle = 0;
	    Polygon polygon = new Polygon();
	    polygon.setFill(color);
	    for (Double angle : angles) {
		polygon.getPoints().addAll(x, y);
		currentAngle += angle;
		x += EDGE_LENGTH * Math.cos(currentAngle);
		y += EDGE_LENGTH * Math.sin(currentAngle);
	    }
	    polygon.getPoints().addAll(x, y);

	    Point2D start = new Point2D(polygon.getPoints().get(0), polygon.getPoints().get(1));
	    Point2D horizontal = start.add(1, 0);
	    Point2D end = new Point2D(x, y);
	    double finalAngle = start.angle(horizontal, end);
	    polygon.setRotate(180-finalAngle);
	    cachedPolygon = polygon;
	}
	return cachedPolygon;
    }

    public double getArea() {
	double area = 0;
	double previousX = getPolygon().getPoints().get(0);
	double previousY = getPolygon().getPoints().get(1);
	for (int i = 2; i < getPolygon().getPoints().size(); i += 2) {
	    double currentX = getPolygon().getPoints().get(i);
	    double currentY = getPolygon().getPoints().get(i + 1);
	    if (currentY == previousY) {
		continue;
	    }
	    area += (currentX + previousX) / 2.0 * (currentY - previousY);
	    previousX = currentX;
	    previousY = currentY;
	}
	area += (getPolygon().getPoints().get(0) + previousX) / 2.0 * (getPolygon().getPoints().get(1) - previousY);

	return Math.abs(area);
    }

    @Override
    public String toString() {
	return "" + getArea();
    }

    private double generateAngle() {
	return RANDOM.nextDouble() * 2 * Math.PI - Math.PI;
    }

    private Color generateColor() {
	return new Color(RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble(), 1);
    }

    private Polygon getPolygon() {
	if (cachedPolygon == null) {
	    return getPolygon(0, 0);
	}
	return cachedPolygon;
    }
}
