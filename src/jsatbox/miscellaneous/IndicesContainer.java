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

/**
 * 
 * @author Jost Neigenfind
 *
 * The IndexContainer class. Manages n dimensional arrays. These n dimensional arrays are
 * represented by one dimensional arrays. Lists of indices are recalculated to position
 * in one dimensional array. Implements Cloneable for creating a real copy of itself.
 * 
 */
public class IndicesContainer implements Cloneable{
	private int dimension;					// dimension of array
	private Object[] one_dimensional_array; // corresponding one dimensional array
	private int[] sizes;					// sizes of the n dimensions
	private int size;						// length of one dimensional array
	
	/**
	 * Constructor 1.
	 * 
	 * @param sizes the sizes of the n dimensions
	 */
	IndicesContainer(int[] sizes){
		this.sizes = sizes;
		this.dimension = sizes.length;
		this.size = IndicesContainer.getSize(sizes);
		this.one_dimensional_array = new Object[size];
	}

	/**
	 * Constructor 2.
	 * 
	 * @param sizes the siztes of the n dimensions
	 * @param one_dimensional_array an already existent one dimensional array
	 */
	IndicesContainer(int[] sizes, Object[] one_dimensional_array){
		this.sizes = sizes;
		dimension = sizes.length;
		size = IndicesContainer.getSize(sizes);
		this.one_dimensional_array = one_dimensional_array;
	}
	
	/**
	 * Sets a value at given position in n dimensional array.
	 * 
	 * @param indices list of indices representing position of value
	 * @param value the value to set
	 * @throws Exception
	 */
	public void set(int[] indices, Object value) throws Exception{
		int index = IndicesContainer.getIndex(sizes, indices);
		if (index < size)
			one_dimensional_array[index] = value;
		else
			throw new Exception("Index not in Array");			
	}

	/**
	 * Sets a value at a given position in one dimensional array.
	 * 
	 * @param index the position of value
	 * @param value the value
	 * @throws Exception
	 */
	public void set(int index, Object value) throws Exception{
		one_dimensional_array[index] = value;			
	}
	
	/**
	 * Gets the value at a given position from n dimensional array.
	 * 
	 * @param indices the position of value
	 * @return the value
	 * @throws Exception
	 */
	public Object get(int[] indices) throws Exception{
		int index = IndicesContainer.getIndex(sizes, indices);
		if (index < size)
			return one_dimensional_array[index];
		else 
			throw new Exception("Index not in Array");
	}
	
	public Object get(int index) throws Exception{
		if (index < this.size)
			return this.one_dimensional_array[index];
		else 
			throw new Exception("Index not in Array");
	}
	
	/**
	 * Gets the one dimensional array.
	 * 
	 * @return the one dimensional array
	 */
	public Object[] getArray(){
		return one_dimensional_array;
	}
	
	/**
	 * Gets the sizes of the n dimensions.
	 * 
	 * @return list of sizes
	 */
	public int[] getSizes(){
		return sizes;
	}
	
	/**
	 * Gets the number of dimensions.
	 * 
	 * @return the number of dimensions
	 */
	public int getDimension(){
		return dimension;
	}

	/**
	 * Creates a copy of itself.
	 * 
	 * @return the copy
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException{
		Object[] one_dimensional_array_ = new Object[this.one_dimensional_array.length];
		for (int i = 0; i < this.one_dimensional_array.length; i++)
			one_dimensional_array_[i] = this.one_dimensional_array[i];

		int[] sizes_ = new int[this.sizes.length];
		for (int i = 0; i < this.sizes.length; i++)
			sizes_[i] = sizes[i];
		
		IndicesContainer as = new IndicesContainer(sizes_, one_dimensional_array_);
		
		return as;
	}
	
	public int size(){
		return this.size;
	}
	
	/**
	 * Calculates one dimensional index from given sizes of n dimensions
	 * and list of n indices.
	 * 
	 * @param sizes the sizes of the n dimensions
	 * @param indices the n indices
	 * @return one dimensional index
	 */
	public static int getIndex(int[] sizes, int[] indices){
		int index = 0;
		for (int i = 0; i < indices.length; i++){
			int p = 1;
			for (int j = i + 1; j < sizes.length; j++)
				p = p*sizes[j];
			p = p*indices[i];
			index = index + p;
		}
		return index;
	}
	
	/**
	 * Calculates number of fields in n dimensional array. 
	 * 
	 * @param sizes the sizes of the n dimensions
	 * @return number of fields in n dimensional array
	 */
	public static int getSize(int[] sizes){
		int p = 1;
		for (int i = 0; i < sizes.length; i++)
			p = p*sizes[i];
		return p;
	}
}
