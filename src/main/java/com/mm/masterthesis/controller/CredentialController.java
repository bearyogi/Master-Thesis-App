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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping("/index")
    public String showCredentialList(@RequestParam String userId, Model model) {
        model.addAttribute("credentials", credentialService.findAllForUser(userId));
        model.addAttribute("userId", userId);
        return "index";
    }

    @GetMapping("/addcredential")
    public String addCredentialForm(@RequestParam String userId, Model model) {
        model.addAttribute("userId", userId);
            return "add-credential";
    }

    @PostMapping("/addcredential/{userId}")
    public String addCredential(@Valid Credential credential, BindingResult result, Model model, @PathVariable("userId") String userId) {
        if (result.hasErrors()) {
            return "add-credential";
        }
        if(credential.getResource().isEmpty() || credential.getPassword().isEmpty()){
            model.addAttribute("newCredentialEmpty", true);
            return "add-credential";
        }
        credential.setUserpass(userId.substring(7));
        credentialService.save(credential);
        return "redirect:/index?userId="+userId.substring(7);
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Credential credential = credentialService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credential Id:" + id));

        model.addAttribute("credential", credential);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateCredential(@PathVariable("id") long id, @Valid Credential credential,
                                   BindingResult result) {
        if (result.hasErrors()) {
            credential.setId(id);
            return "update-user";
        }
        credentialService.save(credential);
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        credentialService.delete(id);
        return "redirect:/index";
    }
}
