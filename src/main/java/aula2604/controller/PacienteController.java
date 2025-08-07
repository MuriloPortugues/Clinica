package aula2604.controller;

import aula2604.model.entity.*;
import aula2604.model.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("paciente")
public class PacienteController {

    @Autowired
    private PacienteRepository repositoryPaciente;

    @Autowired
    private UsuarioRepository repositoryUsuario;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Form único para Paciente, Endereço, Estado, Cidade e Roles
    @GetMapping("/form")
    public String formPaciente(@RequestParam(required = false) Long estadoId,
                               @RequestParam(required = false) Long cidadeId,
                               Paciente paciente,
                               ModelMap model) {

        model.addAttribute("paciente", paciente);

        List<Estado> estados = estadoRepository.findAll();
        model.addAttribute("estados", estados);

        List<Cidade> cidades = new ArrayList<>();
        if (estadoId != null) {
            cidades = cidadeRepository.buscarPorEstadoId(estadoId);
        }
        model.addAttribute("cidades", cidades);

        // Para manter seleção nos selects
        model.addAttribute("estadoId", estadoId);
        model.addAttribute("cidadeId", cidadeId);

        // Listar endereços do paciente (se houver)
        if (paciente != null && paciente.getEnderecos() != null) {
            model.addAttribute("enderecos", paciente.getEnderecos());
        } else {
            model.addAttribute("enderecos", new ArrayList<Endereco>());
        }

        // Listar roles para possível visualização/seleção
        List<Role> roles = roleRepository.roles();
        model.addAttribute("roles", roleRepository.roles());

        return "/paciente/form";
    }

    @GetMapping("/apresentarPaciente")
    public ModelAndView listarPaciente(ModelMap model) {
        model.addAttribute("pacientes", repositoryPaciente.pacientes());
        return new ModelAndView("/paciente/list", model);
    }

    @PostMapping("/savePaciente")
    public Object savePaciente(@Valid Paciente paciente,
                               BindingResult result,
                               @RequestParam String login,
                               @RequestParam String password) {

        if (result.hasErrors()) {
            ModelAndView mv = new ModelAndView("/paciente/form");
            mv.addObject("paciente", paciente);
            return mv;
        }

        // Cria o usuário
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRoles(new ArrayList<>());

        // Busca roles
        Role rolePaciente = roleRepository.findByNome("ROLE_PACIENTE");
        Role roleAdmin = roleRepository.findByNome("ROLE_ADMIN");

        // Verifica se é o primeiro usuário
        long totalUsuarios = repositoryUsuario.count();

        if (totalUsuarios == 0) {
            usuario.getRoles().add(rolePaciente);
            usuario.getRoles().add(roleAdmin);
        } else {
            usuario.getRoles().add(rolePaciente);
        }

        // Salva o usuário
        repositoryUsuario.saveUsuario(usuario);

        // Associa o usuário ao paciente
        paciente.setUsuario(usuario);

        // Associa o paciente a cada endereço
        if (paciente.getEnderecos() != null) {
            for (Endereco endereco : paciente.getEnderecos()) {
                endereco.setPessoa(paciente); // isso garante que o pessoa_id seja populado
            }
        }

        // Salva o paciente (junto com os endereços, se CascadeType.ALL estiver configurado)
        repositoryPaciente.savePaciente(paciente);

        return new ModelAndView("redirect:/paciente/form?cadastrado=true");
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

    // ========== ESTADO ==========

    @PostMapping("/saveEstado")
    public ModelAndView saveEstado(@RequestParam String nome) {
        Estado estado = new Estado();
        estado.setNome(nome);
        estadoRepository.save(estado);
        return new ModelAndView("redirect:/paciente/form?estadoCadastrado=true");
    }

    // ========== CIDADE ==========

    @PostMapping("/saveCidade")
    public ModelAndView saveCidade(@RequestParam String nome, @RequestParam Long estadoId) {
        Cidade cidade = new Cidade();
        cidade.setNome(nome);
        Estado estado = estadoRepository.findById(estadoId);
        cidade.setEstado(estado);
        cidadeRepository.save(cidade);
        return new ModelAndView("redirect:/paciente/form?cidadeCadastrada=true");
    }

    // ========== ENDEREÇO ==========

    @PostMapping("/addEndereco")
    public ModelAndView addEndereco(@RequestParam String logradouro,
                                    @RequestParam Long cidadeId,
                                    @RequestParam Long pacienteId) {
        Paciente paciente = repositoryPaciente.paciente(pacienteId);
        Cidade cidade = cidadeRepository.findById(cidadeId);

        Endereco endereco = new Endereco();
        endereco.setLogradouro(logradouro);
        endereco.setCidade(cidade);

        List<Endereco> enderecos = paciente.getEnderecos();
        if (enderecos == null) {
            enderecos = new ArrayList<>();
            paciente.setEnderecos(enderecos);
        }
        enderecos.add(endereco);

        paciente.getEnderecos().add(endereco);
        repositoryPaciente.updatePaciente(paciente);

        return new ModelAndView("redirect:/paciente/form?enderecoAdicionado=true");
    }

    // ========== ROLE ==========

    @PostMapping("/criarRoles")
    public ModelAndView criarRolesPadrao() {
        roleRepository.save(new Role("ROLE_PACIENTE"));
        roleRepository.save(new Role("ROLE_MEDICO"));
        roleRepository.save(new Role("ROLE_ADMIN"));
        return new ModelAndView("redirect:/paciente/form?rolesCriadas=true");
    }

    // ========== LISTAGENS AUXILIARES PARA O FORMULÁRIO ==========

    @ModelAttribute("estados")
    public List<Estado> listarEstados() {
        return estadoRepository.findAll();
    }

    @GetMapping("/cidades")
    @ResponseBody
    public List<Cidade> listarCidadesPorEstado(@RequestParam("estadoId") Long estadoId) {
        List<Cidade> cidades = cidadeRepository.buscarPorEstadoId(estadoId);
        cidades.forEach(c -> System.out.println("Cidade: " + c.getId() + " - " + c.getNome()));
        return cidades;
    }

    @ModelAttribute("existeRoleAdmin")
    public boolean existeRoleAdmin() {
        return roleRepository.existsRoleAdmin();
    }

}
