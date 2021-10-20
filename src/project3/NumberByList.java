package project3;

import java.util.Scanner;

/**
 * <p>This class is part of the project 3 in csci102, fall 2021 semester. 
 * 
 * 
 * <p>This program performs numerical calculations based on user's input.
 * An operation needs to be provided using a three-token format: 
 *    operand1  operator  operand2  
 * in which the three tokens are separated by one of more white spaces. 
 * The program terminates when the end of file (EOF) character is found. 
 * (Note, this character is entered in the console by typing Ctrl+D sequence.) 
 * 
 * <p>Valid operands are any positive integers (no length limit). 
 * 
 * <p>Valid operators are:
 * <pre>
 *  +   addition
 *  *   multiplication
 *  <   less than 
 *  <=  less than or equal
 *  >   greater than
 *  >=  greater than or equal 
 *  ==  equal
 *  <>  not equal 
 * </pre>
 */

public class NumberByList {

	public static void main(String[] args) {
		
		
		Scanner in = new Scanner (System.in);
		
		String line = null;
		String[] tokens; 
		Number n1=null, n2=null, n3=null;
		
		while (in.hasNextLine() ) {
			//read input from user and break it into expected three tokens:
			//  operand1  operator  operand2 
			line = in.nextLine();
			tokens = line.split("\\s+"); 
			try {
				//get two operands 
				n1 = new Number(tokens[0]);
				n2 = new Number(tokens[2]);
				//perform the action of the operator 
				switch (tokens[1]) {
				case "+": 
					n3 = n1.add(n2); 
					System.out.println(n3);
					break;
				case "*": 
					n3 = n1.multiply(n2); 
					System.out.println(n3);
					break; 
				case "==":
					System.out.println(n1.equals(n2)  ? "true" : "false"); 
					break;
				case "<": 
					System.out.println(n1.compareTo(n2) < 0  ? "true" : "false"); 
					break;
				case "<=": 
					System.out.println(n1.compareTo(n2) <= 0  ? "true" : "false"); 
					break;
				case ">": 
					System.out.println(n1.compareTo(n2) > 0  ? "true" : "false"); 
					break;
				case ">=": 
					System.out.println(n1.compareTo(n2) >= 0  ? "true" : "false"); 
					break;
				case "<>": 
					System.out.println(n1.compareTo(n2) != 0  ? "true" : "false"); 
					break;
					
					
				default: //deals with unsupported and illegal operators 
					System.out.println("Illegal operator found: " + line);
					continue; 
				}
			}
			catch (IllegalArgumentException ex) {  //deals with illegal and misformatted requests 
				System.out.println("Illegal expression found: " + line);
				continue;
			}
			catch (ArrayIndexOutOfBoundsException ex) { //deals with illegal and misformatted requests 
				System.out.println("Illegal expression found: " + line);
				continue;
			}
		
		}
		
		in.close();
		
	}
	//prevent class instantiation (otherwise compiler generates default constructor) 
	private NumberByList( ) {
		
	}

}
