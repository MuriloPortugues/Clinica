package aula2604.model.repository;

import aula2604.model.entity.Endereco;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class EnderecoRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(Endereco endereco) {
        em.persist(endereco);
    }

    public Endereco findById(Long id) {
        return em.find(Endereco.class, id);
    }

    public List<Endereco> findAll() {
        TypedQuery<Endereco> query = em.createQuery("SELECT e FROM Endereco e ORDER BY e.logradouro", Endereco.class);
        return query.getResultList();
    }

    public void remove(Long id) {
        Endereco endereco = em.find(Endereco.class, id);
        if (endereco != null) {
            em.remove(endereco);
        }
    }

    public void update(Endereco endereco) {
        em.merge(endereco);
    }

    // Opcional: buscar endereços por pessoa (paciente)
    public List<Endereco> findByPessoaId(Long pessoaId) {
        String jpql = "SELECT e FROM Endereco e WHERE e.pessoa.id = :pessoaId ORDER BY e.logradouro";
        TypedQuery<Endereco> query = em.createQuery(jpql, Endereco.class);
        query.setParameter("pessoaId", pessoaId);
        return query.getResultList();
    }
}
