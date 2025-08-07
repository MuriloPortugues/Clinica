package aula2604.model.repository;


import aula2604.model.entity.Paciente;
import aula2604.model.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RoleRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(Role role) {
        if (role.getId() == null) {
            em.persist(role);
        } else {
            em.merge(role);
        }
    }

    public Role findByNome(String nome) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.nome = :nome", Role.class)
                    .setParameter("nome", nome)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Role findById(Long id) {
        return em.find(Role.class, id);
    }

    public void deletarRole(Long id) {
        Role role = findById(id);
        if (role != null) {
            em.remove(role);
        }
    }


    public List<Role> roles() {
        return em.createQuery("SELECT r FROM Role r", Role.class)
                .getResultList();
    }

    public boolean existsRoleAdmin() {
        String jpql = "SELECT COUNT(r) FROM Role r WHERE r.nome = :roleName";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("roleName", "ROLE_ADMIN")
                .getSingleResult();
        return count > 0;
    }

    public boolean isRoleInUse(Long id) {
        String jpql = "SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r.id = :roleId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("roleId", id)
                .getSingleResult();
        return count > 0;
    }
}
