package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.Role;
import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.repository.RoleRepository;
import com.mm.masterthesis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/logout")
    public String returnToLogin() {
        return "login";
    }

    @PostMapping("/auth")
    public String checkLogin(@Valid User user, BindingResult result, Model model) {
        System.out.println(result.hasErrors());
        System.out.println(userService.fullAuth(user));
        System.out.println(user.getName().isEmpty());
        System.out.println(user.getPassword().isEmpty());
        if (result.hasErrors()) {
            return "login";
        }

        if(user.getName().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("emptyCredentials", true);
            return "login";
        }

        if(userService.fullAuth(user)){
            return "redirect:/index";
        } else {
            model.addAttribute("badCredentials", true);
            return "login";
        }
    }

    @GetMapping("/login-error")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        return "login-error";

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
            Role role = roleRepository.findByName("ROLE_USER");
            if(role == null){
                role = checkRoleExist();
            }
            user.setRoles(List.of(role));
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userService.save(user);
            model.addAttribute("createdNewUser", true);
            return "login";
        }
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }
}
