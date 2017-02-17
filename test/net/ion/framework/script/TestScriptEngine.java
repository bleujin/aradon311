package net.ion.framework.script;

import java.io.StringReader;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

public class TestScriptEngine extends TestCase {

	public void testJScript() throws Exception {
		Scripter je = Scripter.javascript();
		String hello = IOUtil.toStringWithClose(getClass().getResourceAsStream("helloworld.jscript"));
		InstantScript script = je.createScript(IdString.create("hello"), "hello world", new StringReader(hello));
		String result = script.call(new ResultHandler<String>() {
			public String onSuccess(Object result, Object... args) {
				return (String) result;
			}

			public String onFail(Exception ex, Object... args) {
				ex.printStackTrace();
				return null;
			}
		}, "sayHello", "bleujin");

		Debug.line(result, script.compiled().getClass());
	}

	public void testGScript() throws Exception {
		Scripter ge = Scripter.groovy();
		String hello = IOUtil.toStringWithClose(getClass().getResourceAsStream("helloworld.groovy"));
		InstantScript script = ge.createScript(IdString.create("hello"), "hello world", new StringReader(hello));
		String result = script.call(new ResultHandler<String>() {
			public String onSuccess(Object result, Object... args) {
				return (String) result;
			}

			public String onFail(Exception ex, Object... args) {
				ex.printStackTrace();
				return null;
			}
		}, "sayHello", "bleujin");
		Debug.line(result, script.compiled().getClass());
	}

//	public void testScalar() throws Exception {
//		ScriptEngine engine = new ScriptEngineManager().getEngineByName("scala");
//		((BooleanSetting) (((IMain) engine).settings().usejavacp())).value_$eq(true);
//		long start = System.currentTimeMillis() ;
//		engine.eval("var name = \"hello\" ; println(name)");
//
//		Debug.line(engine, System.currentTimeMillis() - start);
//	}

}
