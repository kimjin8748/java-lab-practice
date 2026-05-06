package NumberSystemConverter;

import java.util.Scanner;

public class NumberSystemConverter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		
		int number = s.nextInt();
		//System.out.println(number);
		String bStr = Integer.toBinaryString(number);
		System.out.println("b "+ bStr);
		String oStr = Integer.toOctalString(number);
		System.out.println("o " +oStr);
		String hStr = Integer.toHexString(number);
		System.out.println("h " +hStr);
	}

}
