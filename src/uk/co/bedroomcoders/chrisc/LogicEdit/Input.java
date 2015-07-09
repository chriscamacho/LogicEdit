package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.effect.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.Control;
import javafx.scene.input.*;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;

// each component (usually) has input nodes - the graphics and input
// for these are handled by the Input class

class Input extends SVGPath {
	
	static int nextId=0;
	final DropShadow dropShadow = new DropShadow();
	public Wire connectedTo = null;
	public boolean state;     
	
	
	// the parent height, the input index and the maximum inputs on the parent
	Input(double h, int n, int max) {
		super();
		
		nextId++;
		setId("Input"+nextId);
		setContent("M 2.9579106,-3.7071381 7.8892,0.05120873 2.9579106,3.8589054 c -2.04766424,0 -6.1427536,-0.02 -6.1427536,-0.02 -2.847999,0 -5.006807,-1.6813985 -5.016104,-3.75989677 -0.0093,-2.07869823 2.191633,-3.73294673 5.020126,-3.74774673 z");

		int m = max+1;
		//setEffect(dropShadow);
		
		//setLayoutY((h/m)*(n+1));
		setTranslateY((h/m)*(n+1));

		setOnMouseEntered(e->mouseEntered(e));
		setOnMouseExited(e->mouseExited(e));
		setOnMouseClicked(e->mouseClicked(e));
		dropShadow.setColor(Color.color(1, 0, 0));
		dropShadow.setRadius(4);
		dropShadow.setHeight(16);
		dropShadow.setWidth(24);
		dropShadow.setSpread(0.75);

	}
	
	protected String toXml() {
		String x="";
		x="<Input id=\""+getId()+"\" state=\""+state+"\" ";
		//if (connectedTo!=null) x=x+"connectedTo=\"Wire"+connectedTo.id+"\" ";
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
			if (Main.lastOutputClicked!=null) {
				Input in = this;
				Output out = Main.lastOutputClicked;
				if (in.connectedTo!=null) {
					Wire.remove(in.connectedTo);
				}
				if (out.connectedTo!=null) {
					Wire.remove(out.connectedTo);
				}
				Wire w=new Wire(out,in);
				Main.lastOutputClicked=null;
			}
		}
	}
}
