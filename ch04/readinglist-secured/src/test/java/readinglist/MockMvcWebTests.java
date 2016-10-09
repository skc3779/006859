package readinglist;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
// 1.3.xx 이전소스로 1.4.xx 에서 duplicate 됨
@SpringApplicationConfiguration(classes = ReadingListApplication.class)
@WebAppConfiguration
public class MockMvcWebTests {

    @Autowired
    private WebApplicationContext webContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReaderRepository readerRepository;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    public void homePage_unauthenticatedUser() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "http://localhost/login"));
    }


    private void getBook() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "BOOK TITLE")
                .param("author", "BOOK AUTHOR")
                .param("isbn", "1234567890")
                .param("description", "DESCRIPTION"))
                .andExpect(status().is3xxRedirection())
                .andDo(print());

        Reader expectedReader = new Reader();
        expectedReader.setUsername("craig");
        expectedReader.setPassword("password");
        expectedReader.setFullname("Craig Walls");

        mockMvc.perform(get("/"))
                .andExpect(authenticated().withUsername("craig"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attribute("reader", samePropertyValuesAs(expectedReader)))
                .andExpect(model().attribute("books", is(not(hasSize(0)))))
                .andExpect(model().attribute("amazonID", "habuma-20"));

    }

    @Test
    @WithUserDetails("craig")
    public void homePage_authenticatedUser1() throws Exception {
        getBook();
    }


    @Test
    @WithUserDetails("craig")
    public void postBook_authenticatedUser() throws Exception {

        mockMvc.perform(post("/")
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "BOOK TITLE")
            .param("author", "BOOK AUTHOR")
            .param("isbn", "1234567890")
            .param("description", "DESCRIPTION"))
            .andExpect(status().is3xxRedirection())
            .andDo(print());

        MvcResult mvcResult= mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(1)))
                .andDo(print())
                .andReturn();

        log.info("model : {}", objectMapper.writeValueAsString(mvcResult.getModelAndView().getModel().get("books")));
    }

    /**
     * @WithUserDetails 사용하지 않고 userDetails 를 삽입
     * @throws Exception
     */
    @Test
    //@WithUserDetails("craig")
    public void userDetailsServiceTest() throws Exception {
        UserDetails userDetails = readerRepository.findOne("craig");
        mockMvc.perform(get("/").with(user(userDetails)))
                .andExpect(authenticated().withUsername("craig"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attribute("amazonID", "habuma-20"));
    }
}
