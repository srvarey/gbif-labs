/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.servlet;

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
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    String path = req.getRequestURI().substring(req.getContextPath().length());
    if (path.contains("/web/") || path.contains("/web2/")) {
      // do not chain any more filters
      request.getRequestDispatcher(path).forward(request, response);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }

}
