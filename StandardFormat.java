import java.util.Map;

public class StandardFormat {
	// Output record format
	private String date;
	private String transDesc;
	private String debit;
	private String credit;
	private String currency;
	private String cardName;
	private String transType;
	private String location;

	public StandardFormat(Map transaction) {
		date = transaction.get("date").toString();
		transDesc = transaction.get("transDesc").toString();
		debit = transaction.get("debit").toString();
		credit = transaction.get("credit").toString();
		currency = transaction.get("currency").toString();
		cardName = transaction.get("cardName").toString();
		transType = transaction.get("transType").toString();
		location = transaction.get("location").toString();
	}

	@Override
	public String toString() {
		return date + "," + transDesc + "," + debit + "," + credit + "," + currency + "," + cardName + ","
				+ transType + "," + location + "\n";
	}

	public String getDate() {
		return date;
	}

	public String getTransDesc() {
		return transDesc;
	}

	public String getDebit() {
		return debit;
	}

	public String getCredit() {
		return credit;
	}

	public String getCurrency() {
		return currency;
	}

	public String getCardName() {
		return cardName;
	}

	public String getTransType() {
		return transType;
	}

	public String getLocation() {
		return location;
	}
}
