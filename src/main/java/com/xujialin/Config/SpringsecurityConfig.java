package com.xujialin.Config;

import com.xujialin.Handler.*;
import com.xujialin.Repository.MyPersistentTokenRepository;
import com.xujialin.SafetyVerification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * @author XuJiaLin
 * @date 2021/7/17 21:02
 */
@Configuration
public class SpringsecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CustomizeAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomizeFilterMetadataSource metadataSource;

    @Autowired
    private CustomizeAccessDecisionManager manager;

    //@Autowired
    //private CustomizeAbstractSecurityInterceptor interceptor;

    @Autowired
    private CustomizeAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomizeAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomizeAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginProcessingUrl("/login").permitAll()  //????????????
                .successHandler(authenticationSuccessHandler) //?????????????????????
                .failureHandler(authenticationFailureHandler) //?????????????????????
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler) //???????????????????????????
                .authenticationEntryPoint(authenticationEntryPoint); // ???????????????????????????????????????

        http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(metadataSource);
                o.setAccessDecisionManager(manager);
                return o;
            }
        });

            http.logout().logoutUrl("/logout")
                    .logoutSuccessHandler(customizeLogoutSuccessHandler)
                    .deleteCookies("JSESSIONID")
                    .permitAll();
        ;

        //??????csrf??????
        http.csrf().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

    }
}
