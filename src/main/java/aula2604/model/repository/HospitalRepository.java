package aula2604.model.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class HospitalRepository {

    @PersistenceContext
    private EntityManager em;



}
