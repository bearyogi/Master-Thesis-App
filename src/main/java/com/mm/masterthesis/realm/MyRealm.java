package com.mm.masterthesis.realm;

import com.mm.masterthesis.configuration.IpInfo;
import com.mm.masterthesis.domain.User;
import com.mm.masterthesis.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;

import java.util.HashMap;

import static java.util.List.*;

public class MyRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    private HashMap<String, IpInfo> cache = new HashMap<>();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = userService.findByName(username);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(of(user.getRole()));
        //authorizationInfo.setStringPermissions(user.getPermissions());
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        User user = userService.findByName(username);

        if(checkDos(upToken.getHost())) {
            throw new RuntimeException("IP Blocked!");
        } else {
            addLoginAttempt(upToken.getHost());
        }

        if (user == null) {
            throw new UnknownAccountException("No account found for user [" + username + "]");
        }

        if(!user.getPassword().equals(new Sha256Hash(upToken.getPassword(), user.getSalt()).toString())){
            throw new CredentialsException("Password does not match!");
        } else {
            upToken.setPassword(new Sha256Hash(upToken.getPassword(), user.getSalt()).toString().toCharArray());
        }

        return new SimpleAuthenticationInfo(user.getName(), user.getPassword(), getName());
    }

    private boolean checkDos(String ip) {
        IpInfo ipInfo = cache.get(ip);
        if(ipInfo != null){
            return ipInfo.getLoginAttempts() > 10;
        } else {
            return false;
        }

    }

    private void addLoginAttempt(String ip) {
        if(cache.get(ip) == null){
            cache.put(ip, new IpInfo(ip, 1));
        } else {
            IpInfo ipInfo = cache.get(ip);
            cache.remove(ip);
            ipInfo.setLoginAttempts(ipInfo.getLoginAttempts() + 1);
            cache.put(ip, ipInfo);
        }
    }
}
