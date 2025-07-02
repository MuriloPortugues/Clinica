package aula2604.model.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration //classe de configuração
@EnableWebSecurity //indica ao Spring que serão definidas configurações personalizadas de segurança
public class SecurityConfiguration {

    @Autowired
    UsuarioDetailsConfig usuarioDetailsConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                customizer ->
                    customizer
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/paciente/form").permitAll()
                            .requestMatchers("/paciente/apresentarPaciente").permitAll()
                            .requestMatchers("/agenda/disponibilizar").hasAnyRole("MEDICO", "ADMIN")
                            .requestMatchers("/agenda/list").hasAnyRole("PACIENTE", "ADMIN", "MEDICO")
                            .requestMatchers("/paciente/apresentarPaciente").hasAnyRole("PACIENTE", "ADMIN")
                            .requestMatchers("/medico/apresentarMedico").hasAnyRole("MEDICO", "ADMIN")
                            .requestMatchers(HttpMethod.POST,"/paciente/savePaciente").permitAll()
                            .anyRequest() //define que a configuração é válida para qualquer requisição.
                            .authenticated() //define que o usuário precisa estar autenticado.
                )
                .formLogin(customizer ->
                            customizer
                                    .loginPage("/hospital/login") //passamos como parâmetro a URL para acesso à página de login que criamos
                                    .loginProcessingUrl("/login") // Importante: o endpoint que processa POST do formulário
                                    .defaultSuccessUrl("/hospital/home", true)
                                    .failureUrl("/hospital/login?error=true")
                                    .permitAll() //define que essa página pode ser acessada por todos, independentemente do usuário estar autenticado ou não.
                )
                .httpBasic(withDefaults()) //configura a autenticação básica (usuário e senha)
                .logout(LogoutConfigurer::permitAll) //configura a funcionalidade de logout no Spring Security.
                .rememberMe(withDefaults()); //permite que os usuários permaneçam autenticados mesmo após o fechamento do navegador
        return http.build();
    }

//        @Bean
//        public InMemoryUserDetailsManager userDetailsService() {
//            UserDetails user1 = User.withUsername("user")
//                    .password(passwordEncoder().encode("123"))
//                    .roles("USER")
//                    .build();
//            UserDetails user2 = User.withUsername("paciente")
//                    .password(passwordEncoder().encode("123"))
//                    .roles("PACIENTE")
//                    .build();
//            UserDetails user3 = User.withUsername("medico")
//                    .password(passwordEncoder().encode("123"))
//                    .roles("MEDICO")
//                    .build();
//            UserDetails admin = User.withUsername("admin")
//                    .password(passwordEncoder().encode("admin"))
//                    .roles("ADMIN")
//                    .build();
//            return new InMemoryUserDetailsManager(user1, user2, user3, admin);
//        }

    @Autowired
    public void configureUserDetails(final AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(usuarioDetailsConfig).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * Com o método, instanciamos uma instância do encoder BCrypt e deixando o controle dessa instância como responsabilidade do Spring.
     * Agora, sempre que o Spring Security necessitar condificar um senha, ele já terá o que precisa configurado.
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
