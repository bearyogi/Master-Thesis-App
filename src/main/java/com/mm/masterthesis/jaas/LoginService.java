package com.mm.masterthesis.jaas;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class LoginService {

    public Subject login(LoginContext loginContext) throws LoginException {
        loginContext.login();
        return loginContext.getSubject();
    }
}
