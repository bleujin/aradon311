package net.ion.radon.core.let.jaxb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.core.let.PathHandler;
import net.ion.radon.core.let.TinyClient;

@Path("/json/product")
public class TestJaxb extends TestCase {

	@GET
	@Path("/{no}")
	@Produces(MediaType.APPLICATION_JSON)
	public Product getProduct() {
		return new Product("bleujin", 20);
	}

	public void testHello() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(TestJaxb.class)).start().get();

		TinyClient.local9000().sayHello("/json/product/1");
		radon.stop().get();
	}

}
