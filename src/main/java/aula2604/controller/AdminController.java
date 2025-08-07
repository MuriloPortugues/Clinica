package aula2604.controller;


import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import aula2604.model.entity.Role;
import aula2604.model.repository.PacienteRepository;
import aula2604.model.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private PacienteRepository repositoryPaciente;

    @Autowired
    private RoleRepository repositoryRole;

    @GetMapping("/apresentarPaciente")
    public ModelAndView listarPacientes(ModelMap model) {
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        return new ModelAndView("/paciente/list", model);
    }

    @GetMapping("/removePaciente/{id}")
    public ModelAndView removePaciente(@PathVariable("id") Long id) {
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

    @GetMapping("/apresentarRole")
    public ModelAndView apresentarRole(ModelMap model) {
        model.addAttribute("roles", repositoryRole.roles());
        return new ModelAndView("/hospital/role", model);
    }

    @GetMapping("/formRole")
    public String formRole(ModelMap model) {
        model.addAttribute("role", new Role());
        return "hospital/formRole";
    }

    @PostMapping("/saveRole")
    public Object save(@Valid @ModelAttribute("role") Role role, BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            return "admin/formRole";
        }

        // Verifica se já existe uma Role com o mesmo nome
        Role existente = repositoryRole.findByNome(role.getNome());
        if (existente != null) {
            model.addAttribute("erro", "Já existe um perfil com esse nome.");
            return "admin/formRole";
        }

        repositoryRole.save(role);
        return new ModelAndView("redirect:/admin/formRole?cadastrado=true");

    }

    @GetMapping("/editarRole/{id}")
    public ModelAndView editarRole(@PathVariable Long id) {
        Role role = repositoryRole.findById(id);
        ModelAndView mv = new ModelAndView("hospital/formRole");
        mv.addObject("role", role);
        return mv;
    }
}

