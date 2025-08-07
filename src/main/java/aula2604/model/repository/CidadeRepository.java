package aula2604.model.repository;

import aula2604.model.entity.Cidade;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public class CidadeRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(Cidade cidade) {
        em.persist(cidade);
    }

    public Cidade findById(Long id) {
        return em.find(Cidade.class, id);
    }

    public List<Cidade> findAll() {
        TypedQuery<Cidade> query = em.createQuery("SELECT cidade FROM Cidade cidade ORDER BY cidade.nome", Cidade.class);
        return query.getResultList();
    }

    public void remove(Long id) {
        Cidade cidade = em.find(Cidade.class, id);
        if (cidade != null) {
            em.remove(cidade);
        }
    }

    public void update(Cidade cidade) {
        em.merge(cidade);
    }

    public List<Cidade> buscarPorEstadoId(Long estadoId) {
        return em.createQuery("SELECT cidade FROM Cidade cidade WHERE cidade.estado.id = :estadoId", Cidade.class)
                .setParameter("estadoId", estadoId)
                .getResultList();
    }
}
