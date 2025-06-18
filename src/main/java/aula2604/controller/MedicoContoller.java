package aula2604.controller;

import aula2604.model.entity.Medico;
import aula2604.model.repository.MedicoRepository;
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
@RequestMapping("medico")
public class MedicoContoller {

    @Autowired
    MedicoRepository repositoryMedico;

    @GetMapping("/form")
    public String formMedico(Medico medico){
        return "/medico/form";
    }

    @GetMapping("/apresentarMedico")
    public ModelAndView listarMedico(ModelMap model) {
        model.addAttribute("medicos", repositoryMedico.medicos());
        return new ModelAndView("/medico/list", model);
    }

    @PostMapping("/saveMedico")
    public Object saveMedico(@Valid Medico medico, BindingResult result){
        if(result.hasErrors())
            return "/medico/form";
        repositoryMedico.saveMedico(medico);
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
