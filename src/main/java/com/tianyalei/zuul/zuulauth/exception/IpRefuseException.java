package com.tianyalei.zuul.zuulauth.exception;


import com.netflix.zuul.exception.ZuulException;

/**
 * @author wuweifeng wrote on 2017/10/27.
 */
public class IpRefuseException extends ZuulException {

    public IpRefuseException(Throwable throwable, String sMessage, int nStatusCode, String errorCause) {
        super(throwable, "IP不在白名单，或IP在黑名单", 401, errorCause);
    }

    public IpRefuseException() {
        super("IP不在白名单，或IP在黑名单", 401, "");
    }

    public IpRefuseException(int code, String msg) {
        super(msg, code, "");
    }
}
