package uk.co.bedroomcoders.chrisc.LogicEdit;

import java.util.ArrayList;
    
// any class extending Logic is a plugin loaded into the GUI at run time
// via reflection and is deemed a "plug in"

// you MUST have only one constructor accepting a Panel (its parent)
// the constructor MUST call super(parent)
// the constructor SHOULD set the number of inputs and output (numInputs & numOutputs)
// the constructor MUST call initialiseInterface 
// a plugin CAN have its own mouse event handlers but only on its own componets not parent

// a potential plug in author CAN, SHOULD and MUST read all the existing code for
// all classes extending the Logic class

    
abstract class Logic {
	
	protected int id;
	protected static int nextId=0;

	int numInputs=0;
	int numOutputs=0;
    Input[] inputs = null;
    Output[] outputs = null;
    
	Panel parent;
	
	protected Logic(Panel Parent) { 
		parent=Parent; 
		nextId++;
		id=nextId;
	} 
	
	// create a new set of inputs and outputs 
	// given the number of both has been set
	// also places them correctly on the panel
	protected void initialiseInterface() {
		
		inputs = new Input[numInputs];
		outputs = new Output[numOutputs];
		
		for (int i=0;i<numInputs;i++) {
			inputs[i]=new Input(Panel.defaultHeight,i,numInputs);
			parent.getChildren().add(inputs[i]);
		}

		for (int i=0;i<numOutputs;i++) {	
			outputs[i] = new Output(Panel.defaultWidth,Panel.defaultHeight,i,numOutputs);
			parent.getChildren().add(outputs[i]);
		}
		
		
	}
	
	abstract void process(StateEvent se);
	
	protected String toXml() {
		String x="";
		x="<Logic id=\"Logic"+id+"\" type=\""+LogicBundle.classToType(this.getClass())+"\" >";
		return x;
	}
}
