package com.mm.masterthesis.jaas;

import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.UserService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryLoginModule implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;
    private String username = "";
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
    private boolean loginSucceeded = false;
    private Principal userPrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username: ");
        PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
            String sn = nameCallback.getName();
            String ps = new String(passwordCallback.getPassword());
            if (sn != null && !ps.isEmpty()) {
                username = sn;
                loginSucceeded = true;
                commit();
            }
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Can't login");
        }
        return loginSucceeded;
    }

    @Override
    public boolean commit() {
        if (!loginSucceeded) {
            return false;
        }
        userPrincipal = new UserPrincipal(username);
        subject.getPrincipals().add(userPrincipal);
        return true;
    }

    @Override
    public boolean abort() {
        logout();
        return true;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().remove(userPrincipal);
        return false;
    }

}
