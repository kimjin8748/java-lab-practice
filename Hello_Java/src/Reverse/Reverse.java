package Reverse;

import java.io.*;
import java.util.Stack;

public class Reverse {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("input.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            Stack<String> stack = new Stack<>();
            String data;

            while ((data = br.readLine()) != null) {
                stack.push(data);
            }
            br.close();

            FileOutputStream fos = new FileOutputStream("output.txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            
            while (!stack.isEmpty()) {
                bw.write(stack.pop());
                bw.newLine();
            }
            bw.close();
            
            System.out.println("Success: output.txt created.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}