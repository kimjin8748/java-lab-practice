package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {
    String id = "test";
    String password = "12345678"; // to compare
    JTextField textField1;
    JPasswordField textField2;

    public Login() {
        setTitle("Login");
        setSize(250, 135);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        JPanel idPanel = new JPanel();
        JLabel idLabel = new JLabel("id");
        idLabel.setPreferredSize(new Dimension(80, 10)); 
        textField1 = new JTextField(12);
        idPanel.add(idLabel);
        idPanel.add(textField1);

        JPanel pwPanel = new JPanel();
        JLabel pwLabel = new JLabel("Password");
        pwLabel.setPreferredSize(new Dimension(80, 10));
        textField2 = new JPasswordField(12);
        pwPanel.add(pwLabel);
        pwPanel.add(textField2);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(new ButtonClickListener());

        add(idPanel);
        add(pwPanel);
        add(loginBtn);

        setVisible(true);
    }

    class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tempId = textField1.getText();
            
            String tempPassword = new String(textField2.getPassword());

            if (tempId.equals(id) && tempPassword.equals(password)) {
                JOptionPane.showMessageDialog(null, "Success");
            } else {
                JOptionPane.showMessageDialog(null, "Fail");
            }
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}