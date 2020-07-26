import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardizeStatement {

	// Constants
	static final String DOMESTIC_TRANSACTION_LABEL = "domestic transactions";
	static final String INTERNATIONAL_TRANSACTION_LABEL = "international transactions";
	static final String CREDIT_LABEL = "credit";
	static final String DEBIT_LABEL = "debit";
	static final String AMOUNT_LABEL = "amount";
	static final String TRANSACTION_DETAILS_LABEL = "transaction details";
	static final String TRANSACTION_DESCRIPTION_LABEL = "transaction description";

	private String inputPath;
	private String outputPath;
	private String nameToBeConsidered;

	// Constructor
	public StandardizeStatement(String inputPath, String outputPath, String nameToBeConsidered) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.nameToBeConsidered = nameToBeConsidered;
	}

	// Parsing the input file
	public void parse() throws Exception {
		File f = new File(inputPath);
		Scanner sc = new Scanner(f);

		String curName = null;
		String curTransType = null;
		ArrayList<String> curHeaders = null;
		ArrayList<StandardFormat> recordList = new ArrayList<>();

		while (sc.hasNext()) {
			String line = sc.nextLine();

			// Ignoring rows with no data
			if (line.split(",").length == 0)
				continue;

			// Taking action according to row type and setting up common values 
			// such as card name, transaction type, etc... to keep track off
			if (isTransactionType(line)) {
				curTransType = getTransType(line);
			} else if (isHeader(line)) {
				curHeaders = getHeaders(line);
			} else if (isCardName(line)) {
				curName = getCardName(line);
			} else if (isTransaction(line)) {
				Map<String, String> attrs = new HashMap<String, String>();
				attrs.put("cardName", curName);
				attrs.put("transType", curTransType);

				String data[] = new String[curHeaders.size()];
				String dataD[] = line.split(",");
				// System.out.println(data.length + " " + dataD.length);
				if(dataD.length < data.length){
					String lField = curHeaders.get(curHeaders.size()-1);
					if(lField.equalsIgnoreCase(DEBIT_LABEL) || lField.equalsIgnoreCase(CREDIT_LABEL) || lField.equalsIgnoreCase(AMOUNT_LABEL)){
						data[data.length-1] = "0";
					}else{
						data[data.length-1] = "";
					}
				}
				for(int i=0;i<dataD.length;i++){
					// System.out.println(dataD[i] + (dataD[i].equals("")) + "," + curHeaders.get(i));
					if((curHeaders.get(i).equalsIgnoreCase(DEBIT_LABEL) 
						|| curHeaders.get(i).equalsIgnoreCase(CREDIT_LABEL)
						|| curHeaders.get(i).equalsIgnoreCase(AMOUNT_LABEL)
						)
						&& dataD[i].trim().equals("")
					){
						data[i] = "0";
					}else{
						data[i] = dataD[i];
					}
				}

				// Processing the transaction record and cleaning up credit, debit, amount values
				for (int i = 0; i < curHeaders.size(); i++) {
					if (curHeaders.get(i).equalsIgnoreCase(TRANSACTION_DETAILS_LABEL)
							|| curHeaders.get(i).equalsIgnoreCase(TRANSACTION_DESCRIPTION_LABEL)) {
						attrs.put("transDesc", data[i]);
					} else if (curHeaders.get(i).equalsIgnoreCase(AMOUNT_LABEL)) {						
						data[i] = data[i].replaceAll("[^0-9]", "");
						attrs.put("credit", "0");
						attrs.put("debit", data[i]);
					} else {
						if (curHeaders.get(i).equalsIgnoreCase(CREDIT_LABEL)
								|| curHeaders.get(i).equalsIgnoreCase(DEBIT_LABEL)) {
							data[i] = data[i].replaceAll("[^0-9]", "");
						}						
						attrs.put(curHeaders.get(i).toLowerCase(), data[i]);
					}
				}

				String[] transDescArr = attrs.get("transDesc").split(" ");
				ArrayList<String> transList = new ArrayList<String>();
				for (int i = 0; i < transDescArr.length; i++) {
					if (!transDescArr[i].trim().equals(""))
						transList.add(transDescArr[i].trim());
				}
				transDescArr = transList.toArray(new String[0]);
				// Setting the currency and location depending on Domestic ( INR ) or International ( ex: USD, EUR, etc... )
				if (curTransType.equalsIgnoreCase(DOMESTIC_TRANSACTION_LABEL)) {
					String loc = transDescArr[transDescArr.length - 1];
					attrs.put("location", loc);
					String transDescNew = "";
					for (int i = 0; i < transDescArr.length - 1; i++) {
						transDescNew += transDescArr[i] + " ";
					}
					attrs.put("transDesc", transDescNew.trim());
					attrs.put("currency", "INR");
				} else {
					String loc = transDescArr[transDescArr.length - 2];
					attrs.put("location", loc);
					String transDescNew = "";
					for (int i = 0; i < transDescArr.length - 2; i++) {
						transDescNew += transDescArr[i] + " ";
					}

					attrs.put("transDesc", transDescNew.trim());
					attrs.put("currency", transDescArr[transDescArr.length - 1]);
				}

				// Trimming Domestic Transactions -> Domestic || International Transactions -> International
				attrs.put("transType", attrs.get("transType").split(" ")[0]);
				StandardFormat record = new StandardFormat(attrs);
				recordList.add(record);
			}

		}
		writeToFile(recordList);

		sc.close();
	}

	// Returns card name
	private String getCardName(String s) {
		String arr[] = s.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			if (arr[i].length() >= 1) {
				return arr[i];
			}
		}
		return null;
	}

	// Returns the headers ( ex: credit, debit, etc... )
	private ArrayList<String> getHeaders(String s) {
		ArrayList<String> headerList = new ArrayList<String>();
		String arr[] = s.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			headerList.add(arr[i]);
		}
		return headerList;
	}

	// Returns the transaction type ( Domestic or international )
	private String getTransType(String s) {
		String arr[] = s.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			if (arr[i].equalsIgnoreCase(DOMESTIC_TRANSACTION_LABEL)
					|| arr[i].equalsIgnoreCase(INTERNATIONAL_TRANSACTION_LABEL)) {
				return arr[i];
			}
		}
		return null;
	}

	// Checking if nth line is Transaction
	private boolean isTransaction(String line) {
		Pattern p = Pattern.compile(".*\\d{2,2}-\\d{2,2}-\\d{4,4}.*");
		Matcher m = p.matcher(line);
		return m.find();
	}

	// Checking if nth line is Header
	private boolean isHeader(String line) {
		return !isTransaction(line) && !isTransactionType(line) && !isCardName(line);
	}

	// Checking if nth line is Card name
	private boolean isCardName(String line) {
		int c = 0;
		String arr[] = line.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			if (arr[i].length() >= 1) {
				c++;
			}
		}
		return c == 1;
	}

	// Checking if nth line is Transaction type ( Domestic or International )
	private boolean isTransactionType(String line) {
		String[] arr = line.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			if (arr[i].equalsIgnoreCase(DOMESTIC_TRANSACTION_LABEL)
					|| arr[i].equalsIgnoreCase(INTERNATIONAL_TRANSACTION_LABEL)) {
				return true;
			}
		}
		return false;
	}

	// Writing the parsed data to output file
	private void writeToFile(ArrayList<StandardFormat> recordList) {
		if(nameToBeConsidered.equals("ALL_RECORDS")){
			try {
				File dir = new File(System.getProperty("user.dir") + "\\outputs");
				dir.mkdirs();
				FileWriter fw = new FileWriter(outputPath);
				fw.write("Date,Transaction Description,Debit,Credit,Currency,CardName,Transaction,Location\n");
				for (StandardFormat record : recordList) {
					fw.write(record.toString());
				}
				fw.close();
				System.out.println("File saved to : " + outputPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				File dir = new File(System.getProperty("user.dir") + "\\outputs");
				dir.mkdirs();
				FileWriter fw = new FileWriter(outputPath);
				fw.write("Date,Transaction Description,Debit,Credit,Currency,CardName,Transaction,Location\n");
				for (StandardFormat record : recordList) {
					if(record.getCardName().equalsIgnoreCase(nameToBeConsidered))
					fw.write(record.toString());
				}
				fw.close();
				System.out.println("File saved to : " + outputPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}				
	}
}