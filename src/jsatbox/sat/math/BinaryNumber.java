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

package jsatbox.sat.math;

import jsatbox.miscellaneous.VariablesContainer;

public class BinaryNumber {
	private String key;
	private int[] indices;
	private int free;
	private int svd_os;
	private int width;
	
	public BinaryNumber(String key, int[] indices, int free, int width, VariablesContainer variables) throws Exception{
		this.key = key;
		this.indices = indices;
		this.free = free;
		this.svd_os = 0;
		this.width = width;
		
		if (variables.containsKey(key))
			throw new Exception("key " + key + " already exists");
		
		variables.add(key, new int[]{width});
	}
	
	public BinaryNumber(String key, int[] indices, int free, int width){
		this.key = key;
		this.indices = indices;
		this.free = free;
		this.svd_os = 0;
		this.width = width;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public int[] getIndices(){
		return this.indices;
	}
	
	public int getFree(){
		return this.free;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getSvdOffset(){
		return this.svd_os;
	}

	public void setSvdOffset(int svd_os){
		this.svd_os = svd_os;
	}
	
	public int[] offset(int os){
		this.indices[this.free] = this.indices[this.free] - this.svd_os;
		this.indices[this.free] = this.indices[this.free] + os;
		this.svd_os = os;
		return this.indices;
	}
}
