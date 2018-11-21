import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Account {

	String address;
	String type;
	String[] labels = { "INBOX" };

	Account(String address, String type) {
		this.address = address;
		this.type = type;
	}

	Account(String address, String type, String[] labels) {
		this.address = address;
		this.type = type;
		this.labels = labels;
	}

	Account(ResultSet r) throws SQLException {
		address = r.getString("Address");
		type = r.getString("Type");
		labels = r.getString("Labels").split(" ");
	}

	public JsonElement toJson() {
		Gson g = new Gson();
		return g.toJsonTree(this);
	}
	
	public String getLabels() {
		return String.join(" ", labels);
	}
}
