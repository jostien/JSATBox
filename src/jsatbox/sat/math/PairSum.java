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

public class PairSum {
	private BinaryNumber summand1;
	private BinaryNumber summand2;
	
	private String keyC;
	private String keyS;
	private String keyR;
	private String keyM1;
	private String keyM2;
	
	private StringBuffer constraints;
	
	private int hash_code;
	
	private VariablesContainer variables;
	
	private int max_width;
	
	PairSum(VariablesContainer variables){
		this.init(variables);
	}
	
	public void init(VariablesContainer variables){
		this.variables = variables;
		this.constraints = new StringBuffer();
		
		// create unique names for variables
		this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		while (this.variables.containsKey("c." + this.hash_code) ||
			   this.variables.containsKey("s." + this.hash_code) ||
			   this.variables.containsKey("r." + this.hash_code) ||
			   this.variables.containsKey("m1." + this.hash_code) ||
			   this.variables.containsKey("m2." + this.hash_code))
			this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		
		this.keyC = "c." + this.hash_code;
		this.keyS = "s." + this.hash_code;
		this.keyR = "r." + this.hash_code;
		this.keyM1 = "m1." + this.hash_code;
		this.keyM2 = "m2." + this.hash_code;
		
		this.max_width = -1;
	}
	
	public void setFirstSummand(BinaryNumber summand){
		this.summand1 = summand;		
	}
	
	public void setSecondSummand(BinaryNumber summand){
		this.summand2 = summand;
	}

	public void setMaxWidth(int max_width){
		this.max_width = max_width;
	}
	
	public String getConstraints(int result, boolean general_sum) throws Exception{
//		if (this.summand1.getWidth() != this.summand2.getWidth())
//			throw new Exception("two summands have not same width");
		
		int width = this.summand1.getWidth();
		
		this.variables.add(this.keyC, new int[]{width + 1});
		this.variables.add(this.keyS, new int[]{width});
		this.variables.add(this.keyR, new int[]{width});
		
		this.constraints.delete(0, this.constraints.length());

		// if there are two positive numbers the result must be positive
		// if there are two negative numbers the result must be negative
		this.constraints.append(-this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(width - 1)) + " " + 
								 this.variables.getEnumeration(this.keyR, new int[]{width - 1}) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(width - 1)) + " " +
		 						-this.variables.getEnumeration(this.keyR, new int[]{width - 1}) + " 0\n");
		
		// first carry-over has to be empty
		this.constraints.append(-this.variables.getEnumeration(this.keyC, new int[]{0}) + " 0\n");
		for (int i = 0; i < width; i++){
			this.constraints.append( this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
									-this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
									 this.variables.getEnumeration(this.keyS, new int[]{i}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
								 	 this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
								 	 this.variables.getEnumeration(this.keyS, new int[]{i}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
				 					-this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
				 					-this.variables.getEnumeration(this.keyS, new int[]{i}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
 								 	 this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
 								 	-this.variables.getEnumeration(this.keyS, new int[]{i}) + " 0\n");			
			
			this.constraints.append(-this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
									-this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
									 this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
									-this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
									 this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
									 this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
									-this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.summand1.getKey(), this.summand1.offset(i)) + " " +
			 				 	 	 this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
			 				 	 	-this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
							 	 	 this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
							 	 	-this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.summand2.getKey(), this.summand2.offset(i)) + " " +
							 	 	 this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
							 	 	-this.variables.getEnumeration(this.keyC, new int[]{i + 1}) + " 0\n");			
			
			this.constraints.append( this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
									-this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
									 this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
								 	 this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
								 	 this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
			this.constraints.append(-this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
				 					-this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
				 					-this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
			this.constraints.append( this.variables.getEnumeration(this.keyS, new int[]{i}) + " " +
								 	 this.variables.getEnumeration(this.keyC, new int[]{i}) + " " +
								 	-this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
		}
		
		if (!general_sum){
			// last carry-over has to be empty
			//this.constraints.append(-this.variables.getEnumeration(this.keyC, new int[]{width}) + " 0\n");
			
			String s = Integer.toBinaryString(result);
			
			int k = s.length() - 1;
			for (int i = 0; i < s.length(); i++){
				if (s.charAt(k) == '0')
					this.constraints.append(-this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
				else
					this.constraints.append( this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");
				k--;
			}
			for (int i = s.length(); i < width; i++)
				this.constraints.append(-this.variables.getEnumeration(this.keyR, new int[]{i}) + " 0\n");				
		}
		
		// if a maximal value/width for the result is given
		if (this.max_width > -1){
			this.variables.add(this.keyM1, new int[]{1});
			this.variables.add(this.keyM2, new int[]{1});

			// and
			for (int i = this.max_width; i < width; i++)
				this.constraints.append(-this.variables.getEnumeration(this.keyR, new int[]{i}) + " ");
			this.constraints.append( this.variables.getEnumeration(this.keyM1, new int[]{0}) + " 0\n");
			
			for (int i = this.max_width; i < width; i++)
				this.constraints.append( this.variables.getEnumeration(this.keyR, new int[]{i}) + " " +
										-this.variables.getEnumeration(this.keyM1, new int[]{0}) + " 0\n");
			
			// or
			for (int i = this.max_width; i < width; i++)
				this.constraints.append( this.variables.getEnumeration(this.keyR, new int[]{i}) + " ");
			this.constraints.append(-this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
			
			for (int i = this.max_width; i < width; i++)
				this.constraints.append(-this.variables.getEnumeration(this.keyR, new int[]{i}) + " " +
										 this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
			
			this.constraints.append(-this.variables.getEnumeration(this.keyM1, new int[]{0}) + " " +
					 				 this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");

			this.constraints.append( this.variables.getEnumeration(this.keyM1, new int[]{0}) + " " +
	 				 				-this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
		}
		
		return this.constraints.toString();
	}
	
	public BinaryNumber getResultVariables(){
		return new BinaryNumber(this.keyR, new int[]{0}, 0, this.summand1.getWidth());
	}
	
	public int hashCode(){
		 return this.hash_code;
	}
}
