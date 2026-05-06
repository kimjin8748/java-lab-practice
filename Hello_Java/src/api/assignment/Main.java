package api.assignment;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
    	ContactManager manager = new ContactManager();
        try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
			    String input = scanner.nextLine().trim();
			    if (input.isEmpty()) continue;
			    if (input.equals("exit")) break; // 종료 조건

			    String[] parts = input.split(" ");
			    String command = parts[0];

			    try {
			        switch (command) {
			            case "add":
			                if (parts.length < 3) {
			                    System.out.println("error");
			                } else {
			                    boolean success = manager.add(parts[1], parts[2]);
			                    if (!success) System.out.println("error");
			                }
			                break;

			            case "find":
			                if (parts.length < 2) {
			                    System.out.println("error");
			                } else {
			                    String phone = manager.find(parts[1]);
			                    if (phone == null) {
			                        System.out.println("error"); // 찾는 이름이 없을 때
			                    } else {
			                        System.out.println(phone);
			                    }
			                }
			                break;

			            case "delete":
			                if (parts.length < 2) {
			                    System.out.println("error");
			                } else {
			                    boolean deleted = manager.delete(parts[1]);
			                    if (!deleted) System.out.println("error");
			                }
			                break;

			            case "show":
			                ArrayList<String> list = manager.show();
			                if(list.isEmpty()) System.out.println("no list");
			                for (String info : list) {
			                    System.out.println(info);
			                }
			                break;

			            default:
			                System.out.println("error");
			                break;
			        }
			    } catch (Exception e) {
			        System.out.println("error");
			    }
			}
		}
	}

}
