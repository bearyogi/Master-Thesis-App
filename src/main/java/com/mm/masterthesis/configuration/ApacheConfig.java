package com.mm.masterthesis.configuration;

import com.mm.masterthesis.realm.MyRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.subject.Subject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
public class ApacheConfig {

    @Bean
    public MyRealm realm() {
        MyRealm myRealm = new MyRealm();
        SecurityManager securityManager = new DefaultSecurityManager(myRealm);
        SecurityUtils.setSecurityManager(securityManager);
        return myRealm;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/login", "authc"); // need to accept POSTs from the login form
        chainDefinition.addPathDefinition("/index/**", "authc");
        return chainDefinition;
    }

    @ModelAttribute(name = "subject")
    @Bean
    public Subject subject() {
        return SecurityUtils.getSubject();
    }
}
