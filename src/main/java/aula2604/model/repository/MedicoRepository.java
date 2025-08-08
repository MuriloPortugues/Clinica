package aula2604.model.repository;

import aula2604.model.entity.Medico;
import aula2604.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class MedicoRepository {
    @PersistenceContext
    private EntityManager em;

    public Medico saveMedico(Medico medico){
        if (medico.getId() == null) {
            em.persist(medico);
            return medico;
        } else {
            return em.merge(medico);
        }
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

    public Medico findByUsuarioId(Long usuarioId) {
        String jpql = "SELECT m FROM Medico m WHERE m.usuario.id = :usuarioId";
        TypedQuery<Medico> query = em.createQuery(jpql, Medico.class);
        query.setParameter("usuarioId", usuarioId);
        List<Medico> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

}
