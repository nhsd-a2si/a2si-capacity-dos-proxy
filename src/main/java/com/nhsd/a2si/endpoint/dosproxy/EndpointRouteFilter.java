package com.nhsd.a2si.endpoint.dosproxy;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.nhsd.a2si.capacity.reporting.service.client.CapacityReportingServiceClient;
import com.nhsd.a2si.capacity.reporting.service.dto.log.Header;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.web.client.RestTemplate;

@Component
public class EndpointRouteFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRouteFilter.class);

    @Autowired

    private CapacityReportingServiceClient capacityReportingServiceClient;
	
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

        LOGGER.debug("(" + System.identityHashCode(ctx) + ") Building DoS request");

        HttpServletRequest request = ctx.getRequest();

        String user = "unknown";
        try {
        	String sRequestBody = IOUtils.toString(request.getReader());
			ctx.set("caseRef", Utils.getXmlContent(sRequestBody, "<web:caseRef>"));
			ctx.set("caseId", Utils.getXmlContent(sRequestBody, "<web:caseId>"));
			user = Utils.getXmlContent(sRequestBody, "<web:username>");

        } catch (IOException ioex) {
        	LOGGER.error("Cannot read posted data");
        }

        String url = ""; // This achieves not adding a slash to the end of the zuul.routes.dos.url application.yml property
        ctx.set("requestURI", url);

        LOGGER.debug("(" + System.identityHashCode(ctx) + ") Sending DoS request");

        // Log that it has been called
        // to do log to history

        Header header = new Header();
        header.setAction(ctx.getRequest().getMethod().toUpperCase());
        header.setComponent("dos-proxy");
        header.setUserId(user);
        header.setEndpoint(ctx.getRouteHost().toString());
        header.setHashcode(String.valueOf(System.identityHashCode(ctx)));
        header.setTimestamp(new Date());
        Header saved = capacityReportingServiceClient.sendLogHeaderToRepotingService(header);

        // Note it would be better to set a UUID for this id
        // and then send up the header in its on thread.
        ctx.set("HeaderID", saved.getId());

        return null;
    }

}
