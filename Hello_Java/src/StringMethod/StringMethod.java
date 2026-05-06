package StringMethod;

public class StringMethod {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(addString("0123456", 3, "-"));
		System.out.println(reverse("abc"));
		System.out.println(removeString("01001000", "00"));
	}
	// Returns the string created by adding 's2' after position 'index' of 's1'.
	static String addString(String s1, int index, String s2) {
		String front = s1.substring(0,index);
		String back = s1.substring(index);
		return front + s2 + back;
	}
	// Returns reversed string of 's'
	static String reverse(String s) {
		String result = "";
	    
	    for (int i = s.length() - 1; i >= 0; i--) {
	        result += s.charAt(i);
	    }
		return result;
	}
	// Returns a string with all 's2's removed from 's1'
	static String removeString (String s1, String s2) {
		return s1.replace(s2, "");
	}
	

}
