package aula2604.controller;

import aula2604.model.entity.Role;
import aula2604.model.entity.Usuario;
import aula2604.model.repository.HospitalRepository;
import aula2604.model.repository.RoleRepository;
import aula2604.model.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
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

import java.util.List;

@Transactional
@Controller
@RequestMapping("hospital")
public class HospitalController {

    @Autowired
    HospitalRepository repositoryHospital;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UsuarioRepository repositoryUsuario;

    @Autowired
    EntityManager em;

    @GetMapping("/home")
    public ModelAndView apresentarHome(ModelMap model) {
        repositoryHospital.inserirRolePacienteSeNaoExistir(); // Chama o método do repositório
        return new ModelAndView("/hospital/home", model);
    }

    @GetMapping("/login")
        public ModelAndView login(ModelMap model) {
        boolean existeAdmin = roleRepository.existsRoleAdmin();
        model.addAttribute("existeRoleAdmin", existeAdmin);
        return new ModelAndView("/hospital/login", model);
    }

}
