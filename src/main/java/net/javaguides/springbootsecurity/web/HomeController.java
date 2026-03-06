package net.javaguides.springbootsecurity.web;

import net.javaguides.springbootsecurity.entities.Message;
import net.javaguides.springbootsecurity.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.logging.Logger;

/**
 * @author Ramesh Fadatare
 */
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

    @GetMapping("/messages/edit/{n}")
    public String editMessage(@PathVariable Integer n, Model model) {
        logger.info("Edit message: n=" + n);
        Message message = messageRepository.getById(n);
        logger.info("Received message:" + message);
        model.addAttribute("content", message.getContent());
        model.addAttribute("n", message.getN());
        return "redirect:/home";
    }

    @PostMapping("/messages")
    public String saveMessage(Message message) {
        logger.info("Save message: " + message.toString());
        messageRepository.save(message);
        return "redirect:/home";
    }

}
