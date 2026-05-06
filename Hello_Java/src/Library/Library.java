package Library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Library {
	
	Book bk = new Book();
	
	public void borrow(int cnt) {
		String target = Integer.toString(cnt);
		
		if (cnt < 1 || cnt > 10) { 
	        System.out.println("Error: Invalid book number.");
	        return;
	    }
		
		if(bk.book.contains(target)) {
			bk.book.remove(target);
			System.out.println("Book " + cnt +" borrowed");
		}  else {
			System.out.println("Error: Book " + cnt + " is already borrowed.");
		}
		
		
	}
	
	public void returBook(int cnt) {
		
		String target = Integer.toString(cnt);
		
		if (cnt < 1 || cnt > 10) { 
	        System.out.println("Error: Invalid book number.");
	        return;
	    }
		
		if(bk.book.contains(target)) {
			System.out.println("Error: Book " + cnt + " is already returned.");
		}  else {
			bk.book.add(target);
			System.out.println("Book " + cnt + " returned");
		}
		
	}
}
