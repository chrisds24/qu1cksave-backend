package com.qu1cksave.qu1cksave_backend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "member")
public class User {
    // IMPORTANT:
    // - email, password, name, and roles aren't marked as NOT NULL in the
    //   schema. But I'm marking it as not nullable here since that is the
    //   correct thing to do

    @Generated
    @Id
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // TODO:
    //  https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#mapping-embeddables
    //  - According to the link, JSON arrays aren't supported, so maybe this
    //    might not work
    //  - UPDATE: It does work
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String[] roles;

    // TODO: I need a User-type class that has an optional accessToken

    // Constructors

    protected User() {}

    public User(
        UUID id,
        String email,
        String password,
        String name,
        String[] roles
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String[] getRoles() { return roles; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setRoles(String[] roles) { this.roles = roles; }
}

// NOTES: (6/23/25) https://www.marcobehler.com/guides/spring-security
//  - Need a WebSecurityConfig, which I should configure
//    -- Seems like this doesn't go into Qu1cksaveBackendConfiguration, not sure though
//    -- Search "multiple configuration class in spring boot"
//  - I have my user info and hashed passwords in my database, so I'll need to
//    define a UserDetailsService bean or implement a UserDetails interface
//    -- The UserDetailsService calls my custom user details service class
//       which implements UserDetailsService and returns a UserDetails object,
//       which is the user wrapped into a UserDetails object
//       + ME: Then I can probably just have my User entity (or a UserDto)
//         implement UserDetails
//    -- As an alternative, I can use off-the-shelf implementations by Spring
//       Security.
//       + In this section, Marco mentions that I can just make my entities
//         implement the UserDetails interface
//    -- I'll also need a password encoder (I can use BCryptPasswordEncoder)
//  - Regarding authorities:
//    -- The roles data in my database doesn't have a ROLE_ prefix. Can I just
//       prepend this after the user?
//    -- ME: Or maybe I can just remove the need for it?
//       + https://www.baeldung.com/spring-security-remove-role_prefix
//       + Removing ROLE_ prefix Spring Security
//    -- When returning the User entity (or dto), the authorities must be
//       stored as a list of SimpleGrantedAuthority
//    -- Can configure WebSecurityConfig to configure which endpoints/URLs
//       require which authorities
//  - I can just disable CSRF protection, which Marco mentioned
//    doesn't make sense for a stateless REST API
//  - Additionally, I can enable @EnableGlobalMethodSecurity to protect
//    Controllers, for example. Add this ApplicationContextConfiguration
//    -- I can just use @Secured on the controllers
//    -- https://stackoverflow.com/questions/28648576/when-should-we-use-preauthorize-and-secured
//       + @PreAuthorize vs @Secured
//  - @AuthenticationPrincipal to inject the authenticated principal to
//    controllers
//    -- @AuthenticationPrincipal will inject a principal if a user is authenticated,
//       or null if no user is authenticated
//    -- Can also get principal through SecurityContextHolder (legacy way of
//       doing things)
//       + Spring Security by default will set an AnonymousAuthenticationToken as
//         authentication on the SecurityContextHolder, if you are not logged in.
//         This leads to some confusion, as people would naturally expect a null value there
//  - Spring Boot only auto-configures Spring Security when adding the
//    spring-boot-starter-security dependency. Every security config is done
//    via plain Spring Security concepts
//    -- Just need to add this dependency and I can immediately start writing
//       my WebSecurityConfigurerAdapter
//  - ------------- To-do (6/24/25) ------------
//  - Read (DONE)
//    -- https://spring.io/guides/gs/securing-web
//       + Has nothing about stateless/sessionless REST
//    -- Spring Boot w/ Spring Security:
//       + https://docs.spring.io/spring-boot/reference/web/spring-security.html
//         * Also nothing about sessionless REST
//    -- Docs: https://docs.spring.io/spring-security/reference/index.html
//  - (NOT DONE)
//  - Does Spring Security make sense for a stateless REST API? (SEARCH THIS)
//    -- Also: Is spring security suited for jwt authentication rest
//  - Need to disable default login + logout page
//  - Search "disable session spring security"
//    -- There's never, stateless, etc.


//  Docs
//  https://docs.spring.io/spring-security/reference/servlet/getting-started.html#servlet-hello-auto-configuration
//  - Runtime Expectations is useful for what Spring Security can provide
//  - Spring Boot has a security auto configuration (shown here)
//    -- Notice how some are overwritten when you create certain beans
//  - REST API with JWT:
//    -- "I am building a REST API, and I need to authenticate a JWT or other bearer token"
//        + https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html


//  Stateless/Sessionless REST API
//  - https://www.baeldung.com/csrf-stateless-rest-api
//    -- Read 2.3. Credentials Stored in Cookies, which talks about only using
//       cookies to store the JWT and not for authenticating (which is what I'm doing)
//  - https://stackoverflow.com/questions/48985293/spring-security-stateless-rest-service-and-csrf
//    -- I should just disable csrf
//  - https://stackoverflow.com/questions/75571606/if-using-jwt-token-there-is-a-need-of-spring-security
//    -- This question linked the very useful springframework.guru link below
//  - https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/
//    -- Doesn't use Spring Security
//    -- Really good. Even has some code on how to work with JWT
//    -- This one uses a filter for authentication too (to check the jwt in
//       the header)
//       + In addition, I need a way to extract the authorities from the jwt
//  - https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
//    -- Also has info when working with JWTs
//  - https://stackoverflow.com/questions/75117913/how-do-i-manually-register-filters-in-springboot
//    -- Also uses FilterRegistrationBean
//  - Can register different filters by having multiple FilterRegistrationBeans
//  - https://docs.spring.io/spring-security/reference/servlet/architecture.html
//    -- Has really nice non Spring Security specific info about filters
//  - https://www.baeldung.com/spring-boot-add-filter
//    -- GREAT SOURCE :)



