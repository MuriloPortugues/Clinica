package aula2604;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracaoSpringMvc implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("redirect:/veiculo/list");
//        registry.addRedirectViewController("/","/veiculo/list");
        registry.addViewController("/").setViewName("forward:/hospital/home");
//        registry.addViewController("/").setViewName("/home"); //aponta para a página
    }
}