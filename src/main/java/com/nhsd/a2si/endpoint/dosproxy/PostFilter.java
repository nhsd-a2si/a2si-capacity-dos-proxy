package com.nhsd.a2si.endpoint.dosproxy;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nhsd.a2si.capacityserviceclient.CapacityServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PostFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(PostFilter.class);

    private CapacityServiceClient capacityServiceClient;

    @Autowired
    public PostFilter(CapacityServiceClient capacityServiceClient) {
        this.capacityServiceClient = capacityServiceClient;
    }

    @PostConstruct
    public void logPostConstruct() {
        logger.info("capacityServiceClient is {}", capacityServiceClient);
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 500;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        logger.debug("(" + System.identityHashCode(ctx) + ") DoS response received");

        // Read the response body
        String sResponseBody = ctx.getResponseBody();
        if (sResponseBody == null) {
            try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
                if (responseDataStream != null) {
                    sResponseBody = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
                }
            } catch (IOException e) {
                logger.warn("Error reading body", e);
            }
        }

        if(okCode(ctx.getResponseStatusCode())) {
            if (sResponseBody != null) {

                int countOfServices = 0; // This is from DoS
                int countOfCapacityRecords = 0; // This is from capacity service

                logger.debug("Receiving service IDs from the response");
                Map<String, String> services = getServices(sResponseBody);
                countOfServices = services.size();

                logger.debug("Got the following service IDs: {}", services.keySet());

                Map<String, String> capacityInformation = capacityServiceClient.getCapacityInformation(services.keySet());
                countOfCapacityRecords = capacityInformation.size();

                for (Map.Entry<String, String> entry : capacityInformation.entrySet()) {
                    String s = services.get(entry.getKey());
                    String injectedNote = injectNoteIntoService(entry.getValue(), s);
                    services.put(entry.getKey(), injectedNote);
                }
                sResponseBody = rejoinResponseBody(sResponseBody, services);
            } else {
                logger.info("Controlled unexpected response being returned from DoS");
            }
        } else {
            logger.info("Unexpected response code being returned from DoS: " + ctx.getResponseStatusCode() + " " + sResponseBody);
        }

        ctx.setResponseBody(sResponseBody);


        return null;
    }

    static String rejoinResponseBody(String sResponseBody, Map<String, String> services) {
        return sResponseBody.replaceAll("(?s)<ns1:CheckCapacitySummaryResult>(.*?)</ns1:CheckCapacitySummaryResult>", "<ns1:CheckCapacitySummaryResult>" + services.values().stream().collect(Collectors.joining()) + "</ns1:CheckCapacitySummaryResult>");
    }

    static Map<String, String> getServices(String responseBody) {
        Map<String, String> ids = new LinkedHashMap<>();
        Pattern regex = Pattern.compile("(<ns1:ServiceCareSummaryDestination>\\s*?<ns1:id>(\\d+?)</ns1:id>.*?</ns1:ServiceCareSummaryDestination>)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(responseBody);
        while (regexMatcher.find()) {
            ids.put(regexMatcher.group(2), regexMatcher.group(1));
        }
        return ids;

    }

    static String injectNoteIntoService(String note, String service) {
        return service.replaceAll("(?s)<ns1:notes>(.*?</ns1:notes>)", "<ns1:notes>" + note + "\n\n$1");
    }

    static boolean okCode(int code){
        return (code >= 200 && code < 300);
    }

}
