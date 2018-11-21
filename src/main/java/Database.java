
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
	String name = "emails";
	Connection c = null;
	Statement stmt = null;

	public void open() {

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
			stmt = c.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
		System.out.println("Opened database successfully");
	}

	public void insert(String user, MyMessage m) {

		try {

			String sql = "INSERT INTO '" + user + "' (ID,Subject,'From','To',Snippet, Body, LabelIds) "
					+ "VALUES (?, ?, ? , ?, ? , ? , ?);";

			PreparedStatement s = c.prepareStatement(sql);
			s.setString(1, m.id);
			s.setString(2, m.subject);
			s.setString(3, m.from);
			s.setString(4, m.to);
			s.setString(5, m.snippet);
			s.setString(6, m.body);
			s.setString(7, m.getLabelIds());
			s.executeUpdate();

		} catch (java.sql.SQLException e) {
			if (e.getErrorCode() == 19) {
				// Record already exists.
			} else {
				System.err.println("Insert error: SQL error" + e.getMessage() + " " + e.getErrorCode());
				e.printStackTrace();
			}
		}

	}

	public MyMessage getMessage(String user, String msgId) {

		try {

			String sql = "select * from '" + user + "' where ID=?;";
			PreparedStatement s = c.prepareStatement(sql);
			s.setString(1, msgId);
			ResultSet r = s.executeQuery();

			while (r.next()) {
				MyMessage m = new MyMessage(r);
				return m;
			}

		} catch (java.sql.SQLException e) {
			if (e.getErrorCode() == 19) {
				// Record already exists.
			} else {
				System.err.println(
						"GetMessage error: SQL error " + e.getMessage() + " " + e.getErrorCode() + " | " + msgId);
				e.printStackTrace();
			}
		}
		return null;

	}

	public void createAccountTable(String user) {
		String sql = "CREATE TABLE IF NOT EXISTS '" + user
				+ "' ( `ID` TEXT, `Subject` TEXT, `From` TEXT, `To` TEXT, `Snippet` TEXT, `Body` TEXT, `LabelIds` TEXT, PRIMARY KEY(`ID`) );";
		try {
			PreparedStatement s = c.prepareStatement(sql);

			s.executeUpdate();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	public void addAccount(Account a) {
		createAccountTable(a.address);
		String sql = "INSERT INTO 'Accounts' (Address, Type, Labels) VALUES ( ? , ? , ?) ;";
		try {
			PreparedStatement s = c.prepareStatement(sql);
			s.setString(1, a.address);
			s.setString(2, a.type);
			s.setString(3, a.getLabels());
			s.executeUpdate();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Account> getAccounts() {
		List<Account> list = new ArrayList<>();
		try {
			String sql = "select * from Accounts;";
			PreparedStatement s = c.prepareStatement(sql);
			ResultSet r = s.executeQuery();

			while (r.next()) {
				Account a = new Account(r);
				list.add(a);

			}
		} catch (java.sql.SQLException e) {
			System.err.println("Get Accounts error: SQL error " + e.getMessage() + " " + e.getErrorCode());
			e.printStackTrace();
		}
		return list;
	}

	public List<MyMessage> getMessages(String user, String label) {
		try {

			String sql = "select * from '" + user + "' where LabelIds like ?;";
			PreparedStatement s = c.prepareStatement(sql);

			s.setString(1, "%" + label + "%");
			ResultSet r = s.executeQuery();
			List<MyMessage> list = new ArrayList<>();
			while (r.next()) {
				MyMessage m = new MyMessage(r);
				list.add(m);
			}

			return list;

		} catch (java.sql.SQLException e) {
			if (e.getErrorCode() == 19) {
				// Record already exists.
			} else {
				System.err.println("GetMessage error: SQL error " + e.getMessage() + " " + e.getErrorCode());
				e.printStackTrace();
			}
		}
		return null;
	}

	public void close() {
		try {
			stmt.close();
			c.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Database closed.");
	}

	public void init() {
		String sql = "CREATE TABLE IF NOT EXISTS \"Accounts\" ( `Address` TEXT, `Type` TEXT, `Labels` INTEGER, PRIMARY KEY(`Address`) )";
		PreparedStatement s;
		try {
			s = c.prepareStatement(sql);
			s.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}