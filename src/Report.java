import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Report {
    private static JFrame frame;
    private JPanel panel1;
    private JButton báoCáoButton;
    private JEditorPane editorPane1;

    public Report() {
        báoCáoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Properties prop = new Properties();
                try {
                    prop.load(new FileInputStream("gmailConfig.properties"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                final String username = prop.getProperty("gmail.user");

                SendMail sm = new SendMail();
                try {
                    sm.SendMailInfo("nnvanloc@gmail.com", "Báo cáo và góp ý từ: " + username + " (" + java.time.LocalDate.now() + ")", editorPane1.getText(), null);
                    JOptionPane.showMessageDialog(frame,
                            "Gửi báo cáo, góp ý thành công!");
                    frame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void showReport() {
        frame = new JFrame("Báo cáo và góp ý");
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setContentPane(new Report().panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
