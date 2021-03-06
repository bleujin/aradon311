package net.ion.radon.core.let;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpRequest;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.util.HttpRequestImpl;

public class RestRequest extends HttpRequestImpl {
	private HttpRequest inner;

	public RestRequest(HttpRequest inner, InputStream inputStream, HttpHeaders httpHeaders, String httpMethod, UriInfo uri) {
        super(inputStream, httpHeaders, httpMethod, uri);
        this.inner = inner ;
    }

    public void setInputStream(InputStream stream) {
        throw new UnsupportedOperationException();
    }

    public Object getAttribute(String name) {
        return inner.data(name) ;
    }

    public void setAttribute(String name, Object value) {
    	inner.data(name, value) ;
    }

    public void removeAttribute(String name) {
       inner.data().remove(name) ;
    }

    public AsynchronousResponse createAsynchronousResponse(long suspendTimeout) {
        throw new UnsupportedOperationException();
    }

    public AsynchronousResponse getAsynchronousResponse() {
        throw new UnsupportedOperationException();
    }

    public void initialRequestThreadFinished() {
        throw new UnsupportedOperationException();
    }
    
//    public MultivaluedMap<String, String> getFormParameters() {
//    	return Encode.decode(super.getFormParameters()) ;
//    }

    public static RestRequest wrap(final HttpRequest request, String prefixURI) throws UnsupportedEncodingException {
        HttpHeaders headers = new RestRequestHeaders(request);

        // org.jboss.resteasy.plugins.server.servlet.ServletUtil is doing this differently (much more complex - not sure why)
        URI uri = URI.create(request.uri());
        URI absPath = URI.create(uri.getPath()) ;
        // URI uri = URI.create(StringUtil.strip(request.uri(), prefixURI));
        
        UriInfo uriInfo = new UriInfoImpl(absPath, uri, uri.getPath(), uri.getQuery(), PathSegmentImpl.parseSegments(StringUtil.removeStart(uri.getPath(), prefixURI), true));
        

        String body = request.body();
        InputStream in = body == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(request.bodyAsBytes());
        return new RestRequest(request, in, headers, request.method(), uriInfo);
    }
    
    public String toString(){
    	return getClass().getCanonicalName() + "[" + inner.uri() + "]" ; 
    }

}
