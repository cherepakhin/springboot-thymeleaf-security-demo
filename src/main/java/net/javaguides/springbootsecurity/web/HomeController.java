package net.javaguides.springbootsecurity.web;

import net.javaguides.springbootsecurity.entities.Message;
import net.javaguides.springbootsecurity.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
public class HomeController {
    @Autowired
    private MessageRepository messageRepository;

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("msgs", messageRepository.findAll());
        return "userhome";
    }

    @GetMapping("/messages/delete/{n}")
    public String deleteMessage(@PathVariable Integer n) {
        logger.info("Delete message: n=" + n);
        messageRepository.deleteById(n);
        return "redirect:/home";
    }

    @GetMapping("/messages/edit_message/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Message message = messageRepository.getById(id);
        model.addAttribute("message", message);
        return "/edit_message"; // show templates/edit_message.html
    }

    @PostMapping(value = "/save_message")
    public String saveMessage(@RequestParam(value = "n") Integer n,
                              @RequestParam(defaultValue = "----", value = "content") String newContent
                              ) { // from  edit_message.html -> th:name="new_content"
        // value = "messageContent" from th:name="messageContent"
        // see <input type="text" class="form-control" id="message" th:name="messageContent" th:value="*{content}" maxlength="100"/>
        logger.info("New content: " + newContent);
        logger.info("Message: n=" + n);
        Message message = new Message();
        message.setN(n);
        message.setContent(newContent);
        messageRepository.save(message);
        return "redirect:/home";
    }

    @GetMapping("/new_message")
    public String newMessage(Model model) {
        // model.addAttribute("msgs", messageRepository.findAll());
        return "new_message";
    }

    @PostMapping(value = "/new_message")
    public String newMessage(@RequestParam(defaultValue = "----", value = "content") String newContent) {
        logger.info("New message: " + newContent);
        Message message = new Message();
        message.setContent(newContent);
        messageRepository.save(message);
        return "redirect:/home";
    }

}
