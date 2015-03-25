import java.util.Scanner;

public class Client {
	public static void main(String[] args)
	{
		Boolean shouldContinue = true;
		
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
