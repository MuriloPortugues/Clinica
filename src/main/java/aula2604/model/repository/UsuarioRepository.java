package aula2604.model.repository;

import aula2604.model.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioRepository {

    @PersistenceContext
    private EntityManager em;

    public Usuario usuario(Long id){
        return em.find(Usuario.class, id);
    }

    public Usuario usuario(String login) {
        String jpql = "SELECT u FROM Usuario u WHERE u.login = :login";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
        query.setParameter("login", login);

        List<Usuario> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public void saveUsuario(Usuario usuario){
        em.persist(usuario);
    }

    public List<Usuario> usuarios() {
        String jpql = "SELECT u FROM Usuario u";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
        return query.getResultList();
    }

    public void removeUsuario(Long id){
        Usuario u = em.find(Usuario.class, id);
        if (u != null) {
            em.remove(u);
        }
    }

    public void updateUsuario(Usuario usuario){
        em.merge(usuario);
    }

    public List<Usuario> buscarPorNome(String nome) {
        String jpql = "SELECT u FROM Usuario u WHERE LOWER(u.login) LIKE LOWER(CONCAT('%', :nome, '%'))";
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
        query.setParameter("nome", nome);
        return query.getResultList();
    }

}