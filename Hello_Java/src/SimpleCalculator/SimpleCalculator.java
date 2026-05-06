package SimpleCalculator;

import java.util.Scanner;

class OutOfRangeException extends Exception {}
class AddZeroException extends Exception {}
class SubtractZeroException extends Exception {}

public class SimpleCalculator {
 public static void main(String[] args) {
     Scanner scanner = new Scanner(System.in);
     String input = scanner.nextLine().trim();
     
     try {
         int operatorIndex = -1;
         char operator = ' ';
         
         if (input.contains("+")) {
             operatorIndex = input.indexOf('+');
             operator = '+';
         } else if (input.contains("-")) {
             operatorIndex = input.indexOf('-');
             operator = '-';
         } else {
             return; 
         }

         int a = Integer.parseInt(input.substring(0, operatorIndex));
         int b = Integer.parseInt(input.substring(operatorIndex + 1));

         if (operator == '+') {
             if (a == 0 || b == 0) {
                 throw new AddZeroException();
             }
         } else if (operator == '-') {
             if (a == 0 || b == 0) {
                 throw new SubtractZeroException();
             }
         }

         int result = 0;
         if (operator == '+') {
             result = a + b;
         } else {
             result = a - b;
         }

         if (a < 0 || a > 1000 || b < 0 || b > 1000 || result < 0 || result > 1000) {
             throw new OutOfRangeException();
         }

         System.out.println(result);

     } catch (AddZeroException | SubtractZeroException | OutOfRangeException e) {
         System.out.println(e.getClass().getSimpleName());
     } catch (Exception e) {
         System.out.println(e.getClass().getSimpleName());
     } finally {
         scanner.close();
     }
 }
}