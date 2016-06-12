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

public class SumSet {
	private HashMap<PairSum, PairSum> sums;
	//private IndexingVariables variables;
	//private int sum_counter;
	
	SumSet(VariablesContainer varialbes){
		this.sums = new HashMap<PairSum, PairSum>();
		//this.variables = variables;
		//this.sum_counter = 0;
	}

	public int addSum(PairSum sum){
		this.sums.put(sum, sum);
		return sum.hashCode();
	}
	
	/*public int addSum(String key1, int[] indices1, int free1, String key2, int[] indices2, int free2, int width){
		PairSum sum = new PairSum(width, this.variables, this.sum_counter);
		sum.setFirstSummand(new Summand(key1, indices1, free1));
		sum.setSecondSummand(new Summand(key2, indices2, free2));
		this.sums.put(sum, sum);
		this.sum_counter++;
		return sum.hashCode();
	}
	
	public int addSum(String key1, int[] indices1, int free1, String key2, int[] indices2, int free2, int width, int result){
		PairSum sum = new PairSum(width, result, this.variables, this.sum_counter);
		sum.setFirstSummand(new Summand(key1, indices1, free1));
		sum.setSecondSummand(new Summand(key2, indices2, free2));
		this.sums.put(sum, sum);
		this.sum_counter++;
		return sum.hashCode();
	}*/
	
	public PairSum getSum(int hash_code){
		return this.sums.get(hash_code);
	}
	
	public int size(){
		return this.sums.size();
	}
}
