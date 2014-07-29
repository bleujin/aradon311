package net.ion.bleujin.aradon;

import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.core.let.PathHandler;

public class HelloServer {

	public static void main(String[] args) throws Exception {
		Radon radon = RadonConfiguration.newBuilder(8111).add(new PathHandler(HelloServerResource.class)).start().get();
	}

}