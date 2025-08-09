package aula2604.controller;

import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import aula2604.model.entity.Role;
import aula2604.model.entity.Usuario;
import aula2604.model.repository.MedicoRepository;
import aula2604.model.repository.PacienteRepository;
import aula2604.model.repository.RoleRepository;
import aula2604.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Transactional
@Controller
@RequestMapping("medico")
public class MedicoContoller {

    @Autowired
    MedicoRepository repositoryMedico;

    @Autowired
    PacienteRepository repositoryPaciente;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository repositoryUsuario;

    @GetMapping("/form")
    public String formMedico(Medico medico){
        return "/medico/form";
    }

    @GetMapping("/formPaciente/{id}")
    public ModelAndView formComPaciente(@PathVariable("id") Long id, ModelMap model) {
        Paciente paciente = repositoryPaciente.paciente(id);
        if (paciente == null) {
            return new ModelAndView("redirect:/paciente/apresentarPaciente");
        }

        Medico medico = new Medico();
        medico.setNome(paciente.getNome()); // preenche o nome

        model.addAttribute("medico", medico);
        model.addAttribute("pacienteId", paciente.getId());

        if (paciente.getUsuario() != null) {
            model.addAttribute("usuarioId", paciente.getUsuario().getId());
            medico.setUsuario(paciente.getUsuario());
        } else {
            model.addAttribute("usuarioId", null);
        }

        return new ModelAndView("/medico/form", model);
    }

    @GetMapping("/apresentarMedico")
    public ModelAndView listarMedico(ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMedico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));
        List<Medico> medicos;

        if (isAdmin) {
            medicos = repositoryMedico.medicos();
        } else if (isMedico) {
            Usuario usuarioLogado = repositoryUsuario.usuario(username);
            medicos = repositoryMedico.buscarPorNome(usuarioLogado.getLogin());
        } else {
            medicos = List.of();
        }

        model.addAttribute("medicos", medicos);
        return new ModelAndView("/medico/list", model);
    }

    @PostMapping("/saveMedico")
    @Transactional
    public Object saveMedico(
            @Valid Medico medico,
            BindingResult result,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {

        if (result.hasErrors()) {
            return "/medico/form";
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = repositoryUsuario.usuario(usuarioId);
        } else if (pacienteId != null) {
            Paciente paciente = repositoryPaciente.paciente(pacienteId);
            if (paciente != null) usuario = paciente.getUsuario();
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            usuario = repositoryUsuario.usuario(username);
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário para associação não encontrado.");
        }

        Role roleMedico = roleRepository.findByNome("ROLE_MEDICO");
        if (roleMedico != null && !usuario.getRoles().contains(roleMedico)) {
            usuario.getRoles().add(roleMedico);
        }

        medico.setUsuario(usuario);
        repositoryMedico.saveMedico(medico);
        repositoryUsuario.updateUsuario(usuario);
        return new ModelAndView("redirect:/medico/apresentarMedico");
    }


    @GetMapping("/removeMedico/{id}")
    public ModelAndView removeMedico(@PathVariable("id") Long id){
        repositoryMedico.removeMedico(id);
        return new ModelAndView("redirect:/medico/apresentarMedico");
    }

    @GetMapping("/editMedico/{id}")
    public ModelAndView editMedico(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("medico", repositoryMedico.medico(id));
        return new ModelAndView("/medico/form", model);
    }

    @PostMapping("/updateMedico")
    public ModelAndView updateMedico(Medico medico) {
        repositoryMedico.updateMedico(medico);
        return new ModelAndView("redirect:/medico/apresentarMedico");
    }

    @GetMapping("/buscarPorNome")
    public ModelAndView buscarPorNome(@RequestParam("nome") String nome, ModelMap model) {
        List<Medico> medicos = repositoryMedico.buscarPorNome(nome);
        model.addAttribute("medicos", medicos);
        return new ModelAndView("/medico/list", model);
    }

}
