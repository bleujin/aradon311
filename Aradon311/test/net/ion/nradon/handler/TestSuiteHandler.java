package net.ion.nradon.handler;

import junit.framework.TestCase;
import net.ion.nradon.handler.authentication.TestInMemoryPasswords;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestAliasHandler.class, TestCompression.class, TestCookie.class, TestEmbeddedResourceHandler.class, TestPathMatchHandler.class, TestURIPathMatchHander.class, TestPost.class, TestServerHeaderHandler.class
	, TestStaleConnection.class, TestStaticFileHandler.class, TestInMemoryPasswords.class})
public class TestSuiteHandler extends TestCase {
	

}
