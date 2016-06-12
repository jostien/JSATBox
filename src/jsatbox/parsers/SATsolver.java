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

package jsatbox.parsers;

import jsatbox.miscellaneous.VariablesContainer;
import jsatbox.sat.math.*;

import java.util.*;
import java.io.*;

public class SATsolver {
	private VariablesContainer variables;
	private String solver = "<path_to_minisat>/minisat/simp/minisat";
	private int seed = -1;
	private String in;
	private String out;
	private String cmd;
	
	public SATsolver(VariablesContainer variables){
		this.variables = variables;
	}

	public SATsolver(VariablesContainer variables, String solver, int seed){
		this.variables = variables;
		this.solver = solver;
		this.seed = seed;
	}
	
	public SATsolver(VariablesContainer variables, String solver, String in, String out, int seed){
		this.variables = variables;
		this.solver = solver;
		this.in = in;
		this.out = out;
		this.seed = seed;
		
		if (this.seed == -1)
			this.cmd = this.solver + " " + in + " " + out;
		else
			this.cmd = this.solver + " " + in + " " + out + " " + seed;
	}
	
	public String getCmd(){
		return this.cmd;
	}
	
	public String execute(StringBuffer constraints, boolean verbose) throws Exception{
		String status = "";
		
		// ----------------------------------------------------------------------------------------
		//							write constraints to cnf file
		// ----------------------------------------------------------------------------------------

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(in)));

		bw.write("c Constraints file\n");
		bw.write("c\n");
		bw.write("p cnf " + this.variables.getEnumerationSize() + " " + constraints.length() + "\n");

		bw.write(constraints.toString());
		bw.close();
		
		Runtime run = Runtime.getRuntime();
		Process process = null;
		process = run.exec(this.cmd);

		// there error stream blocks reading the streams for some executables!
		//BufferedReader err_stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getErrorStream())));
		BufferedReader inp_stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getInputStream())));
		//String err = null;
		String inp = null;

		//while (((err = err_stream.readLine()) != null || (inp = inp_stream.readLine()) != null)){
		while ((inp = inp_stream.readLine()) != null){
			//if (err != null && verbose)
			//	System.out.println(err);
			if (inp != null && verbose)
				System.out.println(inp);

			if (inp != null)
				if (inp.indexOf("SATISFIABLE") != -1)
					status = inp;
		}

		if (status.indexOf("UNSATISFIABLE") != -1)
			return "UNSATISFIABLE";
		
		//err_stream.close();
		inp_stream.close();
		
		// ----------------------------------------------------------------------------------------
		//								read possible solution in
		// ----------------------------------------------------------------------------------------
		BufferedReader br = new BufferedReader(new FileReader(new File(out)));
		String line = br.readLine(); // first line is not from interest, only the last
		String line_;
		while ((line_ = br.readLine()) != null)
			line = line_;
		br.close();

		int[] parities = new int[this.variables.numberOfVariables()]; // for keeping the negations

		String[] line_array = line.split(" ");

		for (int i = 0; i < line_array.length; i++){
			if (!line_array[i].equals("v") && !line_array[i].equals("0")){
				int var = (new Integer(line_array[i])).intValue();

				int index = ((Integer)this.variables.enumeration.get(new Integer(Math.abs(var)))).intValue();
				parities[index - 1] = var;
			}
		}

		this.variables.setParities(parities, new String[] {}); // sets parities
		
		return "SATISFIABLE";
	}
	
	public String execute(StringBuffer constraints, String in, String out, boolean verbose) throws Exception{
		return this.execute(constraints, in, out, -1, verbose);
	}
	
	public String execute(StringBuffer constraints, String in, String out, int seed, boolean verbose) throws Exception{
		String status = "";
		
		// ----------------------------------------------------------------------------------------
		//							write constraints to cnf file
		// ----------------------------------------------------------------------------------------
		
//		// max seems to be equal to enumeration size (at least for several examples)
//		// # of clauses should be the number of StringBuffer elements
//		// therefore, the stuff below is redundant
//		System.out.println("enumeration size: " + this.variables.getEnumerationSize());
//		System.out.println("hm size: " + this.variables.size());
//		System.out.println("number of variables: " + this.variables.numberOfVariables());
//		System.out.println("start");
//		int max = 0;
//		HashMap<Integer, String> tritra = new HashMap<Integer, String>();
//		String s = constraints.toString();
//		String[] cl = s.split("\n");
//		int clauses = cl.length;
//		for (int bla = 0; bla < cl.length; bla++){
//			String[] var = cl[bla].split(" ");
//			for (int blub = 0; blub < var.length; blub++){
//				int value = Math.abs(((new Integer(var[blub])).intValue()));
//				tritra.put(new Integer(value), "bla");
//				if (value > max)
//					max = value;
//			}
//		}
//		System.out.println("number of variables2: " + max);
//		System.out.println("number of clauses: " + clauses);
//		System.out.println("stop");

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(in)));

		bw.write("c Constraints file\n");
		bw.write("c\n");
		bw.write("p cnf " + this.variables.getEnumerationSize() + " " + constraints.length() + "\n");

		bw.write(constraints.toString());
		bw.close();
		
		Runtime run = Runtime.getRuntime();
		Process process = null;
		String cmd = "";
		if (seed == -1)
			cmd = this.solver + " " + in + " " + out;
		else
			cmd = this.solver + " " + in + " " + out + " " + seed;
		process = run.exec(cmd);

		// there error stream blocks reading the streams for some executables!
		//BufferedReader err_stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getErrorStream())));
		BufferedReader inp_stream = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getInputStream())));
		//String err = null;
		String inp = null;

		//while (((err = err_stream.readLine()) != null || (inp = inp_stream.readLine()) != null)){
		while ((inp = inp_stream.readLine()) != null){
			//if (err != null && verbose)
			//	System.out.println(err);
			if (inp != null && verbose)
				System.out.println(inp);

			if (inp != null)
				if (inp.indexOf("SATISFIABLE") != -1)
					status = inp;
		}

		//err_stream.close();
		inp_stream.close();
		
		// ----------------------------------------------------------------------------------------
		//								read possible solution in
		// ----------------------------------------------------------------------------------------
		BufferedReader br = new BufferedReader(new FileReader(new File(out)));
		String line = br.readLine(); // first line is not from interest, only the last
		String line_;
		while ((line_ = br.readLine()) != null)
			line = line_;
		br.close();

		int[] parities = new int[this.variables.numberOfVariables()]; // for keeping the negations

		String[] line_array = line.split(" ");

		for (int i = 0; i < line_array.length; i++){
			if (!line_array[i].equals("v") && !line_array[i].equals("0")){
				int var = (new Integer(line_array[i])).intValue();

				int index = ((Integer)this.variables.enumeration.get(new Integer(Math.abs(var)))).intValue();
				parities[index - 1] = var;
			}
		}

		this.variables.setParities(parities, new String[] {}); // sets parities
		
		return status;
	}
	
	public String execute2(StringBuffer constraints, boolean verbose) throws Exception{
		String status = "SATISFIABLE";
		
		// ----------------------------------------------------------------------------------------
		//								read possible solution in
		// ----------------------------------------------------------------------------------------
		BufferedReader br = new BufferedReader(new FileReader(new File(this.out)));
		String line = br.readLine(); // first line is not from interest, only the last
		String line_;
		while ((line_ = br.readLine()) != null)
			line = line_;
		br.close();

		int[] parities = new int[this.variables.numberOfVariables()]; // for keeping the negations

		String[] line_array = line.split(" ");

		for (int i = 0; i < line_array.length; i++){
			if (!line_array[i].equals("v") && !line_array[i].equals("0")){
				int var = (new Integer(line_array[i])).intValue();

				int index = ((Integer)this.variables.enumeration.get(new Integer(Math.abs(var)))).intValue();
				parities[index - 1] = var;
			}
		}

		this.variables.setParities(parities, new String[] {}); // sets parities
		
		return status;
	}
}
