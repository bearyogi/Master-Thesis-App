package com.mm.masterthesis.controller;

import com.mm.masterthesis.domain.Credential;
import com.mm.masterthesis.domain.User;
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

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;
    private final UserService userService;

    @GetMapping("/index")
    public String showCredentialList(@RequestParam String userId, Model model) {
        model.addAttribute("credentials", credentialService.findAllForUser(userId));
        model.addAttribute("userId", userId);
        return "index";
    }

    @GetMapping("/admin")
    public String showAdminPanel(@RequestParam String userId, Model model) {
        User user = userService.findById(Long.valueOf(userId)).get();
        if(Objects.equals(user.getRole(), "user") || user.getRole() == null || user.getRole().isEmpty()){
            model.addAttribute("userId", userId);
            return "admin-bad";
        } else {
            model.addAttribute("credentials", credentialService.findAll());
            model.addAttribute("userId", userId);
            return "admin";
        }
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
        return "update-credential";
    }

    @PostMapping("/update/{id}")
    public String updateCredential(@PathVariable("id") long id, @Valid Credential credential,
                                   BindingResult result) {
        if (result.hasErrors()) {
            credential.setId(id);
            return "update-credential";
        }

        String userId = credentialService.findById(id).get().getUserpass();
        credential.setUserpass(userId);
        credentialService.save(credential);
        return "redirect:/index?userId="+userId;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        String userId = credentialService.findById(id).get().getUserpass();
        credentialService.delete(id);
        return "redirect:/index?userId="+userId;
    }
}
