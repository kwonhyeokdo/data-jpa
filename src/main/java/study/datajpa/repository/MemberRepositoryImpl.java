package study.datajpa.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
    }
}
