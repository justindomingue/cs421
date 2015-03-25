import java.sql.Connection;
import java.sql.DriverManager;
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
			return;
		}
		
		if (connection == null) {
			System.out.println("Something went wrong. Aborting.");
			return;
		}
		
		// Set class object
		con = connection;
		
		do {
	    // Display menu graphics
	    System.out.println("============================");
	    System.out.println("|     MENU SELECTION       ");
	    System.out.println("============================");
	    System.out.println("| Options:                 ");
	    System.out.println("|        1. Option 1       ");
	    System.out.println("|        2. Option 2       ");
	    System.out.println("|        3. Option 2       ");
	    System.out.println("|        4. Option 2       ");
	    System.out.println("|        5. Option 2       ");
	    System.out.println("|        6. Exit           ");
	    System.out.println("============================");
	    
	    Scanner scanner = new Scanner(System.in);
	    int choice = scanner.nextInt();
	    
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
		} while (shouldContinue);
	  }
	
	public static void option1() { }
	public static void option2() {}
	public static void option3() {}
	public static void option4() {}
	public static void option5() {}
	}
