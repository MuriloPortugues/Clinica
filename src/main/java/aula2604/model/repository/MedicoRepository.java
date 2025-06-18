package aula2604.model.repository;

import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MedicoRepository {
    @PersistenceContext
    private EntityManager em;

    public void saveMedico(Medico medico){
        em.persist(medico);
    }
    public Medico medico(Long id){
        return em.find(Medico.class, id);
    }
    public List<Medico> medicos() {
        Query query = em.createQuery("from Medico");
        return query.getResultList();
    }
    public void removeMedico(Long id){
        Medico m = em.find(Medico.class, id);
        em.remove(m);
    }
    public void updateMedico(Medico medico){
        em.merge(medico);
    }

    public List<Medico> buscarPorNome(String nome) {
        String jpql = "SELECT m FROM Medico m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))";
        Query query = em.createQuery(jpql, Medico.class);
        query.setParameter("nome", nome);
        return query.getResultList();
    }

}
