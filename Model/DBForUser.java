import java.sql.*;
public class DBForUser {
    //连接到数据库的URL
    private final String DBURL = "jdbc:mysql://localhost:3306/NETWORK?useUnicode=true&characterEncoding=utf-8";
    private final String DBUSER = "dbuser";
    private final String DBPWD = "1234";
    private Connection con;
    private Statement stmt;
    private ResultSet rs;

    public DBForUser() {
        try {
            //加载驱动器, 下面的代码为加载MySQL驱动器
            Class.forName("com.mysql.jdbc.Driver");
            //注册MySQL驱动器
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            //建立数据库连接
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            //创建一个Statement 对象
            stmt = con.createStatement();
        } catch (Exception e) {
           
        }
    }

    public void  insert(String name, int age, String password, String address) {
        try {
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            stmt = con.createStatement();
            stmt.executeUpdate("insert into tbl_user(name, age, password, address) values(" + "'" + name + "'," +
                                                                                                age +  ","  +
                                                                                               "'" + password + "'," +
                                                                                               "'" + address + "')");
            stmt.close();
            con.close();
        } catch (Exception e) {
        
        }
    }

    public void  queryAll() {
        try {
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from tbl_user");
            ResultSetMetaData metaData = rs.getMetaData();
            int allColums = metaData.getColumnCount();
            for (int i = 1; i <= allColums; i++) {
                if (i > 1) System.out.print(", ");
                System.out.print(metaData.getColumnLabel(i));
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= allColums; i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(rs.getString(i));
                }
                System.out.println();
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
        
        }
    }
    public boolean  queryByNameIsTrue(String name) {
        try {
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * from tbl_user where name = " + "'" + name + "'");
            ResultSetMetaData metaData = rs.getMetaData();
            int allColums = metaData.getColumnCount();
            while (rs.next()) {
                if (name.equals(rs.getString("NAME"))) {
                    return true;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
        
        }
        return false;
    }

    public boolean  queryByNameAndPasswordIsTrue(String name, String password) {
        try {
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * from tbl_user where name = " + "'" + name + " ' AND  password = '" + password + "'" );
            System.out.println(name + " " + password);
            ResultSetMetaData metaData = rs.getMetaData();
            int allColums = metaData.getColumnCount();
            System.out.println(allColums);
            while (rs.next()) {
                if (name.equals(rs.getString("NAME")) && password.equals(rs.getString("password"))) {
                    return true;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
        
        }
        return false;
    }

    public void deleteByName(String name1) {
        try {
            con = java.sql.DriverManager.getConnection(DBURL, DBUSER, DBPWD);
            stmt = con.createStatement();
            stmt.executeUpdate("delete from tbl_user where name='" + name1 + "'");
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*
    public static void main(String args[]) throws Exception {

        DBForUser dbTester = new DBForUser();
        String name1 =new String("桥蓉".getBytes());
        int  age1 = 20;
        String password1 =new String("11122".getBytes());
        String address1 =new String("上海".getBytes());
        dbTester.insert(name1, age1, password1, address1);
        dbTester.queryAll();
        System.out.println(name1 + ":" +  dbTester.queryByNameIsTrue(name1));
        System.out.println(name1 + ":" + password1  +  dbTester.queryByNameAndPasswordIsTrue(name1, password1));
        dbTester.deleteByName(name1);
    }
*/
}
