package aula2604.controller;

import aula2604.model.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Transactional
@Controller
@RequestMapping("hospital")
public class HospitalController {

    @Autowired
    HospitalRepository repositoryHospital;

    @GetMapping("/home")
        public ModelAndView apresentarHome(ModelMap model) {
            return new ModelAndView("/hospital/home", model);
        }

    @GetMapping("/login")
    public ModelAndView login(ModelMap model) {
        return new ModelAndView("/hospital/login", model);
    }


}
