import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Login {

    private JFrame frame;
    private JLabel emailLabel;
    private JTextField emailTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;


    public static void main(String[] args) {
        new Login();
    }

    public Login() {
        emailLabel = new JLabel("Email: ", SwingConstants.RIGHT);
        emailLabel.setLocation(0, 40);
        emailLabel.setSize(170, 40);
        emailTextField = new JTextField();
        emailTextField.setLocation(190, 40);
        emailTextField.setSize(300, 45);


        passwordLabel = new JLabel("Mật khẩu: ", SwingConstants.RIGHT);
        passwordLabel.setLocation(0, 100);
        passwordLabel.setSize(170, 40);
        passwordField = new JPasswordField();
        passwordField.setLocation(190, 100);
        passwordField.setSize(300, 45);


        loginButton = new JButton("Đăng nhập");
        loginButton.setLocation(130, 170);
        loginButton.setSize(290, 45);
        loginButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();

                try {
                    validateEmail(emailTextField.getText(), passwordField.getText()); // IMAP
                    saveInfo(emailTextField.getText(), passwordField.getText());
                    String[] args = {emailTextField.getText()};
                    ReceiveMail.main(args);
                } catch (MessagingException ex) {
                    JOptionPane.showMessageDialog(frame, "Địa chỉ email hoặc mật khẩu không chính xác. Vui lòng thử lại!","Error", JOptionPane.ERROR_MESSAGE);
                    frame.show();
                    ex.printStackTrace();
                }
            }
        });



        Font font = new Font("Arial", 0, 18);
        emailLabel.setFont(font);
        emailTextField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);


        Border padding = new EmptyBorder(0, 10, 0, 0);
        emailTextField.setBorder(
                new CompoundBorder(emailTextField.getBorder(), padding));
        passwordField.setBorder(
                new CompoundBorder(passwordField.getBorder(), padding));



        frame = new JFrame("Nhóm 14 - Mail Client");
        frame.setSize(550, 290);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.add(emailLabel);
        frame.add(emailTextField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    // Xác thực tài khoản Email
    public void validateEmail(String USERNAME, String PASSWORD) throws MessagingException {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = null;
        store = session.getStore("imaps");
        store.connect("imap.gmail.com", USERNAME, PASSWORD);

    }

    public void saveInfo(String email, String password) {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream("gmailConfig.properties");


            prop.setProperty("gmail.user", email);
            prop.setProperty("gmail.password", password);

            
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
