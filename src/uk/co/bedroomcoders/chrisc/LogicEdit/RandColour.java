package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.paint.Color;
import java.util.Random;

class RandColour {
	
	static Random rand=new Random();
	
	private static Color[] cols = { // TODO add more exotic, interesting and contrasting colours
		Color.web("000"),	Color.web("00F"),	Color.web("0F0"),	Color.web("0FF"),
		Color.web("F00"),	Color.web("F0F"),	Color.web("FF0"),
		Color.web("88F"),	Color.web("8F8"),	Color.web("8FF"),
		Color.web("F88"),	Color.web("F8F")
	}; 
	
	static Color nextColour() {
		return cols[rand.nextInt(cols.length)];
	}
}
