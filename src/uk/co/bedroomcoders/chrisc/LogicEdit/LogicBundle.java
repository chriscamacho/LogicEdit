package uk.co.bedroomcoders.chrisc.LogicEdit;

import java.lang.reflect.Constructor;
import java.util.HashMap;

// convienience class to bundle together info about logic classes
// information is generated via reflection / class file scanning
// during start up

class LogicBundle implements Comparable<LogicBundle> {
	
	private static HashMap<String,LogicBundle> classes = new HashMap<String,LogicBundle>();
	private static HashMap< Class<? extends Logic>,String> names = new HashMap< Class<? extends Logic>,String>();
	String name;
	Constructor<?> constr; 
	
	LogicBundle(String Name, Constructor<?> Constr) {
		name=Name;
		constr=Constr;
		classes.put(name,this);
		Logic l=createInstance(null);
		names.put(l.getClass(),name);
	}
	
    // used to sort the list by name alphabetically
	@Override
    public int compareTo(LogicBundle other){
        int n = this.name.compareTo(other.name);
        return n == 0 ? this.name.compareTo(other.name) : n;
	}
	
	public String toString() { return name; }
	
	// creates a Logic instance from this bundle
	Logic createInstance(Panel dp) {
		Logic l = null;
		try {
			l=(Logic)constr.newInstance(dp);
		} catch (Exception e) {
			System.out.println("Cant create a Logic instance for "+name);
			e.printStackTrace();
		}
		return l;
	}
	
	// given the name of a class return a bundle which
	// can be used to create an instance.
	public static LogicBundle get(String Name) {
		return classes.get(Name);
	}
	
	public static String classToType(Class<? extends Logic> cls) {
		return names.get(cls);
	}
}
