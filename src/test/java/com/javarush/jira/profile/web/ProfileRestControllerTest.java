package com.javarush.jira.profile.web;

import com.javarush.jira.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.profile.web.ProfileRestController.REST_URL;
import static com.javarush.jira.profile.web.ProfileTestData.ADMIN_MAIL;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProfileRestControllerTest extends AbstractControllerTest{

    @Test
    void whenClientIsNotAuthorizedAndTryGet() throws Exception{
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    public void whenClientIsAuthorizedAndTryGet() throws Exception {
        String expectedJson = "{\"id\":2,\"lastLogin\":null,\"mailNotifications\":[\"two_days_before_deadline\",\"one_day_before_deadline\",\"three_days_before_deadline\"],\"contacts\":[{\"code\":\"github\",\"value\":\"adminGitHub\"},{\"code\":\"tg\",\"value\":\"adminTg\"},{\"code\":\"vk\",\"value\":\"adminVk\"}]}";
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void whenClientIsNotAuthorizedAndTryUpdate() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void whenClientIsAuthorizedAndTryUpdateWithEmptyData() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"mailNotifications\": [],\"contacts\": []}"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void whenClientIsAuthorizedAndTryUpdateWithInvalidData() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"mailNotifications\": [\"string\"],\"contacts\": [{\"code\": \"string\",\"value\": \"string\"}]}"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void whenClientIsAuthorizedAndTryUpdateWithMailNotificationsData() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"mailNotifications\": [\"one_day_before_deadline\"],\"contacts\": []}"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void whenClientIsAuthorizedAndTryUpdateWithEmptyMailNotificationsAndCorrectContacts() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"mailNotifications\": [],\"contacts\": [{\"code\": \"tg\",\"value\": \"adminTg\"}]}"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void whenClientIsAuthorizedAndTryUpdateWithAllCorrectData() throws Exception{
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"mailNotifications\": [\"one_day_before_deadline\"],\"contacts\": [{\"code\": \"tg\",\"value\": \"adminTg\"}]}"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}