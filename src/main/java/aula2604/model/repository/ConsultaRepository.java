package aula2604.model.repository;

import aula2604.model.entity.Agenda;
import aula2604.model.entity.Consulta;
import aula2604.model.entity.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
public class ConsultaRepository {
    @PersistenceContext
    private EntityManager em;

    public void saveConsulta(Consulta consulta){
        em.persist(consulta);
    }
    public Consulta consulta(Long id){
        return em.find(Consulta.class, id);
    }


    public List<Consulta> consultas() {
        List<Consulta> lista = em.createQuery("from Consulta", Consulta.class).getResultList();

        LocalDateTime now = LocalDateTime.now();
        for (Consulta c : lista) {
            if (c.getData().isBefore(now)) {
                c.setStatus("Realizada");
            } else {
                c.setStatus("Agendada");
            }
        }
        return lista; 
    }

    public void removeConsulta(Long id){
        Consulta c = em.find(Consulta.class, id);
        em.remove(c);
    }

    public void updateConsulta(Consulta consulta){
        em.merge(consulta);
    }

    public List<Consulta> consultasPorPaciente(Long pacienteId) {
        Query query = em.createQuery("SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId");
        query.setParameter("pacienteId", pacienteId);
        return query.getResultList();
    }

    public List<Consulta> consultasPorMedico(Long medicoId) {
        Query query = em.createQuery("SELECT c FROM Consulta c WHERE c.medico.id = :medicoId");
        query.setParameter("medicoId", medicoId);
        return query.getResultList();
    }

    public List<Consulta> buscarPorData(LocalDate data) {
        LocalDateTime startOfDay = data.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = data.atTime(23, 59, 59); // 23:59:59

        String jpql = "SELECT c FROM Consulta c WHERE c.data BETWEEN :start AND :end";
        TypedQuery<Consulta> query = em.createQuery(jpql, Consulta.class);
        query.setParameter("start", startOfDay);
        query.setParameter("end", endOfDay);
        return query.getResultList();
    }

    public List<Consulta> buscarPorAgenda(Agenda agenda) {
        TypedQuery<Consulta> query = em.createQuery("SELECT c FROM Consulta c WHERE c.agenda = :agenda", Consulta.class);
        query.setParameter("agenda", agenda);
        return query.getResultList();
    }

    public List<Consulta> findByStatus(String status) {
        TypedQuery<Consulta> query = em.createQuery("SELECT c FROM Consulta c WHERE c.status = :status", Consulta.class);
        query.setParameter("status", status);
        return query.getResultList();
    }


}
