package com.sun.ts.tests.ejb30.lite.nointerface.descriptor;

import com.sun.ts.tests.ejb30.lite.nointerface.descriptor.Client;
import com.sun.ts.tests.ejb30.lite.nointerface.descriptor.HttpServletDelegate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EJBLiteServletVehicle extends Client implements Servlet, ServletConfig {
    private static Logger logger = Logger.getLogger(com.sun.ts.tests.ejb30.lite.nointerface.descriptor.EJBLiteServletVehicle.class.getName());

    private HttpServletDelegate delegate = new HttpServletDelegate();

    public void init(ServletConfig config) throws ServletException {
        this.delegate.init(config);
    }

    public ServletConfig getServletConfig() {
        return this.delegate.getServletConfig();
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        this.delegate.service(request, response);
        setInjectionSupported(Boolean.valueOf(true));
        String tn = request.getParameter("testName");
        logger.fine("EJBLiteServletVehicle processing request testName=" + tn);
        setTestName(tn);
        setModuleName(getServletContext().getContextPath());
        String sta = getStatus();
        PrintWriter pw = response.getWriter();
        pw.println(sta + " " + getReason());
        cleanup();
    }

    public String getServletInfo() {
        return this.delegate.getServletInfo();
    }

    public void destroy() {
        this.delegate.destroy();
        this.delegate = null;
    }

    public String getServletName() {
        return this.delegate.getServletName();
    }

    public ServletContext getServletContext() {
        return this.delegate.getServletContext();
    }

    public String getInitParameter(String arg0) {
        return this.delegate.getInitParameter(arg0);
    }

    public Enumeration<String> getInitParameterNames() {
        return this.delegate.getInitParameterNames();
    }
}