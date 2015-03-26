import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Client {
	public static Connection con;
	public static Statement statement;
	public static PreparedStatement prepStatement;

	public static void main(String[] args)
	{
		Boolean shouldContinue = true;

		// CONNECT TO POSTGRESQL JDBC
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostgeSQL JDBC Driver not found.");
			e.printStackTrace();
			return;
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:db2://db2:50000/cs421", "cs421g45", "joidpehejy");
		} catch (SQLException e) {
			System.out.println("Connection failed! Check output console.");
			e.printStackTrace();
		}

		if (connection == null) {
			System.out.println("Something went wrong. Aborting.");
		}

		// Set class object
		con = connection;

		System.out.println("============================");
		System.out.println("======== WELCOME! ==========");
		System.out.println("============================\n");

		do {
		// Display menu graphics
		System.out.println("| Options:                 ");
		System.out.println("|        1. List customers who want discounted books.");
		System.out.println("|        2. List of publisher contacts and the number of books to order.");
		System.out.println("|        3. Update the price for all books for a publisher.");
		System.out.println("|        4. Remove a book from the listing");
		System.out.println("|        5. Add a new publisher.");
		System.out.println("|        6. Exit");
		System.out.print("$ ");
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();

		try {
			// Switch construct
			switch (choice) {
			case 1:
			  option1();
			  break;
			case 2:
			  option2();
			  break;
			case 3:
			  option3();
			  break;
			case 4:
				option4();
				break;
			case 5:
				option5();
				break;
			default:
			  shouldContinue = false;
			  break; // This break is not really necessary
			}
		} catch(SQLException e) {
			System.out.println("SQL Exception.");
			if (con != null) {
            try {
                System.err.print("Transaction is being rolled back");
                con.rollback();
            } catch(SQLException excep) {
                JDBCTutorialUtilities.printSQLException(excep);
            }
        }
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (prepStatement != null) {
				prepStatement.close();
			}
			con.setAutocommit(true);
		}

		} while (shouldContinue);

		System.out.println("Bye.");
	  }

	public static void option1() throws SQLException {
		String query = "SELECT c.cname as customer, b.title AS discounted_title, d.amount AS discount_amount FROM customer as c INNER JOIN wants as w ON c.cid = w.cid INNER JOIN discounts as d ON d.isbn = w.isbn INNER JOIN books as b ON w.isbn = b.isbn WHERE d.until > current_date ORDER BY b.title;";
		System.out.println("Query: " + query);

		// Create statement
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery(query);
		while (rs.next()) {
			String name = rs.getString(1);
			String title = rs.getString(2);
			int discount = rs.getInt(3);
			System.out.println("name: " + name + " \t title: " + title + " \tdiscount: " + discount);
		}
		System.out.println("DONE.");
	}

	public static void option2() throws SQLException{
		String query = "SELECT p.pname AS publisher, p.phone_number, p.address, count(*) AS books_to_order FROM publisher as p INNER JOIN publishes as pb ON p.pid = pb.pid INNER JOIN (SELECT isbn FROM books where qty_stock = 0) as no_stock ON pb.isbn = no_stock.isbn GROUP BY p.pname, p.phone_number, p.address;";
		System.out.println("Query: " + query);

		// Create statement
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery(query);

		while (rs.next()) {
			String name = rs.getString(1);
			Integer phone = rs.getInt(2);
			String address = rs.getString(3);
			Integer count = rs.getInt(4);
			System.out.println("publisher: " + name + "\tphone: " + phone + "\taddress: " + address + "\tcount: " + count);
		}

		System.out.println("DONE.");

	}

	public static void option3() throws SQLException {
		String query = "SELECT pname FROM publisher;";

		// Create statement
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery(query);
		System.out.println("Publishers: ");
		while (rs.next()) {
			String name = rs.getString(1);
			System.out.println(name);
		}

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the publisher name: ");
		String name = scanner.nextLine();
		System.out.print("Enter amount to increase by: ");
		Integer amount = scanner.nextInt();

		String update= "UPDATE books as B SET price = B.price + ? WHERE B.isbn IN (SELECT P2.isbn FROM publisher P INNER JOIN publishes P2 ON P.pid = P2.pid AND P.pname = ?);";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(update);
		prepStatement.setInt(1, amount);
		prepStatement.setString(2, name)
		con.commit();

		System.out.println("DONE.");
	}

	public static void option4() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the ISBN of the book to remove: ");
		Integer isbn = scanner.nextInt();

		String remove = "DELETE FROM books WHERE isbn=?";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(remove);
		prepStatement.setString(1, isbn);
		con.commit();

		System.out.println("DONE.");
	}

	public static void option5() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter publisher id (larger than 10): ");
		Integer id = scanner.nextInt();
		System.out.print("Enter publisher name: ");
		String name = scanner.nextLine();
		System.out.print("Enter publisher phone number: ");
		Integer phone = scanner.nextInt();
		System.out.print("Enter publisher address: ");
		String address = scanner.nextLine();

		String insert = "INSERT INTO publisher VALUES(?, ?, ?, ?);";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(insert);

		prepStatement.setInt(1, id);
		prepStatement.setString(2, name);
		prepStatement.setInt(3, phone)
		prepStatement.setString(4, address);
		con.commit();

		System.out.println("DONE.");
	}
}
