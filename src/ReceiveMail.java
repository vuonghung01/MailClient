
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
/*
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.io.FileNotFoundException;
 */
public class ReceiveMail {
    private Frame frame;
    private JPanel panel1;
    private JButton gửiThưButton;
    private JButton làmMớiButton;
    private JTable table1;
    private DefaultTableModel allTableModel;
    private ArrayList messageList = new ArrayList();
    private Message selectedMessage;

    public ReceiveMail()
    {
        try {
            Image img = ImageIO.read(getClass().getResource("icons/ComposeMail24.gif"));
            gửiThưButton.setIcon(new ImageIcon(img));
            gửiThưButton.setMargin(new Insets(0, 0, 0, 0));
            img = ImageIO.read(getClass().getResource("icons/Refresh24.gif"));
            làmMớiButton.setIcon(new ImageIcon(img));
            làmMớiButton.setMargin(new Insets(0, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeTable();
        gửiThưButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String[] args = {"Gửi thư"};
                SendMail.main(args);
            }
        });
        try
        {
            connect();
            JOptionPane.showMessageDialog(frame,
                    "Load mail thành công!");
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(frame,
                    "Lỗi: " + ex.getMessage(),
                    "Thông báo", JOptionPane.ERROR_MESSAGE);
        }
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                super.mousePressed(me);
                if(me.getClickCount() == 1)
                {
                    selectedMessage = getMessage(table1.getSelectedRow());
                }
                if (me.getClickCount() == 2)
                {
                    try
                    {
                        selectedMessage = getMessage(table1.getSelectedRow());
                        String[] args = {String.valueOf((InternetAddress) selectedMessage.getFrom()[0]),
                                "" + selectedMessage.getSubject(),
                                "" + getText(selectedMessage)};
                        ShowMail sm = new ShowMail(args[0], args[1], args[2], selectedMessage);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        làmMớiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageList.clear();
                initializeTable();
                try {
                    connect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private boolean textIsHtml = false;
    // Tham khảo từ JavaMail FAQ : https://javaee.github.io/javamail/FAQ#mainbody
    private String getText(Part p) throws
            MessagingException, IOException
    {
        if (p.isMimeType("text/*"))
        {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative"))
        {
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain"))
                {
                    if (text == null)
                        text = getText(bp);
                    continue;
                }
                else if (bp.isMimeType("text/html"))
                {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                }
                else
                {
                    return getText(bp);
                }
            }
            return text;
        }
        else if (p.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++)
            {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }
    public void connect() throws IOException
    {
        String pop3Host = "pop.gmail.com";
        String mailStoreType = "pop3s";

        Properties prop = new Properties();

        prop.load(new FileInputStream("gmailConfig.properties"));
        final String username = prop.getProperty("gmail.user");
        final String password = prop.getProperty("gmail.password");

        receiveEmail(pop3Host, mailStoreType, username, password);
    }

    public void receiveEmail(String pop3Host,String storeType, String user, String password)
    {
        Properties props = new Properties();
        props.put("mail.pop3.host", pop3Host);
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.starttls.enable", "true");
        props.put("mail.store.protocol", "pop3");

        Session session = Session.getInstance(props);
        try
        {
            Store mailStore = session.getStore(storeType);
            mailStore.connect(pop3Host, user, password);

            Folder folder = mailStore.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] emailMessages = folder.getMessages();
            System.out.println("Tổng số thư - "
                    + emailMessages.length);

            // Add mail nhận được vào table
            DefaultTableModel modd = (DefaultTableModel) table1.getModel();
            modd.setRowCount(0);
            initializeTable();
            for (int i = 0; i < emailMessages.length; i++) {
                Message message = emailMessages[i];
                Object[] row = {message.getFrom()[0], message.getSubject(), message.getSentDate()};
                modd.addRow(row);
                messageList.add(message);
                /*/ Debug ra thông tin mail nhận được bao gồm Người gửi, Nội dung,...
                Address[] toAddress = message.getRecipients(Message.RecipientType.TO);
                System.out.println();
                System.out.println("Email " + (i+1) + "-");
                System.out.println("Subject - " + message.getSubject());
                System.out.println("From - " + message.getFrom()[0]);

                System.out.println("To - ");
                for(int j = 0; j < toAddress.length; j++){
                    System.out.println(toAddress[j].toString());
                }
                System.out.println("Text - " +
                        message.getContent().toString());*/
            }
            table1.setModel(modd);

            //folder.close(false); Tắt để đọc mail
            //mailStore.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi nhận email.");
        }
    }

    public Message getMessage(int row) {
        return (Message) messageList.get(row);
    }

    public static void main(String[] args) {
        String USERNAME = args[0];
        JFrame frame = new JFrame(USERNAME);
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        bar.add(menu);
        JMenuItem report = new JMenuItem("Báo cáo và góp ý");
        menu.add(report);
        JMenuItem logout = new JMenuItem("Đăng xuất");
        menu.add(logout);
        frame.setJMenuBar(bar);
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                super.mousePressed(me);
                frame.dispose();
                Login login = new Login();
            }
        });
        report.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Report.showReport();
            }
        });
        frame.setPreferredSize(new Dimension(1150, 800));
        frame.setContentPane(new ReceiveMail().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void initializeTable() {
        String col[] = {"Người gửi","Chủ đề", "Ngày"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table1.setModel(tableModel);
    }
}
