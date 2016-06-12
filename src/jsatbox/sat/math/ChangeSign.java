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

/*
 * Changes sign of integer number.
 */
public class ChangeSign {
	private BinaryNumber summand;
	private VariablesContainer variables;
	private String keyP;
	private int hash_code;
	private StringBuffer constraints;
	private BinaryNumber return_summand;
	
	public ChangeSign(VariablesContainer variables){
		this.variables = variables;
		this.constraints = new StringBuffer();

		this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		while (this.variables.containsKey("p." + this.hash_code))
			this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		
		this.keyP = "p." + this.hash_code;
	}

	public void setSummand(BinaryNumber summand){
		this.summand = summand;
	}

	public String getConstraints() throws Exception{
		int width = this.summand.getWidth();
		
		this.variables.add(this.keyP, new int[]{width});
		this.constraints.delete(0, this.constraints.length());

		// flip bits
		for (int i = 0; i < width; i++){
			this.constraints.append( this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(i)) + " " + 
								 	 this.variables.getEnumeration(this.keyP, new int[]{i}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.summand.getKey(), this.summand.offset(i)) + " " + 
					 				-this.variables.getEnumeration(this.keyP, new int[]{i}) + " 0\n");
		}
		
		// create one
		if (!this.variables.containsKey("one")){
			this.variables.add("one", new int[]{width});
		
			this.constraints.append( this.variables.getEnumeration("one", new int[]{0}) + " 0\n");
			for (int i = 1; i < width; i++)
				this.constraints.append(-this.variables.getEnumeration("one", new int[]{i}) + " 0\n");
		}
		
		// add one to flipped bits
		PairSum ps = new PairSum(this.variables);
		ps.setFirstSummand(new BinaryNumber(this.keyP, new int[]{0}, 0, width));
		ps.setSecondSummand(new BinaryNumber("one", new int[]{0}, 0, width));
		this.constraints.append(ps.getConstraints(-1, true));
		this.return_summand = ps.getResultVariables();
		
		return this.constraints.toString();
	}
	
	public BinaryNumber getResultVariables(){
		return this.return_summand;
	}
}
