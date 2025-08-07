package aula2604.model.repository;


import aula2604.model.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class HospitalRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void inserirRolePacienteSeNaoExistir() {
        Long count = em.createQuery("SELECT COUNT(r) FROM Role r WHERE r.nome = :nome", Long.class)
                .setParameter("nome", "ROLE_PACIENTE")
                .getSingleResult();

        if (count == 0) {
            em.persist(new Role("ROLE_PACIENTE"));
            System.out.println(">>> ROLE_PACIENTE inserido com sucesso.");
        } else {
            System.out.println(">>> ROLE_PACIENTE já existe.");
        }
    }

}
