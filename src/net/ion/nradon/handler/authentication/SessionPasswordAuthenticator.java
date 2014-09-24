package net.ion.nradon.handler.authentication;

import java.util.concurrent.Executor;

import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;

/**
 * Provided to BasicAuthenticationHandler to verify the supplied username and password are valid.
 * 
 * Implementations should check username/password are valid and call ResultCallback.success() or ResultCallback.failure(). One of these methods must called - once and only once.
 * 
 * If the result cannot be obtained automatically, the code should not block (as this will block the entire server). Instead, the work should be offloaded to another thread/process, and the ResultCallback methods should be invoked using the handlerExecutor when done.
 * 
 * For simple cases, use InMemoryPasswords.
 * 
 * See samples.authentication.SimplePasswordsExample in the src/tests directory for a really basic usage. To implement a custom authenticator that performs background IO, see samples.authentication.AsyncPasswordsExample.
 * 
 * @see BasicAuthenticationHandler
 * @see InMemoryPasswords
 */
public interface SessionPasswordAuthenticator {

	void authenticate(HttpRequest request, HttpResponse response, String username, String password, ResultCallback callback, Executor handlerExecutor);

	interface ResultCallback {
		void success();

		void failure();
	}

}
