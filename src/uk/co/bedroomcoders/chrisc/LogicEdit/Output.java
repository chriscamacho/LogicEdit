package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.effect.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.Control;
import javafx.scene.input.*;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;

// nodes usually have outputs this class handles visual output and mouse input

class Output extends SVGPath {
	static int nextId=0;
	final DropShadow dropShadow = new DropShadow();
	public Wire connectedTo = null;
	
	// the parent, its index and the maximum inputs on the parent
	Output(double w, double h, int n, int max) {
		super();
		nextId++;
		setId("Output"+nextId);
		setContent("M 2.9579106,-3.7071381 7.8892,0.05120873 2.9579106,3.8589054 c -2.04766424,0 -6.1427536,-0.02 -6.1427536,-0.02 -2.847999,0 -5.006807,-1.6813985 -5.016104,-3.75989677 -0.0093,-2.07869823 2.191633,-3.73294673 5.020126,-3.74774673 z");

		int m = max+1;

		//setLayoutY((h/m)*(n+1));
		//setLayoutX(w);
		setTranslateY((h/m)*(n+1));
		setTranslateX(w);

		
		setOnMouseEntered(e->mouseEntered(e));
		setOnMouseExited(e->mouseExited(e));
		setOnMouseClicked(e->mouseClicked(e));

		dropShadow.setColor(Color.color(0, 0, 1));
		dropShadow.setRadius(4);
		dropShadow.setHeight(16);
		dropShadow.setWidth(24);
		dropShadow.setSpread(0.75);
				
	}
	
	protected String toXml() {
		String x="";
		x="<Output id=\""+getId()+"\" ";
		if (connectedTo!=null) x=x+"connectedTo=\""+connectedTo.to.getId()+"\" ";
		x=x+"/>";
		return x;
	}	
	
	public void mouseEntered(MouseEvent me) {
		setCursor(Cursor.HAND);
		setEffect(dropShadow);
		me.consume();
	}

    public void mouseExited(MouseEvent me) {
		setEffect(null);
		me.consume();
	}
	
	public void mouseClicked(MouseEvent me) {
		if (me.getButton()==MouseButton.PRIMARY) {
			if (Main.lastOutputClicked!=null && connectedTo!=null) {
				Wire.remove(connectedTo);
				Main.lastOutputClicked=null;
			} else {
				Main.lastOutputClicked=this;
			}
			me.consume();
		}
		
		
	}

}
