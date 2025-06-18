package aula2604.controller;
import aula2604.model.entity.Paciente;
import aula2604.model.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Object savePaciente(@Valid Paciente paciente, BindingResult result){
        if(result.hasErrors())
            return "/paciente/form";
        repositoryPaciente.savePaciente(paciente);
        return new ModelAndView("redirect:/paciente/apresentarPaciente");
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
