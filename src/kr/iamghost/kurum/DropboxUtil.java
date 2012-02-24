package kr.iamghost.kurum;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

public class DropboxUtil {
	final static private String INTERNAL_CONFIG_FILE = "InternalConfig.properties";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<WebAuthSession> client;
	private WebAuthSession session;
	private boolean isLoggedIn = false;
	private static DropboxUtil defaultDropbox;
	
	static {
		defaultDropbox = new DropboxUtil();
	}
	
	public static DropboxUtil getDefaultDropbox() {
		return defaultDropbox;
	}
	
	public DropboxUtil() {
		refreshClient();
		loadSavedKeys();
	}
	
	public void refreshClient() {
		PropertyUtil keyFile = new PropertyUtil().loadLocalFile(INTERNAL_CONFIG_FILE);
		String api_key = keyFile.getString("api_key");
		String api_secret =  keyFile.getString("api_secret");
		
		AppKeyPair appkey = new AppKeyPair(api_key, api_secret);
		session = new WebAuthSession(appkey, ACCESS_TYPE);
		client = new DropboxAPI<WebAuthSession>(session);
	}
	
	public DropboxEntry getMetadata(String path) {
		return getMetadata(path, 0, null, true, null);
	}
	
	public DropboxEntry getMetadata(String path, int fileLimit, String hash, boolean list, String rev) {
		DropboxEntry entry = new DropboxEntry();
		
		try {
			Entry newEntry = client.metadata(path, fileLimit, hash, list, rev);
			entry.setEntry(newEntry);
		}
		catch (DropboxException e) {
			//e.printStackTrace();
		}
		
		return entry;
	}
	
	public DropboxEntry upload(File source, String destPath, boolean overwrite) {
		return upload(source, destPath, null, overwrite);
	}
	
	public DropboxEntry upload(File source, String destPath, String rev, boolean overwrite) {
		DropboxEntry result = new DropboxEntry();

		try {
			FileInputStream is = new FileInputStream(source);
			Entry newEntry;
			if (!overwrite)
				newEntry = client.putFile(destPath, is, source.length(), rev, null);
			else
				newEntry = client.putFileOverwrite(destPath, is, source.length(), null);
			
			is.close();
			result.setEntry(newEntry);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public DropboxFileInfo download(String path, File dest) {
		return download(path, dest, null);
	}
	
	public DropboxFileInfo download(String path, File dest, String rev) {
		DropboxFileInfo fileInfo = null;
		try {
			dest.mkdirs();
			
			FileOutputStream os = new FileOutputStream(dest, false);
			
			fileInfo = client.getFile(path, rev, os, null);
			
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileInfo;
		
	}
	
	public DropboxEntry uploadText(String text, String destPath) {
		DropboxEntry entry = new DropboxEntry();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes());
		
		try {
		   Entry newEntry = client.putFileOverwrite(destPath, inputStream,
				   text.length(), null);
		   
		   entry.setEntry(newEntry);
		   
		   inputStream.close();
		}
		catch (DropboxUnlinkedException e) {
			//
		}
		catch (DropboxException e) {
			//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entry;
	}
	
	public void Delete(String path) {
		try {
			client.delete(path);
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadSavedKeys() {
		PropertyUtil config = new PropertyUtil().loadDefaultFile();
		String auth_key = config.getString("oauth_key");
		String auth_secret = config.getString("oauth_secret");
		
		if (!auth_key.equals("") && !auth_secret.equals("")) {
			AccessTokenPair tokens = new AccessTokenPair(auth_key, auth_secret);
			session.setAccessTokenPair(tokens);
			if (isLinked()) {
				Global.set("OnDropboxLoggedIn", true);
			}
		}
		
		
	}
	
	public boolean isLinked() {
		boolean linked = false;
		
		try {
			if (isLoggedIn) {
				linked = true;
			}
			else
			{
				if (client.accountInfo().uid > 0 && session.isLinked()) {
					linked = true;
					isLoggedIn = true;
				}
			}
		}
		catch (DropboxUnlinkedException e) {
			isLoggedIn = false;
			linked = false;
		}
		catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return linked;
	}
	
	public Account getAccountInfo() {
		Account account = null;
		try {
			account = client.accountInfo();
		} catch (DropboxUnlinkedException e) {
			//just return null
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
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
		catch (DropboxUnlinkedException e) {
			userid = null;
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
			isLoggedIn = false;
		}
		saveTokenToConfig("", "");
	}
	
	protected void saveTokenToConfig(String key, String secret) {
		PropertyUtil kurumConfig = PropertyUtil.getDefaultProperty();
		kurumConfig.setString("oauth_key", key);
		kurumConfig.setString("oauth_secret", secret);
		kurumConfig.save();
	}

}
