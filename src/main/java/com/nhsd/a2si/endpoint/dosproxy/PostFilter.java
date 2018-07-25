package com.nhsd.a2si.endpoint.dosproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nhsd.a2si.capacityinformation.domain.CapacityInformation;
import com.nhsd.a2si.capacityserviceclient.CapacityServiceClient;

@Component
public class PostFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(PostFilter.class);

    private CapacityServiceClient capacityServiceClient;

    public static final String xmlTransactionIdStart = "<ns1:TransactionId>";
    public static final String xmlServiceStart = "<ns1:ServiceCareSummaryDestination>";
    public static final String xmlServiceEnd = "</" + xmlServiceStart.substring(1);
    public static final String xmlServiceIdStart = "<ns1:id>";
    public static final String xmlServiceNotesStart = "<ns1:notes>";
    public static final String xmlServiceNotesEnd = "</" + xmlServiceNotesStart.substring(1);
    
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
        
        String sResponseBody = ctx.getResponseBody();
        if (sResponseBody == null) {
        	try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
        		if (responseDataStream != null) {
        			sResponseBody = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
        		}
    		} catch (IOException e) {
    		   logger.warn("Error reading body",e);
    		}        	
        }
        
        if (sResponseBody != null) {
        	
            boolean capacityServiceResponsive = true;

            String sTransactionId = Utils.getXmlContent(sResponseBody, xmlTransactionIdStart);

            int countOfResponses = 0;
            int countOfNonNullResponses = 0;
            int countOfServices = 0;
            
            if (sTransactionId.trim().length() > 0) {
            
	    		try {
	    			
	    			int idxServiceStart = sResponseBody.indexOf(xmlServiceStart);
	    			
	    			
	    			while (idxServiceStart > -1) {
	    				countOfServices++;
	    				
	    				int idxNextServiceStart = sResponseBody.indexOf(xmlServiceStart, idxServiceStart+1);
	    				if (idxNextServiceStart == -1) {
	    					idxNextServiceStart = Integer.MAX_VALUE;
	    				}
	    				int idxServiceEnd = sResponseBody.indexOf(xmlServiceEnd, idxServiceStart+1);
	    				if (idxServiceEnd > -1 && idxServiceEnd < idxNextServiceStart) {
	    				
		    				String sServiceId = Utils.getXmlContent(sResponseBody, xmlServiceIdStart, idxServiceStart+1);
		    				
		    				if (sServiceId.trim().length() > 0) {
	    						
	    		                if (capacityServiceResponsive) {
	    		                    try {
	
	    		                        logger.debug("Getting Capacity Information for Service Id: {}", sServiceId);
	
				                        CapacityInformation capacityInformation =
				                                capacityServiceClient.getCapacityInformation(sServiceId);
		
				                        logger.debug("Got Capacity Information for Service Id: {} with value of: {}",
		    		                        		sServiceId, capacityInformation);
		
				                        countOfResponses++;
				                        if (capacityInformation != null) {
			                        		String sMessage = capacityInformation.getMessage();
			                        		
			                        		if (sMessage != null && sMessage.length() > 0) {
			    	                    		countOfNonNullResponses++;
			    	                    		
			    	    	    				int idxServiceNotesStart = sResponseBody.indexOf(xmlServiceNotesStart, idxServiceStart+1);
			    	    	    				int idxServiceNotesEnd = sResponseBody.indexOf(xmlServiceNotesEnd, idxServiceStart+1);
			    	    	    				if (idxServiceNotesStart < idxServiceNotesEnd && idxServiceNotesStart > -1 && idxServiceNotesEnd > -1 && idxServiceNotesStart < idxNextServiceStart) {
			    	    	    					String sNotes = sResponseBody.substring(idxServiceNotesStart+xmlServiceNotesStart.length(), idxServiceNotesEnd);
			    	    	    					sNotes = sMessage + "\n\n" + sNotes;
			    	    	    					sResponseBody = sResponseBody.substring(0, idxServiceNotesStart+xmlServiceNotesStart.length()) + sNotes + sResponseBody.substring(idxServiceNotesEnd);
			    	    	    				}
			                        		}
				                        }
		
				                    } catch(ResourceAccessException resourceAccessException) {
				                        capacityServiceResponsive = false;
				                        logger.error("Unable to get response from Capacity Service - possible timeout");
				                    }
				                     catch (Exception e) {
				                        capacityServiceResponsive = false;
				                        logger.error("Unable to get response from Capacity Service");
				                    }
	    		                }
			                }
	    				}
	
	    				idxServiceStart = sResponseBody.indexOf(xmlServiceStart, idxServiceEnd);
	    			}
	    		} catch (Exception e) {
	        		logger.error("Error processing returned XML xml={}, error={})", sResponseBody, e.getMessage());
	    			e.printStackTrace();
	    		}
	        	
	
	            if (capacityServiceResponsive) {
            		logger.info("DOS returned {} services, of which {} had waiting times (TransactionId={}, CaseRef={}, CaseID={})", countOfServices, countOfNonNullResponses, sTransactionId, ctx.get("caseRef"), ctx.get("caseId"));
	            } else {
	        		logger.info("DOS returned {} services, of which {} had waiting times, however the capacity service became unresponsive and only {} were checked for waiting times (TransactionId={}, CaseRef={}, CaseID={})", countOfServices, countOfNonNullResponses, countOfResponses, sTransactionId, ctx.get("caseRef"), ctx.get("caseId"));
	            }
            } else {
            	logger.info("Controlled unexpected response being returned from DoS");
            }
	       	
	 		ctx.setResponseBody(sResponseBody); 
        }

        return null;
    }

}
