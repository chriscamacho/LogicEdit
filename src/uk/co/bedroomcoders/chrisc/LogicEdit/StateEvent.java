package uk.co.bedroomcoders.chrisc.LogicEdit;

import java.util.ArrayList;
import java.util.ListIterator;

// handles the pending state change event caused by a gates
// changing output

class StateEvent {
	
	public static ArrayList<StateEvent> events = new ArrayList<StateEvent>();
	
	long tick;
	Input target;		// which input of the logic
	Logic targetLogic;
	boolean newState;
	boolean processed;  // been handled marked for deletion 
	
	StateEvent(long Tick, Input Target, boolean NewState) {
		tick=Tick;
		target=Target;
		newState=NewState;
		processed=false;
		if (target!=null) {
			targetLogic=((Panel)target.getParent()).logic;
		}
		events.add(this);
	}
	
	public static void dumpEvents() {
		for (ListIterator<?> iter = events.listIterator(); iter.hasNext(); ) {
			StateEvent se = (StateEvent)iter.next();
			System.out.println("tick "+se.tick+" target "+se.target.getId()+" new state "+se.newState);
		}
		System.out.println("--------");
	}
	
	public boolean equals(StateEvent other) {
		if (tick!=other.tick) return false;
		if (target!=other.target) return false;
		if (newState!=other.newState) return false;
		return true;
	}
}

