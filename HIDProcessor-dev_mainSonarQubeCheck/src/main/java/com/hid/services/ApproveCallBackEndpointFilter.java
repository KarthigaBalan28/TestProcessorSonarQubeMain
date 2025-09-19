package com.hid.services;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.servlet.filters.IntegrationCustomFilter;
@SuppressWarnings("java:S1186")
@IntegrationCustomFilter(filterOrder = 102, urlPatterns = "ApproveCallBackEndpoint")
public class ApproveCallBackEndpointFilter implements Filter {

	private static final Logger LOG = LogManager.getLogger(com.hid.services.ApproveCallBackEndpointFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOG.debug("HID : In ApproveCallBackEndpointFilter");

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		final String authHeader = httpRequest.getHeader("Authorization");

		LOG.debug("HID : Authorization Request Header --> {}", authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new ServletException("Missing or invalid Authorization header.");
		}

		String token = authHeader.substring(7);
		try {
			if (token != null)
				token = token.trim();
			String storedToken = Objects.toString(
					ServicesManagerHelper.getServicesManager(httpRequest).getResultCache().retrieveFromCache(token),
					null);
			// Doesn't exist in result cache
			if (StringUtils.isBlank(storedToken)) {
				LOG.debug("HID : Invalid Bearer token in Authorization header ....");
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"Invalid Bearer token in Authorization header");
			} else {
				ServicesManagerHelper.getServicesManager(httpRequest).getResultCache().removeFromCache(storedToken);
			}
		} catch (Exception e) {
			LOG.error("HID : Error occured while retrieving the client notification token from result cache", e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

		}
		LOG.debug("HID : Continuing middleware servlet chain execution....");
		chain.doFilter(httpRequest, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
