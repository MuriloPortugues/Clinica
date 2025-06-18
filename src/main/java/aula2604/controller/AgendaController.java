package aula2604.controller;

import aula2604.model.entity.Agenda;
import aula2604.model.entity.Consulta;
import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import aula2604.model.repository.AgendaRepository;
import aula2604.model.repository.ConsultaRepository;
import aula2604.model.repository.MedicoRepository;
import aula2604.model.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    ConsultaRepository repositoryConsulta;
    @Autowired
    PacienteRepository repositoryPaciente;
    @Autowired
    MedicoRepository repositoryMedico;
    @Autowired
    AgendaRepository repositoryAgenda;

    @GetMapping("/disponibilizar")
    public String disponibilizar(Model model) {
        model.addAttribute("agenda", new Agenda());
        model.addAttribute("medicos", repositoryMedico.medicos());
        return "agenda/disponibilizar";
    }

    @PostMapping("/salvar")
    public String salvarDisponibilidade(@ModelAttribute Agenda agendaForm, @RequestParam Long medicoId) {
        Medico medico = repositoryMedico.medico(medicoId);

        if (agendaForm.getId() != null) {
            // EDITAR agenda existente
            Agenda agendaExistente = repositoryAgenda.agenda(agendaForm.getId());
            agendaExistente.setInicio(agendaForm.getInicio());
            agendaExistente.setFim(agendaForm.getFim());
            agendaExistente.setDuracao(agendaForm.getDuracao());
            agendaExistente.setMedico(medico);
            agendaExistente.setStatus("DISPONIVEL");

            repositoryAgenda.updateAgenda(agendaExistente);

        } else {
            // CRIAR várias agendas com intervalos de 30 minutos
            LocalDateTime inicio = agendaForm.getInicio();
            LocalDateTime fim = agendaForm.getFim();
            int duracaoMinutos = agendaForm.getDuracao();

            while (inicio.plusMinutes(duracaoMinutos).isBefore(fim) || inicio.plusMinutes(duracaoMinutos).equals(fim)) {
                Agenda agenda = new Agenda();
                agenda.setInicio(inicio);
                agenda.setFim(inicio.plusMinutes(duracaoMinutos));
                agenda.setDuracao(duracaoMinutos);
                agenda.setStatus("DISPONIVEL");
                agenda.setMedico(medico);

                repositoryAgenda.saveAgenda(agenda);
                inicio = inicio.plusMinutes(duracaoMinutos);
            }
        }

        return "redirect:/agenda/list";
    }

    @GetMapping("/list")
    public String listarAgendaInicial(Model model) {
        List<Agenda> disponiveis = repositoryAgenda.findByStatus("DISPONIVEL");
        List<Consulta> consultasAgendadas = repositoryConsulta.findByStatus("Agendada");

        List<Agenda> agendasFiltradas = disponiveis.stream()
                .filter(agenda ->
                        consultasAgendadas.stream()
                                .noneMatch(consulta ->
                                        consulta.getData().equals(agenda.getInicio()) &&
                                                consulta.getMedico().getId().equals(agenda.getMedico().getId())
                                )
                )
                .toList();

        model.addAttribute("agendas", agendasFiltradas);
        model.addAttribute("medicos", repositoryMedico.medicos());
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        return "agenda/agendar";
    }



    @PostMapping("/agendar/{id}")
    public String agendar(@PathVariable Long id, @RequestParam Long pacienteId) {
        Agenda agenda = repositoryAgenda.agenda(id);
        Paciente paciente = repositoryPaciente.paciente(pacienteId);

        agenda.setStatus("AGENDADO");

        Consulta consulta = new Consulta();
        consulta.setAgenda(agenda);
        consulta.setPaciente(paciente);
        consulta.setMedico(agenda.getMedico());
        consulta.setData(agenda.getInicio());

        repositoryConsulta.saveConsulta(consulta);
        repositoryAgenda.updateAgenda(agenda);

        return "redirect:/consulta/apresentarConsulta";
    }


    @GetMapping("/filtrar")
    public String filtrarDisponibilidade(
            @RequestParam(value = "inicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,

            @RequestParam(value = "fim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,

            @RequestParam(value = "medicoId", required = false) Long medicoId,

            Model model) {

        List<Agenda> agendas;

        if (inicio == null && fim == null && medicoId == null) {
            agendas = repositoryAgenda.findByStatus("DISPONIVEL");
        } else if (inicio != null && fim != null && medicoId != null) {
            agendas = repositoryAgenda.findByIntervaloEMedicoDisponivel(inicio, fim, medicoId, "DISPONIVEL");
        } else {
            // Monta a query dinamicamente conforme os parâmetros informados
            StringBuilder jpql = new StringBuilder("SELECT a FROM Agenda a WHERE a.status = :status");
            if (inicio != null) jpql.append(" AND a.inicio >= :inicio");
            if (fim != null) jpql.append(" AND a.fim <= :fim");
            if (medicoId != null) jpql.append(" AND a.medico.id = :medicoId");

            var query = repositoryAgenda.getEntityManager().createQuery(jpql.toString(), Agenda.class);
            query.setParameter("status", "DISPONIVEL");
            if (inicio != null) query.setParameter("inicio", inicio);
            if (fim != null) query.setParameter("fim", fim);
            if (medicoId != null) query.setParameter("medicoId", medicoId);

            agendas = query.getResultList();
        }

        model.addAttribute("agendas", agendas);
        model.addAttribute("medicos", repositoryMedico.medicos());
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        model.addAttribute("medicoSelecionadoId", medicoId);

        return "agenda/agendar";
    }

    @PostMapping("/updateAgenda")
    public ModelAndView updateAgenda(Agenda agenda) {
        repositoryAgenda.updateAgenda(agenda);
        return new ModelAndView("redirect:/agenda/list");
    }

    @GetMapping("/editAgenda/{id}")
    public ModelAndView editAgenda(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("agenda", repositoryAgenda.agenda(id));
        model.addAttribute("medicos", repositoryMedico.medicos());
        return new ModelAndView("/agenda/disponibilizar", model);
    }


    @PostMapping("/excluir")
    public String removeAgenda(@RequestParam Long id) {
        Agenda agenda = repositoryAgenda.agenda(id);

        List<Consulta> consultas = repositoryConsulta.buscarPorAgenda(agenda);
        for (Consulta consulta : consultas) {
            repositoryConsulta.removeConsulta(consulta.getId());
        }

        repositoryAgenda.removeAgenda(id);

        return "redirect:/agenda/list";
    }
}