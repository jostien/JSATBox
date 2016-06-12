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

public class Equivalence {
	private VariablesContainer variables;
	private BinaryNumber left;
	private BinaryNumber right;
	private StringBuffer constraints;
	
	public Equivalence(VariablesContainer variables){
		this.variables = variables;
		this.left = null;
		this.right = null;
		this.constraints = new StringBuffer();
	}
	
	public void setLeftSide(BinaryNumber left){
		this.left = left;
	}
	
	public void setRightSide(BinaryNumber right){
		this.right = right;
	}

	public String getConstraints() throws Exception{
		if (left.getWidth() != right.getWidth())
			throw new Exception("two summands have not same width");
		
		this.constraints.delete(0, this.constraints.length());

		int width = left.getWidth();
		for (int i = 0; i < width; i++){
			this.constraints.append(-this.variables.getEnumeration(this.left.getKey(), this.left.offset(i)) + " " + 
									 this.variables.getEnumeration(this.right.getKey(), this.right.offset(i)) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.left.getKey(), this.left.offset(i)) + " " + 
					 				-this.variables.getEnumeration(this.right.getKey(), this.left.offset(i)) + " 0\n");
		}
		
		return this.constraints.toString();
	}
}
