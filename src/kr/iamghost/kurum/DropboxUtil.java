package kr.iamghost.kurum;

import java.io.File;
import java.io.FileInputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

public class DropboxUtil {
	final static private String APP_KEY_FILE = "APIKeys.properties";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<WebAuthSession> client;
	private WebAuthSession session;
	private PropertyUtil config;
	
	public DropboxUtil() {
		config = new PropertyUtil().loadDefaultFile();
		
		PropertyUtil keyFile = new PropertyUtil().loadLocalFile(APP_KEY_FILE);
		String api_key = keyFile.getString("api_key");
		String api_secret =  keyFile.getString("api_secret");
		
		AppKeyPair appkey = new AppKeyPair(api_key, api_secret);
		session = new WebAuthSession(appkey, ACCESS_TYPE);
		client = new DropboxAPI<WebAuthSession>(session);
		
		loadSavedKeys();
	}
	
	public Entry getMetadata(String path, int fileLimit, String hash, boolean list, String rev) {
		Entry entry = null;
		
		try {
			entry = client.metadata(path, fileLimit, hash, list, rev);
		}
		catch (DropboxException e) {
			e.printStackTrace();
		}
		
		return entry;
	}
	
	public Entry upload(String path, String destPath) {
		return upload(path, destPath, null);
	}
	
	public Entry upload(String path, String destPath, String rev) {
		Entry result = null;

		try {
			File file = new File(path);
			FileInputStream is = new FileInputStream(file);
			result = client.putFile(path, is, file.length(), rev, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void loadSavedKeys() {
		PropertyUtil config = new PropertyUtil().loadDefaultFile();
		String auth_key = config.getString("oauth_key");
		String auth_secret = config.getString("oauth_secret");
		
		if (auth_key != null && auth_secret != null) {
			AccessTokenPair tokens = new AccessTokenPair(auth_key, auth_secret);
			session.setAccessTokenPair(tokens);
		}
	}
	
	public boolean isLinked() {
		return session.isLinked();
	}
	
	public Account getAccountInfo() {
		Account account = null;
		try {
			account = client.accountInfo();
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		
		return account;
	}
	
	public String requestNewToken() {
		String url = null;
		WebAuthInfo authinfo = null;
		try {
			authinfo = session.getAuthInfo();
			url = authinfo.url;
		}
		catch (DropboxException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public String retrieveNewToken() {
		String userid = null;
		AccessTokenPair tokens = session.getAccessTokenPair();
		RequestTokenPair requestTokenPair = new RequestTokenPair(tokens.key, tokens.secret);
		
		try {
			userid = session.retrieveWebAccessToken(requestTokenPair);
		}
		catch (DropboxException e) {
			e.printStackTrace();
		}
		
		return userid;
		
	}
	
	public void saveToken() {
		AccessTokenPair tokens = session.getAccessTokenPair();
		
		saveTokenToConfig(tokens.key, tokens.secret);
	}
	
	public void deleteToken() {
		if (isLinked()) {
			session.unlink();
		}
		saveTokenToConfig("", "");
	}
	
	protected void saveTokenToConfig(String key, String secret) {
		config.setString("oauth_key", key);
		config.setString("oauth_secret", secret);
		config.save();
	}

}
