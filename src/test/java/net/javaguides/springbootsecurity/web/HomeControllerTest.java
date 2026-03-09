package net.javaguides.springbootsecurity.web;

import net.javaguides.springbootsecurity.entities.Message;
import net.javaguides.springbootsecurity.repositories.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Only load MVC layer, disable security entirely
//@WebMvcTest(
//        controllers = HomeController.class,
//        useDefaultFilters = false // Prevent accidental component scan
//)
@WebMvcTest
@WithMockUser // Provides a mock authenticated user for secured endpoints
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    javax.sql.DataSource dataSource;

    private Message msg1;
    private Message msg2;

    @BeforeEach
    void setUp() {
        msg1 = new Message();
        msg1.setN(1);
        msg1.setContent("Hello World");

        msg2 = new Message();
        msg2.setN(2);
        msg2.setContent("Second message");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com")
    public void testRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com")
    public void testHomeURL() throws Exception {
        when(messageRepository.findAll()).thenReturn(Arrays.asList(msg1, msg2));

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("userhome"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com")
    public void testHome_ShouldReturnUserHomeWithMessages() throws Exception {
        when(messageRepository.findAll()).thenReturn(Arrays.asList(msg1, msg2));

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("userhome"))
                .andExpect(model().attributeExists("msgs"))
                .andExpect(model().attribute("msgs", hasSize(2)))
                .andExpect(model().attribute("msgs", hasItem(hasProperty("n", is(1)))))
                .andExpect(model().attribute("msgs", hasItem(hasProperty("n", is(2)))));

        verify(messageRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteMessage_ShouldDeleteByIdAndRedirect() throws Exception {
        doNothing().when(messageRepository).deleteById(1);

        mockMvc.perform(get("/messages/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(messageRepository, times(1)).deleteById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testEdit_ShouldReturnEditViewWithMessage() throws Exception {
        when(messageRepository.getById(1)).thenReturn(msg1);

        mockMvc.perform(get("/messages/edit_message/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/edit_message"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", hasProperty("n", is(1))))
                .andExpect(model().attribute("message", hasProperty("content", is("Hello World"))));

        verify(messageRepository, times(1)).getById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testSaveMessage_ShouldUpdateAndRedirect() throws Exception {
        Message existingMessage = new Message();
        existingMessage.setN(1);
        existingMessage.setContent("Old content");

        when(messageRepository.findById(1)).thenReturn(Optional.of(existingMessage));
        /* Example thenAnswer with map return
        when(messageRepository.save(existingMessage))
                .thenAnswer(
                        invocation -> invocation.<List<String>>getArgument(0).stream()
                                .map(
                                    m -> {
                                        switch (m) {
                                            case "1": return existingMessage;
                                        }
                                      return m;
                                    }));
        */
        when(messageRepository.save(existingMessage)).thenReturn(existingMessage);
        mockMvc.perform(post("/save_message")
                        .with(csrf())
                        .param("n", "1")
                        .param("content", "Updated content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

//TODO: add verify before update        verify(messageRepository, times(1)).findById(1);
        verify(messageRepository, times(1)).save(argThat(m ->
                m.getN().equals(1) && m.getContent().equals("Updated content")
        ));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testNewMessage_Get_ShouldReturnNewMessageForm() throws Exception {
        mockMvc.perform(get("/new_message"))
                .andExpect(status().isOk())
                .andExpect(view().name("new_message"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testNewMessage_Post_ShouldSaveAndRedirect() throws Exception {
        when(messageRepository.save(any())).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setN(99); // simulate generated ID
            return m;
        });

        mockMvc.perform(post("/new_message")
                        .with(csrf())
                        .param("content", "Brand new message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(messageRepository, times(1)).save(argThat(m ->
                m.getContent().equals("Brand new message") && m.getN() == 99
        ));
    }
}