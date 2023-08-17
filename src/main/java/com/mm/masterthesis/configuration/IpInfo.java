package com.mm.masterthesis.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IpInfo implements AuthenticationInfo {
    private String ip;
    private int loginAttempts;

    @Override
    public PrincipalCollection getPrincipals() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
