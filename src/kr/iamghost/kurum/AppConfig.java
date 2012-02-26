package kr.iamghost.kurum;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class AppConfig {
	private boolean isSyncing = false;
	private String appTitle;
	private String appName;
	private String processName;
	private String author;
	private File originalFile;
	private ArrayList<AppConfigVariable> vars = new ArrayList<AppConfigVariable>();
	private ArrayList<AppConfigFileEntry> files = new ArrayList<AppConfigFileEntry>();
	private boolean usesLuaScript;
	private String luaScriptContent;
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String name) {
		appName = name;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcess(String processName) {
		this.processName = processName;
	}
	
	public void addFile(AppConfigFileEntry file) {
		boolean found = false;
		
		if (!found)
			files.add(file);
	}
	
	public Iterator<AppConfigFileEntry> getFilesIterator() {
		return files.iterator();
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public String getDropboxZipPath() {
		return "/Data/" + appName + ".zip";
	}

	public File getOriginalFile() {
		return originalFile;
	}
	
	public void setOriginalFile(File originalFile) {
		this.originalFile = originalFile;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Iterator<AppConfigVariable> getVarsIterator() {
		return vars.iterator();
	}

	public void addVar(AppConfigVariable value) {
		vars.add(value);
	}
	
	public void resetAllVars() {
		PropertyUtil kurumConfig = PropertyUtil.getDefaultProperty();
		
		for (AppConfigVariable var : vars) {
			Environment.removeVariable(var.getName());
			kurumConfig.setString("var_" + var.getName(), "");
		}
		
		kurumConfig.setString(appName + ".zip", "");
	}
	
	public boolean checkAllVars() {
		boolean errorNotFound = true;
		
		PropertyUtil kurumConfig = PropertyUtil.getDefaultProperty();
		
		for (AppConfigVariable var : vars) {
			String dataInConfig = kurumConfig.getString("var_" + var.getName());
			
			if (Environment.getVariableData(var.getName()) == null) {
				if (!dataInConfig.equals("")) {
					Environment.addVariable(var.getName(), dataInConfig);
				}
				else
				{
					errorNotFound = false;
					Global.setObject("VariableNotFoundError", var);
					Global.setObject("VariableNotFoundAppConfig", this);
					break;
				}
			}
			else
			{
				Environment.getVariableData(var.getName());
			}
		}

		return errorNotFound;
	}

	public boolean isSyncing() {
		return isSyncing;
	}
	
	public boolean isValid() {
		if (getAppName() != null && getAppTitle() != null)
			return true;
		return false;
	}

	public void setSyncing(boolean isSyncing) {
		this.isSyncing = isSyncing;
	}

	public boolean isUsesLuaScript() {
		return usesLuaScript;
	}

	public void setUsesLuaScript(boolean usesLuaScript) {
		this.usesLuaScript = usesLuaScript;
	}

	public String getLuaScriptContent() {
		return luaScriptContent;
	}

	public void setLuaScriptContent(String luaScriptContent) {
		this.luaScriptContent = luaScriptContent;
	}
}
