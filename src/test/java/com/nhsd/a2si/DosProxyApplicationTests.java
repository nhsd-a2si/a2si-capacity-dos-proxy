package com.nhsd.a2si;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("doswrapper-local-dos-stub-na-cpsc-stub-na")
public class DosProxyApplicationTests {

	@Test
	public void contextLoads() {
	}

}
