package com.nhsd.a2si.client.dos;

//import https.nww_pathwaysdos_nhs_uk.app.api.webservices.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.ws.Holder;

import static org.junit.Assert.assertEquals;

/**
 * Created by Mike.Lythgoe on 27/06/2017.
 */

@Ignore
@RunWith(SpringRunner.class)
@ActiveProfiles("doswrapper-local-dos-stub-na-cpsc-stub-na")
public class DosStubClientUnitTests {
/*

    private DosStubClient dosClient = new DosStubClient();

    private UserInfo userInfo;

    @Before
    public void createUserInfo() {

        ObjectFactory objectFactory = new ObjectFactory();
        userInfo = objectFactory.createUserInfo();
        userInfo.setUsername("dosStubClientUsername");
        userInfo.setPassword("dosStubClientPassword");

    }

    @Test
    public void testCheckCapacitySummary() {

        Case c = new ObjectFactory().createCase();

        Holder<String> transactionId = new Holder<>();
        Holder<ArrayOfServiceCareSummaryDestination> checkCapacitySummaryResult = new Holder<>();

        dosClient.checkCapacitySummary(userInfo, c, transactionId, checkCapacitySummaryResult);

        assertEquals(checkCapacitySummaryResult.value.getServiceCareSummaryDestination().get(0).getId(),132175);
    }

    @Test
    public void testHospitalName() {

        assertEquals(dosClient.getHospitalScores(userInfo).getHospital().get(0).getHospitalName(),"Hospital One");
    }

    @Test
    public void testExistingServiceIdReturnsServiceWithValidODSDCode() {

        assertEquals(dosClient.serviceDetailsById(userInfo, "12345").getService().get(0).getOdsCode(), "ODS-0001");
    }

    // This test doesn't work if we're using a stub that returns the data regardless
    @Test
    public void testNonexistentServiceIdReturnsNull() {

        assertEquals(0, dosClient.serviceDetailsById(userInfo, "does-not-exist").getService().size());
    }
*/    
}
