import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
/*
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
*/
public class SendMail {
    private static JFrame frame;
    private JTextField toTextField;
    private JTextField subjectTextField;
    private JTextArea messageField;
    private JButton gửiThưButton;
    private JPanel panel2;
    private JButton attachButton;
    private JTextField attachField;
    String attachfile_path;

    public SendMail()
    {
        try {
            Image img = ImageIO.read(getClass().getResource("icons/SendMail24.gif"));
            gửiThưButton.setIcon(new ImageIcon(img));
            gửiThưButton.setMargin(new Insets(0, 0, 0, 0));

            img = ImageIO.read(getClass().getResource("icons/attach.png"));
            attachButton.setIcon(new ImageIcon(img));
            attachButton.setMargin(new Insets(0, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gửiThưButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String toAddress = toTextField.getText();
                String subject = subjectTextField.getText();
                String message = messageField.getText();
                try
                {
                    SendMailInfo(toAddress,subject,message,attachfile_path);
                    JOptionPane.showMessageDialog(frame,
                            "Gửi email thành công!");
                    frame.dispose();
                }
                catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(frame,
                            "Lỗi: " + ex.getMessage(),
                            "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        attachButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                attachfile_path = f.getAbsolutePath();
                attachField.setText(attachfile_path);
            }
        });
    }

    public void SendMailInfo(String receiverEmail, String title, String content, String attachfile) throws IOException
    {
        Properties prop = new Properties();

        prop.load(new FileInputStream("gmailConfig.properties"));
        final String username = prop.getProperty("gmail.user");
        final String password = prop.getProperty("gmail.password");

        System.out.println("-----------------------------------------");

        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        // get Session
        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        // soạn message
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject(title);
            message.setSentDate(new Date());
            if (attachfile_path == null)
            {
                message.setText(content);
                Transport.send(message);
            }
            else
            {
                // Tạo multi-part
                MimeMultipart multipart = new MimeMultipart();
                // Tạo content part
                MimeBodyPart contentBodyPart = new MimeBodyPart();
                contentBodyPart.setContent(content, "text/html; charset=UTF-8");
                multipart.addBodyPart(contentBodyPart);
                // thêm file đính kèm
                MimeBodyPart attach = new MimeBodyPart();
                attach.attachFile(attachfile);
                multipart.addBodyPart(attach);
                message.setContent(multipart);
                // gửi message
                Transport.send(message);
            }
            System.out.println("Đã gửi mail thành công!");
        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        String USERNAME = args[0];
        frame = new JFrame(USERNAME);
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setContentPane(new SendMail().panel2);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
