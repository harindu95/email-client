import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyMessage {

	List<String> labels;
	String snippet;
	String from;
	String to;
	String subject;
	String body = "";
	String id;
	String threadId;

	public MyMessage(Message m) throws IOException {

		String raw = m.getPayload().toString();
		labels = m.getLabelIds();

		snippet = m.getSnippet();
		threadId = m.getThreadId();
		id = m.getId();

		byte[] bytes = m.getPayload().getBody().decodeData();

		if (bytes == null) {
			for (MessagePart p : m.getPayload().getParts()) {
				if (p.getMimeType().indexOf("text/html") > -1) {
					bytes = p.getBody().decodeData();
					break;
				}
			}
		}
		if(bytes != null)
			body = new String(bytes);

		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(raw).getAsJsonObject();
		JsonArray headers = o.get("headers").getAsJsonArray();

		for (JsonElement i : headers) {
			JsonObject object = i.getAsJsonObject();
			String name = object.get("name").getAsString().toLowerCase();
			String value = object.get("value").getAsString();
			switch (name) {

			case "subject":
				subject = value;
				break;
			case "from":
				from = value;
				break;
			case "to":
				to = value;
				break;
			default:
				break;
			}
		}

	}

	public MyMessage(ResultSet r) {

		try {

			id = r.getString("ID");
			from = r.getString("From");
			to = r.getString("To");
			body = r.getString("Body");
			snippet = r.getString("Snippet");
			labels = Arrays.asList(r.getString("LabelIds").split(" "));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//	

	}

	static String toJson(List<MyMessage> list) {
		if(list == null)
			return "[]";
		JsonArray array = new JsonArray();
		for (MyMessage m : list) {
			array.add(m.toJson());
		}
		return array.toString();
	}

	JsonElement toJson() {
		Gson g = new Gson();
		return g.toJsonTree(this);
	}

	String getLabelIds() {
		String labelIds = "";
		for (int i = 0; i < labels.size(); i++) {
			labelIds += labels.get(i) + " ";
		}
		return labelIds.trim();
	}

	public String toString() {
		return String.format("From: %s \n" + "To: %s\n" + "Subject: %s\n" + "Snippet: %s\n", from, to, subject,
				snippet);
	}

}
