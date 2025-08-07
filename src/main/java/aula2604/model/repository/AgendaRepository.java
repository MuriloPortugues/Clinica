package aula2604.model.repository;

import aula2604.model.entity.Agenda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
public class AgendaRepository {
    @PersistenceContext
    private EntityManager em;


    public void saveAgenda(Agenda agenda) {
        em.persist(agenda);
    }

    public Agenda agenda(Long id) {
        return em.find(Agenda.class, id);
    }


    public void updateAgenda(Agenda agenda) {
        em.merge(agenda);
    }


    public void removeAgenda(Long id) {
        Agenda a = em.find(Agenda.class, id);
        em.remove(a);
    }

    public List<Agenda> findByStatus(String status) {
        return em.createQuery("SELECT a FROM Agenda a WHERE a.status = :status ORDER BY a.inicio", Agenda.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Agenda> findByIntervaloEMedicoDisponivel(LocalDateTime inicio, LocalDateTime fim, Long medicoId, String status) {
        return em.createQuery(
                        "SELECT a FROM Agenda a WHERE a.status = :status AND a.inicio >= :inicio AND a.fim <= :fim AND a.medico.id = :medicoId",
                        Agenda.class)
                .setParameter("status", status)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("medicoId", medicoId)
                .getResultList();
    }

    public EntityManager getEntityManager() {
        return em;
    }
}
