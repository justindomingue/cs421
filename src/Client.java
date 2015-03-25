import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Client {
	public static Connection con;
	
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
	    System.out.println("|        2. Option 2       ");
	    System.out.println("|        3. Option 2       ");
	    System.out.println("|        4. Option 2       ");
	    System.out.println("|        5. Option 2       ");
	    System.out.println("|        6. Exit           ");
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
	    	e.printStackTrace();
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
	
	public static void option2() {
		String query = "SELECT p.pname AS publisher, p.phone_number, p.address, count(*) AS books_to_order FROM publisher as p INNER JOIN publishes as pb ON p.pid = pb.pid INNER JOIN (SELECT isbn FROM books where qty_stock = 0) as no_stock ON pb.isbn = no_stock.isbn GROUP BY p.pname, p.phone_number, p.address;";

	}
	public static void option3() {}
	public static void option4() {}
	public static void option5() {}
	}
