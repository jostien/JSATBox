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

package jsatbox.examples;

import jsatbox.miscellaneous.VariablesContainer;
import jsatbox.parsers.SATsolver;
import jsatbox.sat.math.*;

public class ExampleChangeSign{
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		int width = 4;	// width of binary numbers
		
		// data structure, that handles variables
		VariablesContainer variables = new VariablesContainer();
		
		// data structure, that handles constraints
		StringBuffer constraints = new StringBuffer();
		
		// create one bit array of width 4 for representing byte A
		BinaryNumber summand = new BinaryNumber("A", new int[]{0}, 0, width, variables); 
		
		// create a change sign object and add the created byte
		ChangeSign csign = new ChangeSign(variables);
		csign.setSummand(summand);
		// add the change sign constraint to the constraints
		constraints.append(csign.getConstraints());
		
		// add some constraints: here force byte A to be equal to 3
		constraints.append( variables.getEnumeration("A", new int[]{0}) + " 0\n");
		constraints.append( variables.getEnumeration("A", new int[]{1}) + " 0\n");
		constraints.append(-variables.getEnumeration("A", new int[]{2}) + " 0\n");
		constraints.append(-variables.getEnumeration("A", new int[]{3}) + " 0\n");
		
		// solve constraints
		SATsolver ss = new SATsolver(variables, "<path_to_minisat>/minisat/simp/minisat", 0);
		ss.execute(constraints, "<path_to_result_dir>/sum_test.cnf", "<path_to_result_dir>/out", 0, true);
		
		// print result
		for (int i = 0; i < width; i++)
			System.out.print(variables.variableToString("A", new int[]{i}) + " ");
		System.out.println();
		
		BinaryNumber result = csign.getResultVariables();
		for (int i = 0; i < width; i++)
			System.out.print(variables.variableToString(result.getKey(), new int[]{i}) + " ");
		
		// if
		//  3 = 1100
		// then
		// -3 = 1011
		// because
		// -8 = 0001
		// -7 = 1001
		// -6 = 0101
		// -5 = 1101
		// -4 = 0011
		// -3 = 1011
		// -2 = 0111
		// -1 = 1111
		//  0 = 0000
		//  1 = 1000
		//  2 = 0100
		//  3 = 1100
		//  4 = 0010
		//  5 = 1010
		//  6 = 0110
		//  7 = 1110
	}
}
