package readinglist;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
// 1.4.xx
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
// 1.3.xx
//@SpringApplicationConfiguration(classes = ReadingListApplication.class)
//@WebIntegrationTest(randomPort = true)
public class SimpleWebTest {

    @Value("${local.server.port}")
    private int port;
	
	@Test(expected = HttpClientErrorException.class)
    public void pageNotFound() {

        log.info("pageNotFound port : {}", port);

        try {
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://localhost:{port}/bogusPage", String.class, port);
            fail("Should result in HTTP 404");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            throw e;
        }
    }

}
