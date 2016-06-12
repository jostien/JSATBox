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

public class PairMultiplication {
	private VariablesContainer variables;
	private BinaryNumber factor1;
	private BinaryNumber factor2;
	private BinaryNumber result;
	
	private int max_width;
	
	private String keyP;
	private String keyU1;
	private String keyU2;
	private String keyM1;
	private String keyM2;
	
	private StringBuffer constraints;
	
	private int hash_code;
	
	public PairMultiplication(VariablesContainer variables){
		this.variables = variables;		
		this.constraints = new StringBuffer();
		
		this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		while (this.variables.containsKey("p." + this.hash_code)||
			   this.variables.containsKey("u1." + this.hash_code)||
			   this.variables.containsKey("u2." + this.hash_code)||
			   this.variables.containsKey("m1." + this.hash_code)||
			   this.variables.containsKey("m2." + this.hash_code))
			this.hash_code = Long.toHexString(System.currentTimeMillis()).hashCode();
		
		this.keyP  = "p."  + this.hash_code;
		this.keyU1 = "u1." + this.hash_code;
		this.keyU2 = "u2." + this.hash_code;
		this.keyM1 = "m1." + this.hash_code;
		this.keyM2 = "m2." + this.hash_code;
		
		this.max_width = -1;
	}
	
	public void setFirstNumber(BinaryNumber factor1){
		this.factor1 = factor1;
	}
	
	public void setSecondNumber(BinaryNumber factor2){
		this.factor2 = factor2;
	}
	
	public void setMaxWidth(int max_width){
		this.max_width = max_width;
	}
	
	public String getConstraints() throws Exception{
		// if (this.factor1.getWidth() != this.factor2.getWidth())
		//	throw new Exception("two summands have not same width");
		
		int width = Math.min(this.factor1.getWidth(), this.factor2.getWidth());//this.factor1.getWidth();
		
		for (int i = 0; i < width; i++)
			this.variables.add(this.keyP + "." + i, new int[]{2*width});
		this.variables.add(this.keyU1, new int[]{1});
		this.variables.add(this.keyU2, new int[]{1});
		
		this.constraints.delete(0, this.constraints.length());

		for (int i = 0; i < width; i++){
			for (int j = 0; j < width; j++){
				this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(j)) + " " +
										-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(i)) + " " + 
										 this.variables.getEnumeration(this.keyP + "." + i, new int[]{j + i}) + " 0\n");
				this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(j)) + " " + 
						 				-this.variables.getEnumeration(this.keyP + "." + i, new int[]{j + i}) + " 0\n");
				this.constraints.append( this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(i)) + " " + 
		 								-this.variables.getEnumeration(this.keyP + "." + i, new int[]{j + i}) + " 0\n");
			}
			// must be filled with zeros
			for (int j = 0; j < i; j++)
				this.constraints.append(-this.variables.getEnumeration(this.keyP + "." + i, new int[]{j}) + " 0\n");
			// must be filled correspondingly
			for (int j = i + width; j < 2*width - 1; j++){
				this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
										-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(i)) + " " + 
										 this.variables.getEnumeration(this.keyP + "." + i, new int[]{j}) + " 0\n");
				this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " + 
		 								-this.variables.getEnumeration(this.keyP + "." + i, new int[]{j}) + " 0\n");
				this.constraints.append( this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(i)) + " " + 
										-this.variables.getEnumeration(this.keyP + "." + i, new int[]{j}) + " 0\n");
			}			
		}
		
		GeneralSum gs = new GeneralSum(this.variables);
		for (int i = 0; i < width - 1; i++)
			gs.addSummand(new BinaryNumber(this.keyP + "." + i, new int[]{0}, 0, 2*width));
		
		ChangeSign cp = new ChangeSign(this.variables);
		cp.setSummand(new BinaryNumber(this.keyP + "." + (width - 1), new int[]{0}, 0, 2*width));
		this.constraints.append(cp.getConstraints());
		gs.addSummand(cp.getResultVariables());
		
		this.constraints.append(gs.getConstraints(-1));
		this.result = gs.getResultVariables();

		// if there are two positive/negative numbers the result must be positive
		// else the result must be negative
		this.constraints.append(-this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
		 						 this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
				 				 this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " + 
								 this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
				 				 this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
				 				 this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		
		// save if first factor is zero or not	
		for (int k = 0; k < width - 1; k++){
			this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(k)) + " " +
									 this.variables.getEnumeration(this.keyU1, new int[]{0}) + " 0\n");
		}
		for (int k = 0; k < width - 1; k++)
			this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(k)) + " ");
		this.constraints.append(-this.variables.getEnumeration(this.keyU1, new int[]{0}) + " 0\n");

		// save if second factor is zero or not
		for (int k = 0; k < width - 1; k++){
			this.constraints.append(-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(k)) + " " +
									 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " 0\n");
		}
		for (int k = 0; k < width - 1; k++)
			this.constraints.append( this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(k)) + " ");
		this.constraints.append(-this.variables.getEnumeration(this.keyU2, new int[]{0}) + " 0\n");

		// set parity correspondingly
		this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
				 				 this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.factor1.getKey(), this.factor1.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
				 				 this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append(-this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
				 				 this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.factor2.getKey(), this.factor2.offset(width - 1)) + " " +
								 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");		
		this.constraints.append( this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				 this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.keyU1, new int[]{0}) + " " +
				 				-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		this.constraints.append( this.variables.getEnumeration(this.keyU2, new int[]{0}) + " " +
								-this.variables.getEnumeration(this.result.getKey(),  this.result.offset(2*width - 1)) + " 0\n");
		
		// if a maximal value/width for the result is given
		if (this.max_width > -1){
			this.variables.add(this.keyM1, new int[]{1});
			this.variables.add(this.keyM2, new int[]{1});

			// and
			for (int i = this.max_width; i < 2*width; i++)
				this.constraints.append(-this.variables.getEnumeration(this.result.getKey(), this.result.offset(i)) + " ");
			this.constraints.append( this.variables.getEnumeration(this.keyM1, new int[]{0}) + " 0\n");
			
			for (int i = this.max_width; i < 2*width; i++)
				this.constraints.append( this.variables.getEnumeration(this.result.getKey(), this.result.offset(i)) + " " +
										-this.variables.getEnumeration(this.keyM1, new int[]{0}) + " 0\n");
			
			// or
			for (int i = this.max_width; i < 2*width; i++)
				this.constraints.append( this.variables.getEnumeration(this.result.getKey(), this.result.offset(i)) + " ");
			this.constraints.append(-this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
			
			for (int i = this.max_width; i < 2*width; i++)
				this.constraints.append(-this.variables.getEnumeration(this.result.getKey(), this.result.offset(i)) + " " +
										 this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
			
			this.constraints.append(-this.variables.getEnumeration(this.keyM1, new int[]{0}) + " " +
					 				 this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");

			this.constraints.append( this.variables.getEnumeration(this.keyM1, new int[]{0}) + " " +
	 				 				-this.variables.getEnumeration(this.keyM2, new int[]{0}) + " 0\n");
		}
		
		return this.constraints.toString();
	}
	
	public BinaryNumber getResultVariables(){
		return this.result;
	}
	
	public BinaryNumber getIsZeroVariable1(){
		return new BinaryNumber(this.keyU1, new int[]{0}, 0, 1);
	}
	
	public BinaryNumber getIsZeroVariable2(){
		return new BinaryNumber(this.keyU2, new int[]{0}, 0, 1);
	}
}
