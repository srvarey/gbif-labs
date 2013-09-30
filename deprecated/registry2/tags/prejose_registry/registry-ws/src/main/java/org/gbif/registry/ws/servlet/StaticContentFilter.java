package org.gbif.registry.ws.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A filter that will look for the /web/* content and then stop all subsequent filters from firing.
 * This is intended to intercept the admin console, and stop Guice taking over and assuming everything is
 * intended for Jersey.
 */
public class StaticContentFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
    ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String path = req.getRequestURI().substring(req.getContextPath().length());

    if (path.startsWith("/web")) {
      // do not chain any more filters
      request.getRequestDispatcher(req.getRequestURI()).forward(request, response);

    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }
}
