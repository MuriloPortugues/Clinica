package aula2604.controller;


import aula2604.model.entity.Agenda;
import aula2604.model.entity.Consulta;
import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import aula2604.model.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Transactional
@Controller
@RequestMapping("consulta")
public class ConsultaController {

    @Autowired
    ConsultaRepository repositoryConsulta;
    @Autowired
    PacienteRepository repositoryPaciente;
    @Autowired
    MedicoRepository repositoryMedico;
    @Autowired
    AgendaRepository repositoryAgenda;
    @Autowired
    private UsuarioRepository repositoryUsuario;



    @GetMapping("/form")
    public String abrirFormularioConsulta(
            @RequestParam(required = false) Long agendaId,
            Principal principal,
            Model model) {

        Consulta consulta = new Consulta();

        if (agendaId != null) {
            Agenda agenda = repositoryAgenda.agenda(agendaId);
            if (agenda != null) {
                consulta.setAgenda(agenda);
                consulta.setData(agenda.getInicio());
                consulta.setMedico(agenda.getMedico());
            }
        }

        // Buscar paciente do usuário logado
        Paciente pacienteLogado = repositoryPaciente.pacientePorLogin(principal.getName());
        if (pacienteLogado != null) {
            consulta.setPaciente(pacienteLogado);
        }

        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        model.addAttribute("medicos", repositoryMedico.medicos());

        return "consulta/form";
    }


    @GetMapping("/apresentarConsulta")
    public ModelAndView listarConsulta(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();

        // Verifica se usuário tem ROLE_ADMIN ou ROLE_MEDICO
        boolean isAdminOrMedico = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_MEDICO"));

        List<Consulta> consultas;

        if (isAdminOrMedico) {
            // mostra todas as consultas
            consultas = repositoryConsulta.consultas();
        } else {
            // pega o paciente pelo login do usuário logado
            Paciente paciente = repositoryPaciente.pacientePorLogin(login);
            if (paciente != null) {
                consultas = repositoryConsulta.consultasPorPaciente(paciente.getId());
            } else {
                consultas = List.of(); // lista vazia caso paciente não encontrado
            }
        }

        model.addAttribute("consultas", consultas);
        return new ModelAndView("/consulta/list", model);
    }


    @PostMapping("/saveConsulta")
    public ModelAndView saveConsulta(Consulta consulta) {
        repositoryConsulta.saveConsulta(consulta);

        if (consulta.getAgenda() != null) {
            Agenda agenda = repositoryAgenda.agenda(consulta.getAgenda().getId());
            agenda.setStatus("INDISPONÍVEL");  // ou "INDISPONIVEL"
            repositoryAgenda.updateAgenda(agenda);
        }

        return new ModelAndView("redirect:/consulta/apresentarConsulta");
    }

    @GetMapping("/removeConsulta/{id}")
    public ModelAndView removeConsulta(@PathVariable("id") Long id, ModelMap model){
        // Busca a consulta pelo ID
        Consulta consulta = repositoryConsulta.consulta(id);

        // Se a consulta existe e não foi realizada...
        if (consulta != null && !"REALIZADA".equalsIgnoreCase(consulta.getStatus())) {
            // Busca a agenda associada à consulta
            Agenda agenda = consulta.getAgenda();

            // Se a agenda existir, muda o status para "DISPONIVEL"
            if(agenda != null) {
                agenda.setStatus("DISPONIVEL");
                repositoryAgenda.updateAgenda(agenda);
            }

            // Remove a consulta
            repositoryConsulta.removeConsulta(id);

            // Redireciona para a página de apresentação de consultas
            return new ModelAndView("redirect:/consulta/apresentarConsulta");
        }

        // Caso a consulta seja "REALIZADA", exibe a mensagem de erro
        if (consulta != null && "REALIZADA".equalsIgnoreCase(consulta.getStatus())) {
            model.addAttribute("mensagemErro", "Impossível cancelar, consulta já realizada.");
        } else {
            model.addAttribute("mensagemErro", "Consulta não encontrada ou já cancelada.");
        }

        // Obtém a lista de consultas novamente para exibir na página
        model.addAttribute("consultas", repositoryConsulta.consultas());

        // Retorna para a view de lista com a mensagem de erro
        return new ModelAndView("/consulta/list", model);
    }


    @GetMapping("/editConsulta/{id}")
    public ModelAndView editConsulta(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consulta", repositoryConsulta.consulta(id));
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        model.addAttribute("medicos", repositoryMedico.medicos());
        return new ModelAndView("/consulta/form", model);
    }

    @PostMapping("/updateConsulta")
    public ModelAndView updateConsulta(Consulta consulta) {
        repositoryConsulta.updateConsulta(consulta);
        return new ModelAndView("redirect:/consulta/apresentarConsulta");
    }

    @GetMapping("/consultasPorPaciente/{id}")
    public ModelAndView consultasPorPaciente(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", repositoryConsulta.consultasPorPaciente(id));
        return new ModelAndView("/consulta/list", model);
    }
    @GetMapping("/consultasPorMedico/{id}")
    public ModelAndView consultasPorMedico(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("consultas", repositoryConsulta.consultasPorMedico(id));
        return new ModelAndView("/consulta/list", model);
    }

    @GetMapping("/buscarPorData")
    public ModelAndView buscarPorData(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            ModelMap model) {

        List<Consulta> consultas = repositoryConsulta.buscarPorData(data);
        model.addAttribute("consultas", consultas);
        return new ModelAndView("/consulta/list", model);
    }

}
