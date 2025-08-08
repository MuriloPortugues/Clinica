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
        medico.setNome(paciente.getNome()); // preenche o nome do paciente

        model.addAttribute("medico", medico);
        model.addAttribute("pacienteId", paciente.getId()); // necessário para atualizar a ROLE

        return new ModelAndView("/medico/form", model);
    }

    @GetMapping("/apresentarMedico")
    public ModelAndView listarMedico(ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // login do usuário logado

        // Verificar roles do usuário
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMedico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));

        List<Medico> medicos;

        if (isAdmin) {
            // Admin vê todos os médicos
            medicos = repositoryMedico.medicos();
        } else if (isMedico) {
            // Médico vê só ele mesmo
            Usuario usuarioLogado = repositoryUsuario.usuario(username);
            // Busca o médico pelo nome associado ao usuário
            // Presumindo que o nome do médico é igual ao nome do usuário (ou adapte se tiver relacionamento direto)
            medicos = repositoryMedico.buscarPorNome(usuarioLogado.getLogin());

        } else {
            // Caso outros roles, retorna lista vazia ou uma mensagem de acesso negado
            medicos = List.of();
        }

        model.addAttribute("medicos", medicos);
        return new ModelAndView("/medico/list", model);
    }

    @PostMapping("/saveMedico")
    public Object saveMedico(
            @Valid Medico medico,
            BindingResult result,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId) {

        if (result.hasErrors())
            return "/medico/form";

        Usuario usuario = null;

        if (pacienteId != null) {
            Paciente paciente = repositoryPaciente.paciente(pacienteId);
            if (paciente != null && paciente.getUsuario() != null) {
                usuario = repositoryUsuario.usuario(paciente.getUsuario().getId());
            }
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            usuario = repositoryUsuario.usuario(username);
        }

        if (usuario != null) {
            if (usuario.getRoles() == null) {
                usuario.setRoles(new ArrayList<>());
            }

            Role roleMedico = roleRepository.findByNome("ROLE_MEDICO");
            if (!usuario.getRoles().contains(roleMedico)) {
                usuario.getRoles().add(roleMedico);
            }

            // 🔁 Reassociar corretamente as duas pontas da relação
            usuario.setMedico(medico);  // seta o médico no usuário
            medico.setUsuario(usuario); // seta o usuário no médico

            // 🔄 Salva apenas o médico (o Cascade.ALL salvará o usuário automaticamente)
            medico = repositoryMedico.saveMedico(medico);

        } else {
            // fallback
            medico = repositoryMedico.saveMedico(medico);
        }

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
