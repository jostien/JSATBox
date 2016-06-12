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

import java.util.*;

import jsatbox.miscellaneous.VariablesContainer;

public class GeneralSum {
	private VariablesContainer variables;
	private ArrayList<BinaryNumber> summands;
	private ArrayList<BinaryNumber> results;
	private StringBuffer constraints;
	private BinaryNumber result_variables;
	private int max_width;
	
	public GeneralSum(VariablesContainer variables){
		this.variables = variables;
		this.summands = new ArrayList<BinaryNumber>();
		this.results = new ArrayList<BinaryNumber>();
		this.constraints = new StringBuffer();
		this.max_width = -1;
	}

	public void addSummand(BinaryNumber summand) throws Exception{
		this.summands.add(summand);
//		if (this.summands.size() > 1)
//			if (this.summands.get(this.summands.size() - 2).getWidth() !=
//				this.summands.get(this.summands.size() - 1).getWidth())
//				throw new Exception("width of summands is not equal");
	}
	
	public void setMaxWidth(int max_width){
		this.max_width = max_width;
	}
	
	public String getConstraints(int result) throws Exception{
		this.constraints.delete(0, this.constraints.length());
		
		if (this.summands.size() == 1){
			this.result_variables = this.summands.get(0);
			return "";
		}
		
		// add first two summands
		BinaryNumber summand1 = this.summands.get(0);
		BinaryNumber summand2 = this.summands.get(1);
		PairSum ps = new PairSum(this.variables);
		ps.setFirstSummand(summand1);
		ps.setSecondSummand(summand2);

		if (this.max_width > -1)
			ps.setMaxWidth(this.max_width);

		if (this.summands.size() == 2 && result > -1)
			this.constraints.append(ps.getConstraints(result, false));
		else 
			this.constraints.append(ps.getConstraints(result, true));
		this.result_variables = ps.getResultVariables();
		this.results.add(ps.getResultVariables());
		
		// add each following summand to the result of the former
		for (int i = 2; i < this.summands.size() - 1; i++){
			ps = new PairSum(this.variables);
			ps.setFirstSummand(result_variables);
			ps.setSecondSummand(this.summands.get(i));

			if (this.max_width > -1)
				ps.setMaxWidth(this.max_width);

			this.constraints.append(ps.getConstraints(result, true));
			this.result_variables = ps.getResultVariables();
			this.results.add(ps.getResultVariables());
		}
		
		// the last sum has to be constrained to the wanted result
		if (this.summands.size() > 2){
			ps = new PairSum(this.variables);
			ps.setFirstSummand(this.result_variables);
			ps.setSecondSummand(this.summands.get(this.summands.size() - 1));

			if (this.max_width > -1)
				ps.setMaxWidth(this.max_width);

			if (result > -1)
				this.constraints.append(ps.getConstraints(result, false));
			else 
				this.constraints.append(ps.getConstraints(result, true));
			this.result_variables = ps.getResultVariables();
			this.results.add(ps.getResultVariables());
		}
		
		return this.constraints.toString();
	}
	
	public BinaryNumber getResultVariables(){
		return this.result_variables;
	}
	
	public BinaryNumber getResultVariables(int index){
		return this.results.get(index);
	}
}
