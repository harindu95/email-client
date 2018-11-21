
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users.Messages;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListDraftsResponse;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GmailQuickstart {
	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_MODIFY);
	private static final String CREDENTIALS_FILE_PATH = "credentials.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receier).authorize("user");
	}
	
	private static Credential getCredentialsDB(String user, final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8889).build();
		
		return new AuthorizationCodeInstalledApp(flow, receier).authorize(user);
	}

	public static String addAccount(String user){
		NetHttpTransport HTTP_TRANSPORT;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			// Build flow and trigger user authorization request.
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					clientSecrets, SCOPES)
							.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
							.setAccessType("offline").build();
			LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8889).build();
			
			Credential cred = new AuthorizationCodeInstalledApp(flow, receier).authorize(user);
		
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
					.setApplicationName(APPLICATION_NAME).build();
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "https://accounts.google.com/signin/oauth?client_id=550881935631-uvor0tkf1r1k9vtrqqfprh4p526u3eb3.apps.googleusercontent.com&as=YePpDLynNsR5hP0SygkPkw&destination=http://localhost:8889&approval_state=!ChRmZEM3LS0tQ1dBM2JZb3lvbTU0MBIfQV9kMTl4YngyUDBmSU5vbEVpbVNxcUVWcGFMM2NoWQ%E2%88%99APNbktkAAAAAW_Tlgj5O4KxzE1a1eDFE9L3VJzLusFiU&xsrfsig=AHgIfE9bxlGPAn-vfwjPdTPndqXhKIwAow";
		
		
	}
	
	public static void updateGmail(String user, String[] labels) {

		for (String l : labels) {
			try {
				final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
							
				Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentialsDB(user, HTTP_TRANSPORT))
						.setApplicationName(APPLICATION_NAME).build();

				List<String> labelIdsList = Arrays.asList(l);
				ListMessagesResponse listResponse = service.users().messages().list(user).setLabelIds(labelIdsList)
						.setMaxResults(100L).execute();

				List<Message> messages = listResponse.getMessages();

				Database db = new Database();
				db.open();

				if (messages != null) {

					System.out.println("Number of emails: " + messages.size());
					for (Message msg : messages) {
						MyMessage s = db.getMessage(user, msg.getId());
						if (s == null) {
							Message message = service.users().messages().get(user, msg.getId()).setFormat("full")
									.execute();
							s = new MyMessage(message);
							db.insert(user, s);
						}

					}
				}
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

//	public static void main(String... args) throws IOException, GeneralSecurityException {
//		// Build a new authorized API client service.
//		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//				.setApplicationName(APPLICATION_NAME).build();
//
//		// Print the labels in the user's account.
//		String user = "me";
//		List<String> labelIdsList = new ArrayList<>();
//		labelIdsList.add("INBOX");
//		ListMessagesResponse listResponse = service.users().messages().list(user).setLabelIds(labelIdsList)
//				.setMaxResults(100L).execute();
//		ListLabelsResponse labelResponse = service.users().labels().list(user).execute();
//
//		List<Label> labels = labelResponse.getLabels();
//		List<Message> messages = listResponse.getMessages();
//
//		Map<String, Label> labelMap = new HashMap<>();
//		for (Label l : labels) {
//			labelMap.put(l.getId(), l);
//		}
//
//		Database db = new Database();
//		db.open();
//
//		if (messages != null) {
//
//			System.out.println("Number of emails: " + messages.size());
//			for (Message msg : messages) {
//				MyMessage s = db.getMessage(msg.getId());
//				if (s == null) {
//					Message message = service.users().messages().get(user, msg.getId()).setFormat("full").execute();
//					s = new MyMessage(message);
//					System.out.println(s.getLabelIds());
//					db.insert(s);
//				}
//
//			}
//		}
//		db.close();
//	}
	
	
}
