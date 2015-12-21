import java.awt.*;
import java.awt.event.*;
import javax.accessibility.*;
import java.io.*;
import javax.swing.*;

public class Client extends JFrame implements ActionListener{
    private static final long serialVersionUID = 7526471155622776147L;
    private JLabel nameLabel ,passwordLabel; //定义用户名和密码的标签
    private JTextField nameTextField; //用户名的文本框
    private JPasswordField passwordField; //定义密码的文本框。注：使用JPasswordField的好处就是输入的密码是以****显示给用户的
    private JPanel loginPanel; //定义面板
    private JButton login, register; //定义登陆和注册的按钮
    
    private boolean b=false;
    private File file1;    //定义记录用户注册的用户名和密码的文件夹
    private Writer writer;
    private String []Usersname=new String[1024]; //定义记录用户名的数组
    private String []Userspassword=new String[1024];//定义记录密码的数组

    public Client(){
        loginPanel = new JPanel();
        nameLabel = new JLabel ("用户名:");
        //nameLabel.setBackground(Color.red);//设置标签的背景颜色
        passwordLabel = new JLabel ("密码:");
        //passwordLabel.setBackground(Color.red);//设置标签的背景颜色
        nameTextField = new JTextField (10); 
        passwordField = new JPasswordField (10);
        login = new JButton("登陆");
        login.setBackground(Color.lightGray);
        register = new JButton("注册");
        register.setBackground(Color.lightGray);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        //loginPanel.setLayout(new GridLayout(10,3));
        this.setContentPane(loginPanel);
        GridBagLayout gridBag = new GridBagLayout();
        loginPanel.setLayout(gridBag);
        loginPanel.add(nameLabel);
        loginPanel.add(nameTextField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(login);
        loginPanel.add(register);
        
        JLabel username_detail = new JLabel("                         ");
        loginPanel.add(username_detail);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.weightx = 1;
        c2.insets = new Insets(10,10,10,10); //设置组件之间彼此的间距 它有四个参数，分别是上，左，下，右，默认为(0,0,0,0)
        c2.fill = GridBagConstraints.BOTH;
        c2.gridx = 0;
        c2.gridy = 0;
        gridBag.setConstraints(nameLabel,c2);
        c2.gridx = 1;
        c2.gridy = 0;
        gridBag.setConstraints(nameTextField, c2);
        c2.gridx = 2;
        c2.gridy = 0;
        c2.insets = new Insets(10,30,10,10); //设置组件之间彼此的间距 它有四个参数，分别是上，左，下，右，默认为(0,0,0,0)
        gridBag.setConstraints(username_detail,c2);
        c2.insets = new Insets(10,10,10,10); //设置组件之间彼此的间距 它有四个参数，分别是上，左，下，右，默认为(0,0,0,0)
        // 可以中途修改 c2.insets = new Insets(20,20,20,20); //设置组件之间彼此的间距 它有四个参数，分别是上，左，下，右，默认为(0,0,0,0)
        c2.gridx = 0;
        c2.gridy = 1;
        loginPanel.add(passwordLabel, c2);
        c2.gridx = 1;
        c2.gridy = 1;
        loginPanel.add(passwordField, c2);
        c2.gridx = 0;
        c2.gridy = 2;
        c2.gridwidth = 1; // 默认都是1，而且是以整体看齐的
        loginPanel.add(register, c2);
        register.addActionListener(this);
        c2.ipadx = 0;
        c2.gridx = 1;
        c2.gridy = 2;
        c2.gridwidth = 2;
        loginPanel.add(login, c2);
        login.addActionListener(this);
        //添加关闭窗口的事件
        WindowListener wl = new WindowAdapter() {
            public void windowClosing(WindowEvent  e) {
                System.exit(0);
            }
        };
        this.addWindowListener(wl);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){ 
        // 用户登录事件处理
        if (e.getSource() == login) {
            String username = nameTextField.getText();
            String password = String.copyValueOf(passwordField.getPassword());
            boolean flag = false;
            int index = 0;
            DBForUser dbForUser = new DBForUser();
            //System.out.println(username);
            dbForUser.queryAll();
            //System.out.println(password);
            if (dbForUser.queryByNameAndPasswordIsTrue(username, password)) {   
                //处理用户登录成功后的反馈信息
                JOptionPane.showMessageDialog(null, "恭喜您登陆成功!", "消息", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "对不起您的用户名或密码错误!", "错误",JOptionPane.ERROR_MESSAGE);
            }
        }

        // 用户注册模块的事件处理
        if (e.getSource() == register) {
            String NCmp = nameTextField.getText();
            String PCmp = String.copyValueOf(passwordField.getPassword());
            boolean flag = false;
            for (int i = 0; i < Usersname.length; i++) {
                if (NCmp.equals(Usersname[i])) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                JOptionPane.showMessageDialog(null, "对不起您的用户名已经注册!", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                int index = 0;
                JOptionPane.showMessageDialog(null, "注册成功!", "消息", JOptionPane.INFORMATION_MESSAGE);
                for (int i = 0; i < Usersname.length; i++) {
                    if (Usersname[i] == null) {
                        Usersname[i] = NCmp;
                        index = i;
                        break;
                    }
                }
                Userspassword[index] = PCmp;
            }
            try{
                file1=new File("Student.dat");
                FileWriter fw=new FileWriter(file1,true); 
                fw.write("用户名"+"\t\t"+"密码"+"\n");
                fw.write(NCmp+"\t\t"+PCmp+"\n");    
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }    
        }
    } 
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Frame Fstudent=new Client();
//        Fstudent.setSize(200,200);

    }
}
