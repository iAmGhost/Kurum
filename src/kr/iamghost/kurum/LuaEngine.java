package kr.iamghost.kurum;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class LuaEngine {
	public enum LuaMode {
		UPLOAD, DOWNLOAD;
	}
	
	private static LuaEngine defaultLuaEngine;
	private ScriptEngineManager manager;
	private ScriptEngine engine;
	
	static {
		setDefaultLuaEngine(new LuaEngine());
	}
	
	public LuaEngine() {
		manager = new ScriptEngineManager();
		engine = manager.getEngineByExtension(".lua");
	}

	public void run(AppConfig appConfig, LuaMode mode) {
		try {
			String script = appConfig.getLuaScriptContent();
			
			if (mode == LuaMode.DOWNLOAD)
				script += "\nonDownload()";
			else
				script += "\nonUpload()";
			
			CompiledScript cs = ((Compilable)engine).compile(script);
			Bindings b = engine.createBindings();
			b.put("kurum", new AppSyncrEngine(appConfig));
			b.put("fileutil", new FileUtil());
			
			
			cs.eval(b);
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static LuaEngine getDefaultLuaEngine() {
		return defaultLuaEngine;
	}

	public static void setDefaultLuaEngine(LuaEngine defaultLuaEngine) {
		LuaEngine.defaultLuaEngine = defaultLuaEngine;
	}

}
