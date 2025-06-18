package aula2604.model.repository;

import aula2604.model.entity.Paciente;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PacienteRepository {
    @PersistenceContext
    private EntityManager em;

    public void savePaciente(Paciente paciente){
        em.persist(paciente);
    }
    public Paciente paciente(Long id){
        return em.find(Paciente.class, id);
    }
    public List<Paciente> pacientes() {
        Query query = em.createQuery("from Paciente");
        return query.getResultList();
    }
    public void removePaciente(Long id){
        Paciente p = em.find(Paciente.class, id);
        em.remove(p);
    }
    public void updatePaciente(Paciente paciente){
        em.merge(paciente);
    }

    public List<Paciente> buscarPorNome(String nome) {
        String jpql = "SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))";
        Query query = em.createQuery(jpql, Paciente.class);
        query.setParameter("nome", nome);
        return query.getResultList();
    }
}
