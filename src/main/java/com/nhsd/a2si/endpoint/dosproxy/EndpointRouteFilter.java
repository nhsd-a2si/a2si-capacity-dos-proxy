package com.nhsd.a2si.endpoint.dosproxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class EndpointRouteFilter extends ZuulFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRouteFilter.class);
	
	@Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 101;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {


	    RequestContext ctx = RequestContext.getCurrentContext();
        LOGGER.debug("EndpointRouteFilter.run() has received a request");

        HttpServletRequest request = ctx.getRequest();

        try {
        	String sRequestBody = IOUtils.toString(request.getReader());
			ctx.set("caseRef", Utils.getXmlContent(sRequestBody, "<web:caseRef>"));
			ctx.set("caseId", Utils.getXmlContent(sRequestBody, "<web:caseId>"));
        } catch (IOException ioex) {
        	LOGGER.error("Cannot read posted data");
        }

        String url = ""; // This achieves not adding a slash to the end of the zuul.routes.dos.url application.yml property
        ctx.set("requestURI", url);

        LOGGER.debug("EndpointRouteFilter.run() has finished");
        return null;
    }

}
