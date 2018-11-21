import static spark.Spark.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Spark;

public class Main {

	Database db;
	String[] labels = { "INBOX", "SENT", "DRAFT", "IMPORTANT" };
	List<Account> accounts;

	public Main() {
		db = new Database();
		db.open();
		db.init();
//		db.addAccount(new Account("harindudilshan95@gmail.com", "GMAIL"));
		accounts = db.getAccounts();
	}

	public String emails(String user, String label) {
		String json = MyMessage.toJson(db.getMessages(user, label));
		return json;
	}

	public String labels(String account) {
		for (Account a : accounts) {
			if (a.address.equals(account))
				return new Gson().toJson(a.labels);
		}
		return "[]";
	}

	public String accounts() {
		JsonArray array = new JsonArray();
		for (Account a : accounts) {
			array.add(a.toJson());
		}
		return array.toString();
	}

	public void openBrowser() {
		try {
			Process oProc = Runtime.getRuntime().exec("chromium --app=http://localhost:8888 ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String addAccount(String req) {
		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(req).getAsJsonObject();
		String address = o.get("address").getAsString();
		String type = o.get("type").getAsString();
		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		if (address.matches(regex)) {
			if (type.equals("GMAIL")) {
				Account a = new Account(address, type, labels);
				db.addAccount(a);
				String url = GmailQuickstart.addAccount(address);
				JsonObject obj = new JsonObject();
				obj.addProperty("url", url);
				return obj.toString();
			}

		}
		System.out.println(req);
		return "{}";
	}

	public static void main(String[] args) {

		Main application = new Main();
		staticFiles.location("/public");
		application.openBrowser();
		Timer t = new Timer();
		t.schedule(new BackgroundTask(), 10000L, 100000L);

		port(8888);

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
		});

		get("/hello/:name", (req, res) -> {
			System.out.println("hello req");
			res.body("Hello: " + req.params(":name"));
			return "Hello: " + req.params(":name");
		});
		get("/:account/messages/:label", (req, res) -> {

			String body = application.emails(req.params(":account"), req.params(":label"));
			return body;
		});

		get("/:account/labels/", (req, res) -> {
			return application.labels(req.params(":account"));
		});

		get("/accounts/", (req, res) -> {
			return application.accounts();
		});

		post("/account/", (req, res) -> {
			String response  =application.addAccount(req.body());
			System.out.println(response);
			return response;
		});

//		Spark.stop();
//		application.db.close();
	}
}
