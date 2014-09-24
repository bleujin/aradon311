package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;


public interface FormDataHandler<T> {

	public T handle(InputBody ibody) throws IOException ;
}
