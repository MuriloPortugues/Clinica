package aula2604.controller;


import aula2604.model.entity.Agenda;
import aula2604.model.entity.Consulta;
import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import aula2604.model.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
        model.addAttribute("consultas", repositoryConsulta.consultas());
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
    public ModelAndView removePaciente(@PathVariable("id") Long id){
        repositoryConsulta.removeConsulta(id);
        return new ModelAndView("redirect:/consulta/apresentarConsulta");
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
