package com.nhsd.a2si.endpoint.dosproxy;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PostFilterTest {

    private final String content;

    private final String[] serviceOrder = new String[]{
            "1402068411", "2000001035", "1411477100", "1411478660", "1353952415", "1315571109", "2000003682",
            "2000003647", "1316782136", "1311847246", "2000007025", "2000007018", 
            "3000007041", "3000007042", "3000007043", "3000007044", "3000007045", "3000007046", 
    };

    public PostFilterTest() throws IOException {
        content = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "dummy-response.xml")), "UTF-8");
    }

    @Test
    public void getServicesFromContnet() throws IOException {
        Map<String, String> services = PostFilter.getServices(content);
        assertTrue(services.size() == serviceOrder.length);
        assertTrue(Objects.nonNull(services.get("1402068411")));
    }

    @Test
    public void findNotesBlock() throws IOException {
        Map<String, String> services = PostFilter.getServices(content);
        String service = PostFilter.injectNoteIntoService("Here is your message!!!", services.get("1402068411"));
        assertTrue("The message in injected at the start of notes", service.contains("<ns1:notes>Here is your message!!!"));
    }

    @Test
    public void correctServiceOrder() {
        Map<String, String> services = PostFilter.getServices(content);
        int i = 0;
        for (String s : services.keySet()) {
            assertThat(s, is(serviceOrder[i]));
            i++;
        }
    }

    @Test
    public void rejoinResponseBody() {
        String rejoinedServices = PostFilter.rejoinResponseBody(content, PostFilter.getServices(content));
        assertTrue(PostFilter.getServices(rejoinedServices).size() == serviceOrder.length);
        int i = 0;
        for (String s : PostFilter.getServices(rejoinedServices).keySet()) {
            assertThat(s, is(serviceOrder[i]));
            i++;
        }
    }

    @Test
    public void rejoinResponseBodyAfterInsertions() {
        Map<String, String> services = PostFilter.getServices(content);
        services.put("3000007046", PostFilter.injectNoteIntoService("Here is your message!!!", services.get("3000007046")));
        services.put("1402068411", PostFilter.injectNoteIntoService("Here is your message!!!", services.get("1402068411")));
        services.put("2000003682", PostFilter.injectNoteIntoService("Here is your message!!!", services.get("2000003682")));
        services.put("2000003647", PostFilter.injectNoteIntoService("Here is your message!!!", services.get("2000003647")));
        // Include blank message test
        services.put("3000007045", PostFilter.injectNoteIntoService("  ", services.get("3000007045")));
        int i = 0;
        for (String s : PostFilter.getServices(PostFilter.rejoinResponseBody(content, services)).keySet()) {
            assertThat(s, is(serviceOrder[i]));
            i++;
        }
    }

    @Test
    public void okCode(){
        assertFalse(PostFilter.okCode(199));
        assertTrue(PostFilter.okCode(200));
        assertTrue(PostFilter.okCode(203));
        assertTrue(PostFilter.okCode(299));
        assertFalse(PostFilter.okCode(300));
        assertFalse(PostFilter.okCode(400));
        assertFalse(PostFilter.okCode(404));
        assertFalse(PostFilter.okCode(500));
        assertFalse(PostFilter.okCode(502));
    }
}