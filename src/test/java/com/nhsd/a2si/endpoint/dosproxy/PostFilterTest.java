package com.nhsd.a2si.endpoint.dosproxy;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PostFilterTest {

    private final String content;

    public PostFilterTest() throws IOException {
        content = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "dummy-response.xml")), "UTF-8");
    }

    @Test
    public void getServicesFromContnet() throws IOException {
        Map<String, String> services = PostFilter.getServices(content);
        assertTrue(services.size() == 12);
        assertTrue(Objects.nonNull(services.get("1402068411")));
    }

    @Test
    public void findNotesBlock() throws IOException {
        Map<String, String> services = PostFilter.getServices(content);
        String service = PostFilter.injectNoteIntoService("Here is your message!!!", services.get("1402068411"));
        assertTrue("The message in injected at the start of notes", service.contains("<ns1:notes>Here is your message!!!"));
    }
    
}