package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.jaas.ConsoleCallbackHandler;
import com.mm.masterthesis.jaas.InMemoryLoginModule;
import com.mm.masterthesis.jaas.LoginService;
import com.mm.masterthesis.service.CredentialService;
import com.mm.masterthesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LoginService loginService;
    private final CredentialService credentialService;
    private final InMemoryLoginModule inMemoryLoginModule;
    private Subject subject;
    LoginContext loginContext;

    @GetMapping("/")
    public String redirectLogin(Model model) {
        model.addAttribute("user", new User());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/out")
    public String logout() {
        subject.getPrincipals().clear();
        return "redirect:/login";
    }

    @PostMapping("/auth")
    public String checkLogin(@Valid User user, BindingResult result, Model model) throws LoginException {
        ConsoleCallbackHandler consoleCallbackHandler = new ConsoleCallbackHandler(userService);
        consoleCallbackHandler.setUsername(user.getName());
        loginContext = new LoginContext("jaasApplication", consoleCallbackHandler);
        subject = loginService.login(loginContext);
        if (result.hasErrors()) {
            return "login";
        }

        if(user.getName().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("emptyCredentials", true);
            return "login";
        }

        if(subject != null){
            model.addAttribute("user", userService.findByName(user.getName()));
            return "redirect:/index";
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
            user.setRole("user");
            userService.save(user);
            model.addAttribute("createdNewUser", true);
            return "login";
        }
    }

    @GetMapping("/index")
    public String showCredentialList(Model model) {
        String name = subject.getPrincipals().stream().findFirst().toString().substring(9);
        name = name.substring(0, name.length()-1);
        model.addAttribute("credentials", credentialService.findAllForUser(name));
        return "index";
    }

    @GetMapping("/admin")
    public String showAdminPanel( Model model) {
        String name = subject.getPrincipals().stream().findFirst().toString().substring(9);
        name = name.substring(0, name.length()-1);
        if(!name.equals("admin")){
            return "admin-bad";
        } else {
            model.addAttribute("credentials", credentialService.findAll());
            return "admin";
        }
    }

    @GetMapping("/addcredential")
    public String addCredentialForm() {
        return "add-credential";
    }

    @PostMapping("/addcredential")
    public String addCredential(@Valid Credential credential, BindingResult result, Model model) {
        String name = subject.getPrincipals().stream().findFirst().toString().substring(9);
        name = name.substring(0, name.length()-1);
        if (result.hasErrors()) {
            return "add-credential";
        }
        if(credential.getResource().isEmpty() || credential.getPassword().isEmpty()){
            model.addAttribute("newCredentialEmpty", true);
            return "add-credential";
        }
        credential.setUserpass(name);
        credentialService.save(credential);
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Credential credential = credentialService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credential Id:" + id));

        model.addAttribute("credential", credential);
        return "update-credential";
    }

    @PostMapping("/update/{id}")
    public String updateCredential(@PathVariable("id") long id, @Valid Credential credential,
                                   BindingResult result) {
        String name = subject.getPrincipals().stream().findFirst().toString().substring(9);
        name = name.substring(0, name.length()-1);
        if (result.hasErrors()) {
            credential.setId(id);
            return "update-credential";
        }
        credential.setUserpass(name);
        credentialService.save(credential);
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        credentialService.delete(id);
        return "redirect:/index";
    }
}
