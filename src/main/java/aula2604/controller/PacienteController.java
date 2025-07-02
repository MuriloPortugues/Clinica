package aula2604.controller;
import aula2604.model.entity.Paciente;
import aula2604.model.entity.Role;
import aula2604.model.entity.Usuario;
import aula2604.model.repository.PacienteRepository;
import aula2604.model.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Transactional
@Controller
@RequestMapping("paciente")
public class PacienteController {

    @Autowired
    PacienteRepository repositoryPaciente;

    @Autowired
    UsuarioRepository repositoryUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @GetMapping("/form")
    public String formPaciente(Paciente paciente){
        return "/paciente/form";
    }


    @GetMapping("/apresentarPaciente")
    public ModelAndView listarPaciente(ModelMap model) {
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        return new ModelAndView("/paciente/list", model);
    }

    @PostMapping("/savePaciente")
    @Transactional
    public Object savePaciente(@Valid Paciente paciente,
                               BindingResult result,
                               @RequestParam String login,
                               @RequestParam String password) {

        if (result.hasErrors()) {
            return "/paciente/form";
        }

        // 1. Criar e configurar o usuário
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setPassword(passwordEncoder.encode(password));

        // 2. Buscar ou criar a role de paciente
        String roleName = "ROLE_PACIENTE";
        List<Role> roles = em.createQuery("FROM Role r WHERE r.nome = :nome", Role.class)
                .setParameter("nome", roleName)
                .getResultList();

        Role rolePaciente;
        if (roles.isEmpty()) {
            rolePaciente = new Role(roleName);
            em.persist(rolePaciente);
        } else {
            rolePaciente = roles.get(0);
        }

        usuario.getRoles().add(rolePaciente);
        repositoryUsuario.saveUsuario(usuario); // opcional com Cascade, mas seguro

        // 3. Vincular ao paciente
        paciente.setUsuario(usuario);

        // 4. Salvar paciente
        repositoryPaciente.savePaciente(paciente);

        return new ModelAndView("redirect:/paciente/form?cadastrado=true");
    }

    @GetMapping("/removePaciente/{id}")
    public ModelAndView removePaciente(@PathVariable("id") Long id){
        repositoryPaciente.removePaciente(id);
        return new ModelAndView("redirect:/paciente/apresentarPaciente");
    }

    @GetMapping("/editPaciente/{id}")
    public ModelAndView editPaciente(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("paciente", repositoryPaciente.paciente(id));
        return new ModelAndView("/paciente/form", model);
    }

    @PostMapping("/updatePaciente")
    public ModelAndView updatePaciente(Paciente paciente) {
        repositoryPaciente.updatePaciente(paciente);
        return new ModelAndView("redirect:/paciente/apresentarPaciente");
    }

    @GetMapping("/buscarPorNome")
    public ModelAndView buscarPorNome(@RequestParam("nome") String nome, ModelMap model) {
        List<Paciente> pacientes = repositoryPaciente.buscarPorNome(nome);
        model.addAttribute("pacientes", pacientes);
        return new ModelAndView("/paciente/list", model);
    }

}
