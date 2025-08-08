package aula2604.model.repository;

import aula2604.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class PacienteRepository {

    @PersistenceContext
    private EntityManager em;


    public void savePaciente(Paciente paciente) {
        em.persist(paciente);
    }

    // Buscar paciente por id com endereços e cidades carregados (fetch join)
    public Paciente paciente(Long id) {
        String jpql = "SELECT p FROM Paciente p " +
                "LEFT JOIN FETCH p.enderecos e " +
                "LEFT JOIN FETCH e.cidade c " +
                "WHERE p.id = :id";

        TypedQuery<Paciente> query = em.createQuery(jpql, Paciente.class);
        query.setParameter("id", id);

        List<Paciente> resultados = query.getResultList();

        // Pode não encontrar paciente com endereços, então retorna null se vazio
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public List<Paciente> pacientes() {
        return em.createQuery("SELECT DISTINCT p FROM Paciente p LEFT JOIN FETCH p.enderecos", Paciente.class)
                .getResultList();
    }

    public void removePaciente(Long id) {
        Paciente p = em.find(Paciente.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    public void updatePaciente(Paciente paciente) {
        em.merge(paciente);
    }

    public List<Paciente> buscarPorNome(String nome) {
        String jpql = "SELECT DISTINCT p FROM Paciente p " +
                "LEFT JOIN FETCH p.enderecos e " +
                "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))";

        TypedQuery<Paciente> query = em.createQuery(jpql, Paciente.class);
        query.setParameter("nome", nome);

        return query.getResultList();
    }

    public Paciente pacientePorLogin(String login) {
        String jpql = "SELECT p FROM Paciente p " +
                "LEFT JOIN FETCH p.enderecos e " +
                "LEFT JOIN FETCH e.cidade c " +
                "WHERE p.usuario.login = :login";

        TypedQuery<Paciente> query = em.createQuery(jpql, Paciente.class);
        query.setParameter("login", login);

        List<Paciente> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

}
