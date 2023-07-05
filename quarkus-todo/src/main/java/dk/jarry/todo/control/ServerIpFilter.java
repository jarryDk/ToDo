package dk.jarry.todo.control;


import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class ServerIpFilter implements ContainerResponseFilter {

	private static final Logger LOG = Logger.getLogger(ServerIpFilter.class);

	@Context
	HttpServerRequest request;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		System.out.println(request.getHeader("Authorization"));
		String name = "X-server-ip";
		String value = request.localAddress().host();
		responseContext.getHeaders().add(name, value);
		LOG.info("X-server-ip: " + value);

		request.headers().forEach( k -> {
				LOG.info( k.getKey() + " : " + request.getHeader(k.getKey()));
			}
		);

	}

}