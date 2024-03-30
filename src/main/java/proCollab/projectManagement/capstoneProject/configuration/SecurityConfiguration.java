package proCollab.projectManagement.capstoneProject.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final DataSource dataSource;
        private final BCryptPasswordEncoder bCryptPasswordEncoder;

        @Autowired
        public SecurityConfiguration(
                        BCryptPasswordEncoder bCryptPasswordEncoder, DataSource dataSource) {
                this.bCryptPasswordEncoder = bCryptPasswordEncoder;
                this.dataSource = dataSource;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
                http
                                .authorizeRequests()
                                .antMatchers("/projects/createProject").hasRole("ADMIN")
                                .antMatchers("/register", "/", "/login", "/about", "/css/**", "/webjars/**",
                                                "/forgotPassword/**", "/forgot-password/**", "/reset-password/**",
                                                "/loading", "/websocket", "/project/**")
                                .permitAll()
                                .antMatchers("/project/projectTasks/**").hasAnyRole("ADMIN", "USER", "SUPERADMIN")
                                .antMatchers("/company/**", "/proAdmin/**").hasAnyRole("PROADMIN")
                                .antMatchers("/tasks/**", "/task/**", "/users", "/user/**",
                                                "/projects/**", "/dashboard/**", "/notes", "/notes/**")
                                .hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                                .antMatchers("/changePassword", "/changePassword/**",
                                                "/h2-console/**", "/profile/**",
                                                "/changePassword/changeUserPassword")
                                .hasAnyRole("USER, ADMIN,SUPERADMIN,PROADMIN")
                                .antMatchers("/assignment/**")
                                .hasAnyRole("ADMIN,SUPERADMIN")
                                .antMatchers("/superAdmin/**", "/superadmin").hasRole("SUPERADMIN")
                                .and()
                                .formLogin()
                                .loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/profile", true)
                                .and()
                                .logout()
                                .logoutSuccessUrl("/login");

                http.csrf().ignoringAntMatchers("/h2-console/**", "/notes/**", "/forgot-password", "/reset-password",
                                "/company/**", "/projects/**", "/dashboard/**", "/websocket", "/project/**","/upload/fromExcel");
                http.headers().frameOptions().sameOrigin();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.jdbcAuthentication()
                                .usersByUsernameQuery(
                                                "select email as principal, password as credentials, true from user where email=?")
                                .authoritiesByUsernameQuery(
                                                "select u.email as principal, r.role as role from user u inner join user_role ur on(u.user_id=ur.user_id) inner join role r on(ur.role_id=r.role_id) where u.email=?")
                                .dataSource(dataSource)
                                .passwordEncoder(bCryptPasswordEncoder)
                                .rolePrefix("ROLE_");
        }
}