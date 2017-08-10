package com.github.steevedroz.genetichedgehog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

public class GeneticHedgehog extends Pane {
    private static final Random RANDOM = new Random();
    private static final int GENERATION_SIZE = 16;
    private static final int ELITE = 8;

    private boolean timerOn;
    private boolean displayOn;
    private int generation;

    private List<Hedgehog> hedgehogs;
    private List<Hedgehog> parents;
    private Hedgehog best;

    public GeneticHedgehog() {
	timerOn = false;
	displayOn = true;
	hedgehogs = new ArrayList<Hedgehog>();
	for (int i = 0; i < GENERATION_SIZE; i++) {
	    hedgehogs.add(new Hedgehog());
	}
	best = new Hedgehog();
	drawHedgehogs();
	AnimationTimer timer = new AnimationTimer() {

	    @Override
	    public void handle(long now) {
		breed();
		if (displayOn) {
		    drawHedgehogs();
		}
	    }
	};
	setOnMouseClicked(event -> {
	    switch (event.getButton()) {
	    case PRIMARY:
		timerOn ^= true;
		if (timerOn) {
		    timer.start();
		} else {
		    timer.stop();
		}
		break;

	    case SECONDARY:
		displayOn ^= true;
		if (displayOn) {
		    drawHedgehogs();
		} else {
		    getChildren().clear();
		    Label message = new Label("Right click to display hedgehogs");
		    message.setMinWidth(this.getWidth());
		    message.setTextAlignment(TextAlignment.CENTER);
		    message.setContentDisplay(ContentDisplay.CENTER);
		    getChildren().add(message);
		}
		break;

	    default:
	    }
	});

	setFocusTraversable(true);
	setOnKeyReleased(event -> {
	    switch (event.getCode()) {
	    case N:
		breed();
		drawHedgehogs();
		break;

	    default:
	    }
	});
    }

    public void breed() {
	generation++;
	parents = new ArrayList<Hedgehog>();
	parents.addAll(hedgehogs);
	parents.sort((a, b) -> a.attack(b));
	for (int i = 0; i < ELITE; i++) {
	    parents.set(i, parents.get(parents.size() - i - 1));
	}

	hedgehogs = new ArrayList<Hedgehog>();
	boolean bestFoundThisGeneration = false;
	for (int i = 0; i < GENERATION_SIZE - 1; i++) {
	    int index1 = RANDOM.nextInt(parents.size());
	    int index2;
	    do {
		index2 = RANDOM.nextInt(parents.size());
	    } while (index1 == index2);
	    Hedgehog hedgehog = parents.get(index1).breed(parents.get(index2));
	    hedgehogs.add(hedgehog);
	    if (hedgehog.attack(best) > 0) {
		best = hedgehog;
		bestFoundThisGeneration = true;
	    }
	}
	if (!bestFoundThisGeneration) {
	    hedgehogs.add(best);
	}

	hedgehogs.sort((a, b) -> b.attack(a));
	try {
	    hedgehogs.remove(GENERATION_SIZE);
	} catch (IndexOutOfBoundsException exception) {
	}
    }

    private void drawHedgehogs() {
	getChildren().clear();
	for (int i = 0; i < hedgehogs.size(); i++) {
	    int x = i % 4;
	    int y = i / 4;
	    getChildren().add(hedgehogs.get(i).getPolygon(x * 200 + 100, y * 200 + 100));
	}
	getChildren().add(new Label(String.format("Generation %1$d", generation)));
    }
}
