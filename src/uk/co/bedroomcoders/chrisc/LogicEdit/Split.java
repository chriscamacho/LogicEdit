package uk.co.bedroomcoders.chrisc.LogicEdit;

// provides two outputs the same as the input without a gate delay

class Split extends Logic {

	// must have a public constructor providing parent to the
	// super class
	public Split(Panel Parent) { 
		super(Parent);
		if (Parent==null) return; // happens when making a dummy logic during discovery
	
		numInputs=1;
		numOutputs=2;
		
		initialiseInterface();
		
		if (parent.logicPane!=null) parent.logicPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");		
	} 
	
	void process(StateEvent se) {
		// splitter is like a wire so has no gate delay
		
		// the _Output_ is _connectedTo_ a _Wire_ which goes _to_ an _Input_ 
		if (outputs[0].connectedTo!=null)
			new StateEvent(Main.Tick, outputs[0].connectedTo.to, se.newState );
		if (outputs[1].connectedTo!=null)
			new StateEvent(Main.Tick, outputs[1].connectedTo.to, se.newState );
	}
}
