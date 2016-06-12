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

public class Twice {
	private VariablesContainer variables;
	private BinaryNumber summand;
	private StringBuffer constraints;
	private String keyT;
	private int hash_code;
	
	public Twice(VariablesContainer variables){
		this.variables = variables;
		this.constraints = new StringBuffer();
		
		this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		while (this.variables.containsKey("t." + this.hash_code))
			this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		
		this.keyT = "t." + this.hash_code;
	}
	
	public void setSummand(BinaryNumber summand){
		this.summand = summand;
	}
	
	public String getConstraints() throws Exception{
		int width = this.summand.getWidth();
		
		this.variables.add(this.keyT, new int[]{width});
		
		this.constraints.delete(0, this.constraints.length());

		this.constraints.append(-this.variables.getEnumeration(this.keyT, new int[]{0}) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.keyT, new int[]{width - 1}) + " " +
								-this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.keyT, new int[]{width - 1}) + " " +
								 this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(width - 1)) + " 0\n");
		//this.constraints.append(-this.variables.getEnumeration(this.keyT, new int[]{width - 1}) + " 0\n");
		for (int i = 1; i < width; i++){
			this.constraints.append(-this.variables.getEnumeration(this.keyT, new int[]{i}) + " " + 
								 	 this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(i - 1)) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.keyT, new int[]{i}) + " " + 
					 				-this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(i - 1)) + " 0\n");
		}
		
		return this.constraints.toString();
	}
	
	public BinaryNumber getResultVariables(){
		return new BinaryNumber(this.keyT, new int[]{0}, 0, this.summand.getWidth());
	}
}
