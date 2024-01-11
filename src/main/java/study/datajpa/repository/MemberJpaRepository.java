package study.datajpa.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll(){
        return em.createQuery("SELECT m FROM Member m", Member.class)
                 .getResultList();
    }

    public long count(){
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class)
                 .getSingleResult();
    }

    public void delete(Member member){
        em.remove(member);
    }
}
