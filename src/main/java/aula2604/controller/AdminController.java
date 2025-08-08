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
            // mantém o objeto role e retorna para o formulário
            model.addAttribute("role", role);
            return "hospital/formRole";
        }

        if (role.getNome() != null) {
            String nomeCorrigido = role.getNome().replace(",", "").trim();

            if (!nomeCorrigido.toUpperCase().startsWith("ROLE_")) {
                role.setNome("ROLE_" + nomeCorrigido.toUpperCase());
            } else {
                role.setNome(nomeCorrigido.toUpperCase());
            }
        }

        Role existente = repositoryRole.findByNome(role.getNome());
        if (existente != null) {
            model.addAttribute("erro", "Já existe um perfil com esse nome.");
            model.addAttribute("role", role);  // importante para preencher o formulário
            return "hospital/formRole";
        }

        repositoryRole.save(role);
        return new ModelAndView("redirect:/admin/formRole?cadastrado=true");
    }



    @GetMapping("/editarRole/{id}")
    public ModelAndView editar(@PathVariable Long id, ModelMap model) {
        Role role = repositoryRole.findById(id);
        if (role == null) {
            return new ModelAndView("redirect:/hospital/formRole?erro=true");
        }

        // Só exibe o sufixo no campo, mas salva corretamente depois
        String sufixo = role.getNome().replace("ROLE_", "");
        role.setNome(sufixo);

        model.addAttribute("role", role);
        return new ModelAndView("hospital/formRole", model);
    }

    @PostMapping("/updateRole")
    public ModelAndView updateRole(Role role) {
        // Remove vírgulas indesejadas e espaços
        String nomeCorrigido = role.getNome().replace(",", "").trim();

        // Garante o prefixo ROLE_
        if (!nomeCorrigido.toUpperCase().startsWith("ROLE_")) {
            role.setNome("ROLE_" + nomeCorrigido.toUpperCase());
        } else {
            role.setNome(nomeCorrigido.toUpperCase());
        }

        repositoryRole.updateRole(role);
        return new ModelAndView("redirect:/admin/apresentarRole");
    }

    @GetMapping("/excluirRole/{id}")
    public ModelAndView excluirRole(@PathVariable("id") Long id, ModelMap model) {
        if (repositoryRole.isRoleInUse(id)) {
            model.addAttribute("erro", "Não é possível excluir. Perfil está em uso por um ou mais usuários.");
        } else {
            repositoryRole.deletarRole(id);
            model.addAttribute("msg", "Perfil excluído com sucesso.");
        }

        model.addAttribute("roles", repositoryRole.roles());
        return new ModelAndView("hospital/role", model);
    }
}

