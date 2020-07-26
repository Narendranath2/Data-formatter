
public class Main {
	public static void main(String[] args) throws Exception {
		String nameToBeConsidered = "ALL_RECORDS";
		if(args.length != 0) {
			nameToBeConsidered = args[0];
		}
		// Object initializations
		StandardizeStatement ip = new StandardizeStatement(System.getProperty("user.dir") + "\\inputs\\HDFC-Input-Case1.csv", System.getProperty("user.dir") + "\\outputs\\HDFC-Output-Case1.csv", nameToBeConsidered);
		StandardizeStatement ip2 = new StandardizeStatement(System.getProperty("user.dir") + "\\inputs\\ICICI-Input-Case2.csv", System.getProperty("user.dir") + "\\outputs\\ICICI-Output-Case2.csv", nameToBeConsidered);
		StandardizeStatement ip3 = new StandardizeStatement(System.getProperty("user.dir") + "\\inputs\\Axis-Input-Case3.csv", System.getProperty("user.dir") + "\\outputs\\Axis-Output-Case3.csv", nameToBeConsidered);
		StandardizeStatement ip4 = new StandardizeStatement(System.getProperty("user.dir") + "\\inputs\\IDFC-Input-Case4.csv", System.getProperty("user.dir") + "\\outputs\\IDFC-Output-Case4.csv", nameToBeConsidered);				
		
		try {
			ip.parse();	
		} catch (Exception e) {
			System.out.println("Error 1");
		}
		try {
			ip2.parse();	
		} catch (Exception e) {
			System.out.println("Error 2");
		}
		try {
			ip3.parse();	
		} catch (Exception e) {
			System.out.println("Error 3");
		}
		try {
			ip4.parse();	
		} catch (Exception e) {
			System.out.println("Error 4");
		}
		
	}
}
