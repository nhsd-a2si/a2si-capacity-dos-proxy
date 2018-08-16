package com.nhsd.a2si.client.dos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
//import com.nhsd.a2si.endpoint.dosproxy.DosWrapperSoapEndpoint;
//import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.nhsd.a2si.endpoint.dosproxy.PostFilter;
import com.nhsd.a2si.endpoint.dosproxy.Utils;

import javax.xml.ws.Holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Mike.Lythgoe on 27/06/2017.
 */
/*
  This Test requires a Wiremock instance running on localhost on port 7010
  (i.e. http://127.0.0.1:7010/app/api/webservices will be the full URL used)
  Because of this external dependency, this class should not be run automatically as
  part of build processes, hence the use of te @Ignore annotation.
 */
//@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Configuration
//@ActiveProfiles("doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na")
@ActiveProfiles("doswrapper-aws-dos-soap-aws-wiremock-cpsc-stub-na")
public class DosClientToWireMockIntegrationTests {
    
    @Value("${dos.service.url}")
    private String dosServiceUrl;

    private Properties userInfo;

    @Before
    public void createUserInfo() {
        userInfo = new Properties();
        userInfo.setProperty("userInfo.username", "dosStubClientUsername");
        userInfo.setProperty("userInfo.password", "dosStubClientPassword");
    }

    @Test
	@Ignore
    public void testCheckCapacitySummary() {
    	HttpClient httpClient = new DefaultHttpClient();
    	try {
	    	HttpPost httpPost = new HttpPost(dosServiceUrl);
	    	String data = getSubstitutedRequestXML(userInfo);
	    	HttpEntity entity = new StringEntity(data);
	    	httpPost.setEntity(entity);
	    	// execute method and handle any error responses.
	    	HttpResponse response = httpClient.execute(httpPost);    	
	    	
	    	int statusCode = response.getStatusLine().getStatusCode();    	
	    	
	    	assertEquals("Unexpected status code.", statusCode, 200);

	    	HttpEntity responseEntity = response.getEntity();
	    	String sResponseBody = EntityUtils.toString(responseEntity, "UTF-8");
	    	
	    	String sFirstServiceId = Utils.getXmlContent(sResponseBody, PostFilter.xmlServiceIdStart);
	        assertEquals("First Service Id not as expected.", sFirstServiceId, "1323782502");
    	} catch (Exception e) {
    		assertNull(e);
    	} finally {
    		httpClient.getConnectionManager().shutdown();
    	}
    }
/*
    @Test
    public void testHospitalName() {
        List<Hospital> hospitalList = dosWrapperSoapEndpoint.getHospitalScores(userInfo).getHospital();

        assertEquals(dosWrapperSoapEndpoint.getHospitalScores(userInfo).getHospital().get(0).getHospitalName(),"Hospital One");
    }

    @Test
    public void testexistingServiceIdReturnsServiceWithValidODSDCode() {

        assertEquals(dosWrapperSoapEndpoint.serviceDetailsById(userInfo, "12345").getService().get(0).getOdsCode(), "ODS-0001");
    }

    // This test doesn't work if we're using a stub that returns the data regardless
    @Test
    public void testNonexistentServiceIdReturnsNull() {

        assertEquals(0, dosWrapperSoapEndpoint.serviceDetailsById(userInfo, "does-not-exist").getService().size());

    }
*/    
    private static final String REQUEST_XML = 
    		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:web=\"https://nww.pathwaysdos.nhs.uk/app/api/webservices\">\n" + 
    		"   <soap:Header>\n" + 
    		"      <web:serviceVersion>1.3</web:serviceVersion>\n" + 
    		"   </soap:Header>\n" + 
    		"   <soap:Body>\n" + 
    		"      <web:CheckCapacitySummary>\n" + 
    		"         <web:userInfo>\n" + 
    		"           <web:username>**userInfo.username**</web:username>\n" + 
    		"           <web:password>**userInfo.password**</web:password>\n" + 
    		"         </web:userInfo>\n" + 
    		"         <web:c>\n" + 
    		"            <web:caseRef>**c.caseRef**</web:caseRef>\n" + 
    		"            <web:caseId>**c.caseId**</web:caseId>\n" + 
    		"            <web:postcode>SE165FX</web:postcode>\n" + 
    		"            <web:surgery>UNK</web:surgery>\n" + 
    		"            <web:age>30</web:age>\n" + 
    		"            <web:ageFormat>Years</web:ageFormat>\n" + 
    		"            <web:disposition>1012</web:disposition>\n" + 
    		"            <web:symptomGroup>1013</web:symptomGroup>\n" + 
    		"            <web:symptomDiscriminatorList>\n" + 
    		"               <!--Zero or more repetitions:-->\n" + 
    		"               <web:int>4156</web:int>\n" + 
    		"            </web:symptomDiscriminatorList>\n" + 
    		"            <!--Optional:-->\n" + 
    		"            <web:searchDistance>60</web:searchDistance>\n" + 
    		"            <web:gender>M</web:gender>\n" + 
    		"         </web:c>\n" + 
    		"      </web:CheckCapacitySummary>\n" + 
    		"   </soap:Body>\n" + 
    		"</soap:Envelope>";
    
    private String getSubstitutedRequestXML (Properties props) {
    	String xml = REQUEST_XML;
    	for (Object key : Collections.list(props.keys())) {
    		xml = xml.replaceAll("\\*\\*" + key.toString() + "\\*\\*", props.getProperty(key.toString()));
    	}
    	return xml;
    }
}


