import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShowMail {
    private JPanel panel1;
    private JTextField fromtxt;
    private JTextField subjecttxt;
    private JButton button1;
    private Message message;
    private JEditorPane editorPane1;
    private JLabel lfiles;
    JFrame frame;
    private ArrayList<File> Files;

    public ShowMail(String sFrom, String sSubject, String body, Message message) {
        try {
            Image img = ImageIO.read(getClass().getResource("icons/Save24.gif"));
            button1.setIcon(new ImageIcon(img));
            button1.setMargin(new Insets(0, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fromtxt.setText(sFrom);
        subjecttxt.setText(sSubject);
        editorPane1.setContentType("text/html; charset=UTF-8");
        editorPane1.setText(body);
        fromtxt.setEditable(false);
        subjecttxt.setEditable(false);
        editorPane1.setEditable(false);
        this.message = message;
        button1.setVisible(false);
        lfiles.setVisible(false);

        frame = new JFrame(sSubject);
        frame.setPreferredSize(new Dimension(900, 700));
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        showSaveFileButton();
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAttachment(message);
            }
        });
    }

    public void showSaveFileButton() {
        String contentType = "";
        String messageContent = "";
        String attachFiles = "";
        try {
            contentType = message.getContentType();
            messageContent = "";

            // lưu trữ tên file đính kèm, được phân tách bằng dấu phẩy
            attachFiles = "";

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) message.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        button1.setVisible(true);
                        lfiles.setVisible(true);
                        String fileName = part.getFileName();
                        attachFiles += fileName + ", ";
                    }
                }
            }
            if(!attachFiles.equalsIgnoreCase("")) {
                attachFiles = attachFiles.substring(0,attachFiles.length()-2);
                lfiles.setText(attachFiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAttachment(Message message) {
        String contentType = "";
        String messageContent = "";
        String attachFiles = "";
        Files = new ArrayList<File>();
        try {
            contentType = message.getContentType();
            messageContent = "";

            // lưu trữ tên file đính kèm, được phân tách bằng dấu phẩy
            attachFiles = "";

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) message.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        // phần này là file đính kèm
                        String fileName = part.getFileName();
                        attachFiles += fileName + ", ";

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Specify a file to save");
                        fileChooser.setSelectedFile(new File(part.getFileName()));
                        int userSelection = fileChooser.showSaveDialog(frame);
                        File fileToSave = new File(part.getFileName());
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            fileToSave = fileChooser.getSelectedFile();
                            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                        }

                        InputStream is = part.getInputStream();
                        File f = new File(fileToSave.getAbsolutePath());
                        FileOutputStream fos = new FileOutputStream(f);
                        byte[] buf = new byte[4096];
                        int bytesRead;
                        while((bytesRead = is.read(buf))!=-1) {
                            fos.write(buf, 0, bytesRead);
                        }
                        fos.close();
                        Files.add(f);
                    } else {
                        // phần này có thể là nội dung message .. Chưa test
                        messageContent = part.getContent().toString();
                    }
                }

                if (attachFiles.length() > 1) {
                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = message.getContent();
                if (content != null) {
                    messageContent = content.toString();
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(!Files.isEmpty()) {
            button1.setVisible(true);
        }

    }
}
