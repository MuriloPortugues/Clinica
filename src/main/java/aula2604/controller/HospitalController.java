package aula2604.controller;

import aula2604.model.entity.Usuario;
import aula2604.model.repository.HospitalRepository;
import aula2604.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("hospital")
public class HospitalController {

    @Autowired
    HospitalRepository repositoryHospital;

    @Autowired
    UsuarioRepository repositoryUsuario;

    @GetMapping("/home")
        public ModelAndView apresentarHome(ModelMap model) {
            return new ModelAndView("/hospital/home", model);
        }

    @GetMapping("/login")
        public ModelAndView login(ModelMap model) {
        return new ModelAndView("/hospital/login", model);
    }


}
