package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import static org.springframework.security.core.context.SecurityContextHolder.*;

@Controller
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping("/index")
    public String showCredentialList(Model model) {
        model.addAttribute("credentials", credentialService.findAllForUser(getUserName()));
        return "index";
    }

    @GetMapping("/admin")
    public String showAdminPanel(Model model) {
        model.addAttribute("credentials", credentialService.findAll());
        return "admin";
    }

    @GetMapping("/addcredential")
    public String addCredentialForm() {
        return "add-credential";
    }

    @PostMapping("/addcredential")
    public String addCredential(@Valid Credential credential, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-credential";
        }
        if (credential.getResource().isEmpty() || credential.getPassword().isEmpty()) {
            model.addAttribute("newCredentialEmpty", true);
            return "add-credential";
        }
        credential.setUserpass(getUserName());
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
        if (result.hasErrors()) {
            credential.setId(id);
            return "update-credential";
        }

        String userName = credentialService.findById(id).get().getUserpass();
        credential.setUserpass(userName);
        credentialService.save(credential);
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        credentialService.delete(id);
        return "redirect:/index";
    }

    private String getUserName() {
        return getContext().getAuthentication().getName();
    }
}
