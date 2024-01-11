package study.datajpa.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Team;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamJpaRepository {
    private final EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }

    public void delete(Team team){
        em.remove(team);
    }

    public List<Team> findAll(){
        return em.createQuery("SELECT t FROM Team t", Team.class)
                 .getResultList();
    }

    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count(){
        return em.createQuery("SELECT count(t) FROM Team t", Long.class)
                 .getSingleResult();
    }
}
