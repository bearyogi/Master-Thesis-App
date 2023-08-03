package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public String redirectLogin(Model model) {
        model.addAttribute("user", new User());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/auth")
    public String checkLogin(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        if(user.getName().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("emptyCredentials", true);
            return "login";
        }

        if(userService.fullAuth(user)){
            model.addAttribute("user", userService.findByName(user.getName()));
            return "redirect:/index?userId="+userService.findByName(user.getName()).getId();
        } else {
            model.addAttribute("badCredentials", true);
            return "login";
        }
    }


    @GetMapping("/adduserform")
    public String addUserForm() {
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        if(user.getName().isEmpty() || user.getPassword().isEmpty()){
            model.addAttribute("newUserEmpty", true);
            return "add-user";
        }

        if(userService.auth(user)){
            model.addAttribute("userExist", true);
            return "add-user";
        } else {
            userService.save(user);
            model.addAttribute("createdNewUser", true);
            return "login";
        }
    }
}
