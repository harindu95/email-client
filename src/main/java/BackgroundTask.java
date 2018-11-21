import java.util.List;
import java.util.TimerTask;

public class BackgroundTask extends TimerTask{

	List<Account> accounts;
	String[] labels = { "INBOX", "SENT", "DRAFT", "IMPORTANT" };
	public BackgroundTask() {
		Database db = new Database();;
		db.open();
		accounts = db.getAccounts();
		db.close();
	}

	public  void updateAccounts() {
		for(Account a: accounts) {
			if(a.type.equals("GMAIL"))
				GmailQuickstart.updateGmail(a.address, labels);
		}
	}
	
	@Override
	public void run() {
		System.out.println("Update accounts");
		updateAccounts();
		
	}
}
