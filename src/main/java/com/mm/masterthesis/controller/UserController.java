package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.CredentialService;
import com.mm.masterthesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CredentialService credentialService;

    @GetMapping("/")
    public String redirectLogin(Model model) {
        model.addAttribute("user", new User());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/log")
    public String showLogout() {
        SecurityUtils.getSubject().logout();
        return "login";
    }

    @PostMapping("/auth")
    public String checkLogin(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        if (user.getName().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("emptyCredentials", true);
            return "login";
        }

        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            subject.getSession(true).setAttribute("name", user.getName());
            if(Objects.equals(user.getName(), "admin")) {
                subject.getSession(true).setAttribute("role", "admin");
            } else {
                subject.getSession(true).setAttribute("role", "user");
            }
            return "redirect:/index";
        } catch (
                UnknownAccountException | IncorrectCredentialsException e) {
            model.addAttribute("badCredentials", true);
            return "login";
        }
    }

    @GetMapping("/index")
    public String showCredentialList(Model model) {
        if(checkRole(List.of("user", "admin"))) {
            model.addAttribute("credentials", credentialService.findAllForUser(SecurityUtils.getSubject().getSession(false).getAttribute("name") + ""));
            return "index";
        } else {
            return "admin-bad";
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
            if (user.getName().isEmpty() || user.getPassword().isEmpty()) {
                model.addAttribute("newUserEmpty", true);
                return "add-user";
            }

            if (userService.auth(user)) {
                model.addAttribute("userExist", true);
                return "add-user";
            } else {
                user.setRole("user");
                String salt = new SecureRandomNumberGenerator().getSecureRandom().toString();
                user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
                user.setSalt(salt);
                userService.save(user);
                model.addAttribute("createdNewUser", true);
                return "login";
            }
    }

    public boolean checkRole(Collection<String> roles) {
        return roles.contains(SecurityUtils.getSubject().getSession(true).getAttribute("role"));
    }
}
