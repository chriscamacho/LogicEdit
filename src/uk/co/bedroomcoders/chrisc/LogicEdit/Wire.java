package uk.co.bedroomcoders.chrisc.LogicEdit;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;

import javafx.beans.value.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;

// this handles the graphical output and mouse input of a connection
// between an Output and an Input 

// once this has been finished with remove(Wire) MUST be called

class Wire {
		
	public static ArrayList<Wire> wires = new ArrayList<Wire>();
	
	protected int id;
	protected static int nextId=0;
	
	public Output from;
	public Input to;
	
	NumberBinding startx;
	NumberBinding starty;
	NumberBinding endx;
	NumberBinding endy;
	
	private CubicCurve graphic;

	Wire(Output From, Input To) {

		nextId++;
		id=nextId;
	
		from=From;
		to=To;
		from.connectedTo=this;
		to.connectedTo=this;
		wires.add(this);
		graphic = new CubicCurve();
		
		//startx = Bindings.add(from.layoutXProperty(),from.getParent().translateXProperty());
		//starty = Bindings.add(from.layoutYProperty(),from.getParent().translateYProperty());
		startx = Bindings.add(from.translateXProperty(),from.getParent().translateXProperty());
		starty = Bindings.add(from.translateYProperty(),from.getParent().translateYProperty());
		//endx = Bindings.add(to.layoutXProperty(),to.getParent().translateXProperty());
		//endy = Bindings.add(to.layoutYProperty(),to.getParent().translateYProperty());
		endx = Bindings.add(to.translateXProperty(),to.getParent().translateXProperty());
		endy = Bindings.add(to.translateYProperty(),to.getParent().translateYProperty());
		
		from.getParent().translateXProperty().addListener((ov,oldv,newv)->changed(ov,oldv,newv));
		from.getParent().translateYProperty().addListener((ov,oldv,newv)->changed(ov,oldv,newv));
		to.getParent().translateXProperty().addListener((ov,oldv,newv)->changed(ov,oldv,newv));
		to.getParent().translateYProperty().addListener((ov,oldv,newv)->changed(ov,oldv,newv));
		
		graphic.startXProperty().bind(startx);
		graphic.startYProperty().bind(starty);
		graphic.endXProperty().bind(endx);
		graphic.endYProperty().bind(endy);
		
		// set initial position of control points
		changed(from.getParent().translateXProperty(),from.getParent().getTranslateX(),from.getParent().getTranslateX());

		//graphic.setStroke(Color.DARKGREEN);
		graphic.setStroke(RandColour.nextColour());
		graphic.setStrokeWidth(3);
		graphic.setStrokeLineCap(StrokeLineCap.ROUND);
		
		graphic.setFill(null);
		graphic.toFront();

		Main.mainPane.getChildren().add(graphic);
		
	}

	//TODO panel rotation not accounted for...
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		// TODO possibly finesse this at a later date..
		
		//Point2D p=from.getLocalToSceneTransform().transform(0,0);
		//graphic.setStartX(p.getX()/Panel.Scale.getValue());
		//graphic.setStartY(p.getY()/Panel.Scale.getValue());
		
		if ((double)startx.getValue()<(double)endx.getValue()) {
			graphic.setControlX1((double)endx.getValue());
			graphic.setControlY1((double)starty.getValue());
			graphic.setControlX2((double)startx.getValue());
			graphic.setControlY2((double)endy.getValue());
		} else {
			if ((double)starty.getValue()<(double)endy.getValue()) {
				graphic.setControlX1((double)startx.getValue()+((double)startx.getValue()-(double)endx.getValue()));
				graphic.setControlY1((double)starty.getValue()+160); // TODO replace constant values
				graphic.setControlX2((double)startx.getValue()-((double)startx.getValue()-(double)endx.getValue())*2);
				graphic.setControlY2((double)endy.getValue()-160);
			} else {
				graphic.setControlX1((double)startx.getValue()+((double)startx.getValue()-(double)endx.getValue()));
				graphic.setControlY1((double)starty.getValue()-160);
				graphic.setControlX2((double)startx.getValue()-((double)startx.getValue()-(double)endx.getValue())*2);
				graphic.setControlY2((double)endy.getValue()+160);
			}	
		}
		
	}

	
	public static void remove(Wire w) {
		
		if (w.from!=null) w.from.connectedTo=null;
		if (w.to!=null) w.to.connectedTo=null;
		wires.remove(w);
		Main.mainPane.getChildren().remove(w.graphic);
		w.graphic.startXProperty().unbind();
		w.graphic.startYProperty().unbind();
		w.graphic.endXProperty().unbind();
		w.graphic.endYProperty().unbind();
		w.to.getParent().translateXProperty().removeListener((ov,oldv,newv)->w.changed(ov,oldv,newv));
		w.to.getParent().translateYProperty().removeListener((ov,oldv,newv)->w.changed(ov,oldv,newv));
		w.from.getParent().translateXProperty().removeListener((ov,oldv,newv)->w.changed(ov,oldv,newv));
		w.from.getParent().translateYProperty().removeListener((ov,oldv,newv)->w.changed(ov,oldv,newv));

		w.startx.dispose();
		w.starty.dispose();
		w.endx.dispose();
		w.endy.dispose();

	}

	protected String toXml() {
		String x="";
		x="<Wire id=\"Wire"+id+"\" from=\""+from.getId()+"\" to=\""+to.getId()+"\" />";
		return x;
	}
}
