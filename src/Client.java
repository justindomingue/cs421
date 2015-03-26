import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Client {
	public static Connection con;
	public static Statement statement;
	public static PreparedStatement prepStatement;

	public static void main(String[] args) throws SQLException
	{
		Boolean shouldContinue = true;

		// CONNECT TO POSTGRESQL JDBC
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("PostgeSQL JDBC Driver not found.");
			e.printStackTrace();
			return;
		}
				
		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://db2/CS421", "cs421g45", "joidpehejy");
		} catch (SQLException e) {
			System.err.println("Connection failed! Check output console.");
			e.printStackTrace();
			return;
		}

		if (connection == null) {
			System.err.println("Something went wrong. Aborting.");
			return;
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
			System.err.println("SQL Exception.");
            e.printStackTrace();
            
			if (con != null) {
            try {
                System.err.println("Transaction is being rolled back");
                con.rollback();
            } catch(SQLException excep) {
            }
        }
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (prepStatement != null) {
				prepStatement.close();
			}
			con.setAutoCommit(true);
		}

		} while (shouldContinue);

		con.close();
		
		System.out.println("Closed all connections.\nHave a good day!");
	  }

	public static void option1() throws SQLException {
		String query = "SELECT c.cname as customer, b.title AS discounted_title, d.amount AS discount_amount FROM customer as c INNER JOIN wants as w ON c.cid = w.cid INNER JOIN discounts as d ON d.isbn = w.isbn INNER JOIN books as b ON w.isbn = b.isbn WHERE d.until > current_date ORDER BY b.title;";
		System.out.println("Query: " + query);

		// Create statement
		statement = con.createStatement();
		ResultSet rs = statement.executeQuery(query);
		while (rs.next()) {
			String name = rs.getString(1);
			String title = rs.getString(2);
			int discount = rs.getInt(3);
			System.out.println("name: " + name + " | title: " + title + " | discount: " + discount + '%');
		}
		System.out.println("DONE.");
	}

	public static void option2() throws SQLException{
		String query = "SELECT p.pname AS publisher, p.phone_number, p.address, count(*) AS books_to_order FROM publisher as p INNER JOIN publishes as pb ON p.pid = pb.pid INNER JOIN (SELECT isbn FROM books where qty_stock = 0) as no_stock ON pb.isbn = no_stock.isbn GROUP BY p.pname, p.phone_number, p.address;";
		System.out.println("Query: " + query);

		// Create statement
		statement = con.createStatement();
		ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			String name = rs.getString(1);
			String phone = rs.getString(2);
			String address = rs.getString(3);
			Integer count = rs.getInt(4);
			System.out.println("publisher: " + name + "\tphone: " + phone + "\taddress: " + address + "\tcount: " + count);
		}

		System.out.println("DONE.");

	}

	public static void option3() throws SQLException {
		String query = "SELECT pname FROM publisher;";

		// Display publisher names
		statement = con.createStatement();
		ResultSet rs = statement.executeQuery(query);
		System.out.println("Publishers: ");
		while (rs.next()) {
			String name = rs.getString(1);
			System.out.println(name);
		}

		// Update price
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the publisher name: ");
		String name = scanner.nextLine();
		System.out.print("Enter amount to increase by: ");
		Integer amount = scanner.nextInt();

		String update= "UPDATE books as B SET price = B.price + ? WHERE B.isbn IN (SELECT P2.isbn FROM publisher P INNER JOIN publishes P2 ON P.pid = P2.pid AND P.pname = ?);";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(update);
		prepStatement.setInt(1, amount);
		prepStatement.setString(2, name);
		prepStatement.executeUpdate();
		con.commit();

		con.setAutoCommit(true);
		
		// Show result
		String displayResult = "SELECT title, price FROM books B WHERE B.isbn IN (SELECT P2.isbn FROM publisher P INNER JOIN publishes P2 ON P.pid = P2.pid AND P.pname = '" + name + "');";
		statement = con.createStatement();
		rs = statement.executeQuery(displayResult);
		while (rs.next()) {
			name = rs.getString(1);
			Integer price = rs.getInt(2);
			System.out.println("name: " + name + "\tprice: " + price);
		}
		
		System.out.println("DONE.");
	}

	public static void option4() throws SQLException {
		// Display book names
		String query = "SELECT isbn, title FROM books;";
		statement = con.createStatement();
		ResultSet rs = statement.executeQuery(query);
		System.out.println("Books: ");
		while (rs.next()) {
			Integer isbn = rs.getInt(1);
			String title = rs.getString(2);
			System.out.println("name: " + isbn + " title: " + title);
		}

		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the ISBN of the book to remove: ");
		Integer isbn = scanner.nextInt();

		String remove = "DELETE FROM books WHERE isbn=?";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(remove);
		prepStatement.setString(1, isbn.toString());
		int n = prepStatement.executeUpdate();
		con.commit();
		statement = con.createStatement();
		rs = statement.executeQuery(query);
		System.out.println("\nBooks (after delete): ");
		while (rs.next()) {
			isbn = rs.getInt(1);
			String title = rs.getString(2);
			System.out.println("name: " + isbn + " title: " + title);
		}
		
		System.out.println(n + " rows changed.\n");
		System.out.println("DONE.");
	}

	public static void option5() throws SQLException {
		String query = "SELECT pname FROM publisher;";

		// Display publisher names
		statement = con.createStatement();
		ResultSet rs = statement.executeQuery(query);
		System.out.println("Current publishers: ");
		while (rs.next()) {
			String name = rs.getString(1);
			System.out.println(name);
		}

		Scanner scanner = new Scanner(System.in);
		System.out.print("\nEnter publisher id (larger than 10): ");
		Integer id = scanner.nextInt();
		System.out.print("Enter publisher name: ");
		scanner.nextLine();
		String name = scanner.nextLine();
		System.out.print("Enter publisher phone number: ");
		String phone = scanner.nextLine();
		System.out.print("Enter publisher address: ");
		String address = scanner.nextLine();

		String insert = "INSERT INTO publisher VALUES(?, ?, ?, ?);";

		con.setAutoCommit(false);
		prepStatement = con.prepareStatement(insert);

		prepStatement.setInt(1, id);
		prepStatement.setString(2, name);
		prepStatement.setString(3, phone);
		prepStatement.setString(4, address);
		prepStatement.executeUpdate();
		con.commit();

		// Display publisher names
		statement = con.createStatement();
		rs = statement.executeQuery(query);
		System.out.println("Publishers (after insert): ");
		while (rs.next()) {
			name = rs.getString(1);
			System.out.println(name);
		}

		System.out.println("DONE.");
	}
}
