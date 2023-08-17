package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.CredentialService;
import com.mm.masterthesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;
    private final UserService userService;

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        if(checkRole(Collections.singletonList("admin"))) {
            String user = SecurityUtils.getSubject().getSession(false).getAttribute("name") + "";
                model.addAttribute("credentials", credentialService.findAll());
                return "admin";
        } else {
            return "admin-bad";
        }
    }

    @GetMapping("/addcredential")
    public String addCredentialForm() {
        if(checkRole(List.of("user", "admin"))) {
            return "add-credential";
        } else {
            return "admin-bad";
        }
    }

    @PostMapping("/addcredential")
    public String addCredential(@Valid Credential credential, BindingResult result, Model model) {
        if(checkRole(List.of("user", "admin"))) {
            if (result.hasErrors()) {
                return "add-credential";
            }
            if (credential.getResource().isEmpty() || credential.getPassword().isEmpty()) {
                model.addAttribute("newCredentialEmpty", true);
                return "add-credential";
            }
            String name = SecurityUtils.getSubject().getSession(false).getAttribute("name") + "";
            credential.setUserpass(name);
            credentialService.save(credential);
            return "redirect:/index";
        } else {
            return "admin-bad";
        }
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        if(checkRole(List.of("user", "admin"))) {
            Credential credential = credentialService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credential Id:" + id));

            model.addAttribute("credential", credential);
            return "update-credential";
        } else {
            return "admin-bad";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCredential(@PathVariable("id") long id, @Valid Credential credential,
                                   BindingResult result) {
        if(checkRole(List.of("user", "admin"))) {
            if (result.hasErrors()) {
                credential.setId(id);
                return "update-credential";
            }

            String userId = credentialService.findById(id).get().getUserpass();
            credential.setUserpass(userId);
            credentialService.save(credential);
            return "redirect:/index";
        } else {
            return "admin-bad";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        if (checkRole(List.of("user","admin"))) {
            String userId = credentialService.findById(id).get().getUserpass();
            credentialService.delete(id);
            return "redirect:/index";
        } else {
            return "admin-bad";
        }
    }

    public boolean checkRole(List<String> roles) {
        return roles.contains(SecurityUtils.getSubject().getSession(true).getAttribute("role"));
    }
}
