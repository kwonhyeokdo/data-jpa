package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional(readOnly = true)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember(){
        Member member = new Member("유저1");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void basicCRUD(){
        Member member1 = new Member("유저1");
        Member member2 = new Member("유저2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //  단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();

        result.forEach(System.out::println);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();

        result.forEach(System.out::println);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));

        result.forEach(System.out::println);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> resultList = memberRepository.findListByUsername("AAA");
        Member resultMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> resultOptional = memberRepository.findOptionalByUsername("AAA");

        System.out.println(resultList);
        System.out.println(resultMember);
        System.out.println(resultOptional);
    }
    
    @Test
    @Transactional
    @Rollback(false)
    public void paging(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        List<MemberDto> pageMemberDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)).getContent();
        System.out.println(pageMemberDto);
        
        List<Member> content = page.getContent();

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }
    
    @Test
    @Transactional
    @Rollback(false)
    public void slice(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        
        List<Member> content = page.getContent();

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }
    
    @Test
    @Transactional
    @Rollback(false)
    public void list(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        memberRepository.findListByAge(age, pageRequest);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int age = 20;
        int resultCount = memberRepository.bulkAgePlus(age);

        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findMemberLazy(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();
        // List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    public void queryHint(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void lock(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");
        System.out.println(findMember);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void callCustom(){
        List<Member> findMember = memberRepository.findMemberCustom();
        System.out.println(findMember);
    }
}
