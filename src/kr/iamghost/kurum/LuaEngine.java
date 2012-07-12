package kr.iamghost.kurum;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.luaj.vm2.script.LuaScriptEngineFactory;

public class LuaEngine {
	public enum LuaMode {
		UPLOAD, DOWNLOAD;
	}
	private static LuaEngine defaultLuaEngine;
	private ScriptEngine engine = new LuaScriptEngineFactory().getScriptEngine();
	
	static {
		setDefaultLuaEngine(new LuaEngine());
	}
	
	public void run(AppConfig appConfig, LuaMode mode) {
		try {
			String script = appConfig.getLuaScriptContent();
			
			if (mode == LuaMode.DOWNLOAD) {
				script += "\nonDownload()";
			} else {
				script += "\nonUpload()";
			}
			
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
