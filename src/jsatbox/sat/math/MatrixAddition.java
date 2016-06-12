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

public class MatrixAddition {
	private VariablesContainer variables;
	private StringBuffer constraints;
	private BinaryNumber[][] A;
	private BinaryNumber[][] B;
	private BinaryNumber[][] C;
	
	public MatrixAddition(VariablesContainer variables){
		this.variables = variables;
	}
	
	public void setFirstMatrix(BinaryNumber[][] A){
		this.A = A;		
	}
	
	public void setSecondMatrix(BinaryNumber[][] B){
		this.B = B;
	}
	
	public String getConstraints() throws Exception{
		this.constraints = new StringBuffer();
		
		int n = this.A.length;
		int m = this.A[0].length;
		
		if ((n != this.B.length) || (m != this.B[0].length))
			throw new Exception("A and B can not be added because of dimensions");
		
		this.C = new BinaryNumber[n][m];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				GeneralSum gs = new GeneralSum(this.variables);
				gs.addSummand(this.A[i][j]);
				gs.addSummand(this.B[i][j]);
				this.constraints.append(gs.getConstraints(-1));
				this.C[i][j] = gs.getResultVariables();
			}
		}
		
		return this.constraints.toString();
	}
	
	public BinaryNumber[][] getResultVariables(){
		return this.C;
	}
}
