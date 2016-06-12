/* JSATBox, Copyright (c) 2016 Jost Neigenfind <jostie@gmx.de>
 * Java SAT Toolbox
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package jsatbox.miscellaneous;
import java.util.*;

/**
 * 
 * @author Jost Neigenfind
 *
 * The IndexingVariables class. Container for integers representing Boolean variables.
 * Implements Cloneable for creating real clones of itself.
 */
public class VariablesContainer implements Cloneable{
	public HashMap<String, IndicesContainer> hm;	// the hash containing n dimensional arrays for indexing Boolean variables
	public HashMap<Integer, Integer> id;			// contains the integers of all variables as key and the corresponding enumeration of used boolean variables
	public HashMap<Integer, Integer> enumeration; 	// contains the enumeration of used variables as key and the corresponding integers of all variables 

	private int offset;								// position in Boolean variables

	/**
	 * Constructor.
	 */
	public VariablesContainer(){
		this.hm = new HashMap<String, IndicesContainer>();
		this.offset = 1;
		this.reset();
	}

	/**
	 * Reset object.
	 */
	public void reset(){
		this.id	= new HashMap<Integer, Integer>();
		this.enumeration = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Adds a new n dimensional array (in form of an ArrayStorage object).
	 * 
	 * @param key the name of object, for instance "haplotype"
	 * @param sizes the sizes of dimensions of the new n dimensional array
	 */
	public void add(String key, int[] sizes){
		int size = IndicesContainer.getSize(sizes);
		Integer[] variables = new Integer[size];
		for (int i = 0; i < size; i ++)
			variables[i] = new Integer(this.offset + i);
		this.offset = this.offset + size;
		
		IndicesContainer indices_container = new IndicesContainer(sizes, variables);
		this.hm.put(key, indices_container);
	}
	
	/**
	 * Returns the enumeration value given the id of the variable. 
	 * 
	 * @param ident The integer representing the id.
	 * @return The enumeration value. 
	 * @throws Exception
	 */
	public int getEnumeration(int ident) throws Exception{
		if (ident < 1)
			throw new Exception("Value has to be positive");
		
		return ((Integer)this.id.get(new Integer(ident))).intValue();
	}
	
	
	/**
	 * Returns the enumeration (used boolean variables) given the identifier (String). 
	 * 
	 * @param key indicates the kind of n dimensional array
	 * @param indices which position inside of this array, indices must have the size of n 
	 * @return integer of wanted Boolean variable
	 * @throws Exception
	 */
	public int getEnumeration(Object key, int[] indices) throws Exception{
		int ident = Math.abs(((Integer)((IndicesContainer)hm.get(key)).get(indices)).intValue());
		
		int size = 0;
		if (!this.id.containsKey(new Integer(ident))){
			size = this.id.size() + 1;
			this.id.put(new Integer(ident), new Integer(size));
			
			this.enumeration.put(new Integer(size), new Integer(ident));
		
			return size;
		}
		
		return ((Integer)this.id.get(new Integer(ident))).intValue();
	}

	/**
	 * Returns the id given the enumeration value.
	 * 
	 * @param enumerat The enumeration value.
	 * @return The id.
	 * @throws Exception
	 */
	public int getId(int enumerat) throws Exception{
		if (enumerat < 1)
			throw new Exception("Values has to be positive");
		
		return ((Integer)this.enumeration.get(new Integer(enumerat))).intValue();
	}
	
	/**
	 * Returns the evaluated id.
	 * 
	 * @param key
	 * @param indices
	 * @return
	 * @throws Exception
	 */
	public int getEvaluatedId(Object key, int[] indices) throws Exception{
		int variable = ((Integer)((IndicesContainer)hm.get(key)).get(indices)).intValue();
		return variable; 
	}
	
	/**
	 * Returns evaluated id as string.
	 * 
	 * @param key
	 * @param indices
	 * @return
	 * @throws Exception
	 */
	public String variableToString(Object key, int [] indices) throws Exception{
		if (this.getEvaluatedId(key, indices) > 0)
			return "1";
		else
			return("0");
	}
	
	/**
	 * Sets already existent Boolean variables. Needed for alternative haplotypes and
	 * genotypes.
	 * 
	 * @param key name of n dimensional array
	 * @param variables array of Objects (actually integers) that must be set
	 * @param sizes the sizes of dimensions of the new n dimensional array
	 */
	public void set(String key, Object[] variables, int[] sizes){
		IndicesContainer indices_container = new IndicesContainer(sizes, variables);
		hm.put(key, indices_container);
	}
	
	/**
	 * Set a whole IndexingVariables object.
	 * 
	 * @param indices_container the IndexingVariables object
	 * @param offset the position
	 */
	public void set(HashMap<String, IndicesContainer> indices_container, int offset){
		this.hm = indices_container;
		this.offset = offset;
	}
	
	/**
	 * Gets sizes of dimensions of the n dimensional array indicated by key.
	 * 
	 * @param key which array is wanted
	 * @return array containing sizes of dimensions
	 */
	public int[] getObjectSizes(String key){
		int[] dim = null;

		if (hm.containsKey(key))
			dim = ((IndicesContainer)hm.get(key)).getSizes();
		
		return dim;
	}
	
	public int getObjectSize(Object key){
		return ((IndicesContainer)this.hm.get(key)).size();
	}
	
	/**
	 * Returns the size of HashMap.
	 * 
	 * @return size of HashMap
	 */
	public int size(){
		return this.hm.size();
	}
		
	/**
	 * Returns number of Boolean varialbes.
	 * 
	 * @return number of Boolean varialbes
	 */
	public int numberOfVariables(){
		return this.offset - 1;
	}
	
	/**
	 * Sets parities to integers and is called in ReadCSVFile.
	 * 
	 * @param parities array containing parities
	 * @param outsider n dimensional arrays in HashMap that are not allowed to be set
	 * @throws Exception
	 */
	public void setParities(int[] parities, Object[] outsider) throws Exception{
		Set<String> s = hm.keySet();
		Iterator<String> it = s.iterator();
		
		while (it.hasNext()){
			Object key = it.next();
			
			boolean b = true;
			for (int i = 0; i < outsider.length; i++)
				if (key.equals(outsider[i]))
					b = false;
			
			if (b){
				IndicesContainer indices_container = (IndicesContainer)hm.get(key);
				Integer[] variables = (Integer[])indices_container.getArray();
				for (int i = 0;  i < variables.length; i++)
					if (parities[variables[i].intValue() - 1] < 0)
						indices_container.set(i, new Integer(-variables[i].intValue()));
			}
		}
	}

	public void show() throws Exception{
		Set<String> s = hm.keySet();
		Iterator<String> it = s.iterator();
		
		while (it.hasNext()){
			Object key = it.next();
			
			IndicesContainer indices_container = (IndicesContainer)hm.get(key);
			Object[] variables = (Object[])indices_container.getArray();
			for (int i = 0;  i < variables.length; i++)
				System.out.println(key + "\t" + variables[i] + "\t" + this.id.get(new Integer(Math.abs(((Integer)variables[i]).intValue()))));
		}
	}

	/**
	 * Creates a copy of itself.
	 * 
	 * @return the copy
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException{
		VariablesContainer ret = new VariablesContainer();
		
		HashMap<String, IndicesContainer> hm = new HashMap<String, IndicesContainer>();
		Iterator<String> string_iterator = this.hm.keySet().iterator();
		while (string_iterator.hasNext()){
			String key = string_iterator.next();
			hm.put(key, this.hm.get(key));
		}
		ret.set(hm, this.offset);

		HashMap<Integer, Integer> id = new HashMap<Integer, Integer>();
		Iterator<Integer> integer_iterator = this.id.keySet().iterator();
		while (integer_iterator.hasNext()){
			Integer key = integer_iterator.next();
			id.put(key, this.id.get(key));
		}
		ret.id = id;
		
		HashMap<Integer, Integer> enumeration = new HashMap<Integer, Integer>();
		integer_iterator = this.enumeration.keySet().iterator();
		while (integer_iterator.hasNext()){
			Integer key = integer_iterator.next();
			id.put(key, this.enumeration.get(key));
		}
		ret.enumeration = enumeration;
		
		return ret;
	}
	
	public int getEnumerationSize(){
		return this.enumeration.size();
	}
	
	public boolean containsKey(Object key){
		return this.hm.containsKey(key);
	}

	public int getIdSize(){
		return this.id.size();
	}
}
