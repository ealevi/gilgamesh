package org.gilgamesh.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Memory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Set<Atom<?>> atoms = Collections.synchronizedSet(new HashSet<Atom<?>>());
	protected HashMap<Long, Fact> facts = new HashMap<Long, Fact>();
	
	
	
	public Collection<Atom<?>> getAtoms() {
		return Collections.unmodifiableCollection(atoms);
	}
	
	public int getTotalAtoms() {
		return atoms.size();
	}
	
	public boolean contains(Atom<?> atom) {
		return atoms.contains(atom);
	}
	
	public boolean remove(Atom<?> atom) {
		
		if((atom = load(atom)) == null)
			return false;

		HashSet<Long> removed = new HashSet<Long>();
		
		for(Fact fact : facts.values().toArray(new Fact[0])) {
			
			for(Atom<?> current : fact.atoms)
				if(current.equals(atom)) {
					removed.add(fact.time);
					facts.remove(fact.time);
					break;
				}
		}
		
		for(Atom<?> current : atoms.toArray(new Atom<?>[0]))
			for(long id : removed)
				current.getFacts().remove(id);
		
		atoms.remove(atom);
		
		return true;
	}

	public void link(Atom<?> atom, Fact fact) {

		Atom<?> current = load(atom);
		
		if(current == null)
			atoms.add(atom);
		else
			atom = current;

		atom.add(refresh(fact));		
	}
	
	private Fact refresh(Fact fact) {
		
		for(Fact current : facts.values())
			if(current.equals(fact))
				return new Fact(fact.force, current.time, fact.atoms);
		
		facts.put(fact.time, fact);
		return fact;
	}
	
	public Atom<?> load(Atom<?> atom) {

		for(Atom<?> current : atoms)
			if(current.equals(atom))
				return current;
		
		return null;
	}

	public Collection<Fact> getFacts() {
		
		return facts.values();
	}
	
	public Set<Fact> getFacts(Atom<?> atom) {
		
		HashSet<Fact> set = new HashSet<Fact>();
		
		Atom<?> current = load(atom);
		
		if(current == null)
			return set;
		
		for(long id : current.getFacts().keySet())
			set.add(facts.get(id));
		
		return Collections.unmodifiableSet(set);
	}
	

}































