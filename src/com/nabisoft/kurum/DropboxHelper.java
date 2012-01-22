package com.nabisoft.kurum;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

public class DropboxHelper {
	final static private String APP_KEY = "rvzxzpe5uny24vk";
	final static private String APP_SECRET = "p2w1u3zeq2yx5js";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<WebAuthSession> mAPI;
	private WebAuthSession mSession;
	private WebAuthInfo mAuthInfo;
	
	public DropboxHelper() {
		
		AppKeyPair keys = new AppKeyPair(APP_KEY, APP_SECRET);
		
		mSession = new WebAuthSession(keys, ACCESS_TYPE);
		mAPI = new DropboxAPI<WebAuthSession>(mSession);
	}
	
	public void RequestNewToken() {
		try {
			mAuthInfo = mAPI.getSession().getAuthInfo();
		}
		catch (DropboxException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(mAuthInfo.url);
	}
	
	public void GetNewToken() {
		AccessTokenPair tokens = mSession.getAccessTokenPair();
		System.out.println(tokens.key + "/" + tokens.secret);
	}

}
