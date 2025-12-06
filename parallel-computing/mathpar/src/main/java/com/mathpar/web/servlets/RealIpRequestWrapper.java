package com.mathpar.web.servlets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps HTTP request in way that {@code getRemoteAddr()} returns address from
 * "X-Real-IP" header to obtain real IP addresses behind the proxy.
 *
 * @author ivan
 */
public class RealIpRequestWrapper extends HttpServletRequestWrapper {

    public RealIpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRemoteAddr() {
        String realIP = super.getHeader("X-Real-IP");
        return realIP != null ? realIP : super.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        try {
            return InetAddress.getByName(getRemoteAddr()).getHostName();
        } catch (UnknownHostException e) {
            return getRemoteAddr();
        }
    }
}
