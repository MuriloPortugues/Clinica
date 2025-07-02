package aula2604.model.security;


import aula2604.model.entity.Usuario;
import aula2604.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

/**
 *
 * @author fagno
 */
@Transactional
@Repository
public class UsuarioDetailsConfig implements UserDetailsService{

    @Autowired
    UsuarioRepository repositoryUsuario;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repositoryUsuario.usuario(login);
        if(usuario == null){
            throw new UsernameNotFoundException("usuário não encontrado!");
        }
        return new User(usuario.getLogin(), usuario.getPassword(), true, true, true, true, usuario.getAuthorities());
    }

}