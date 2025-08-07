package aula2604.model.repository;

import aula2604.model.entity.Estado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class EstadoRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(Estado estado) {
        em.persist(estado);
    }

    public Estado findById(Long id) {
        return em.find(Estado.class, id);
    }

    public List<Estado> findAll() {
        TypedQuery<Estado> query = em.createQuery("SELECT e FROM Estado e ORDER BY e.nome", Estado.class);
        return query.getResultList();
    }

    public void remove(Long id) {
        Estado estado = em.find(Estado.class, id);
        if (estado != null) {
            em.remove(estado);
        }
    }

    public void update(Estado estado) {
        em.merge(estado);
    }
}
