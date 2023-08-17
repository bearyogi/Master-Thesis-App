package com.mm.masterthesis.jaas;

import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.callback.*;

@Component
@RequiredArgsConstructor
public class ConsoleCallbackHandler implements CallbackHandler {
    private String username;
    private final UserService userService;

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback nameCallback) {
                nameCallback.setName(username);

            } else if (callback instanceof PasswordCallback passwordCallback) {
                User user = userService.findByName(username);
                passwordCallback.setPassword(user.getPassword().toCharArray());

            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
