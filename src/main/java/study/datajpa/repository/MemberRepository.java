package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("SELECT m FROM Member m WHERE m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("SELECT m.username FROM Member m")
    List<String> findUsernameList();

    @Query(
        """
            SELECT new study.datajpa.dto.MemberDto(m.id, m.username, t.name)
              FROM Member m
              JOIN m.team t
        """
    )
    List<MemberDto> findMemberDto();

    @Query(
        """
            SELECT m
              FROM Member m
             WHERE m.username IN :names
        """
    )
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "SELECT m FROM Member m LEFT JOIN m.team t", countQuery = "SELECT count(m) FROM Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);
    List<Member> findListByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query(
        """
            UPDATE Member m
               SET m.age = m.age + 1
             WHERE m.age >= :age
        """
    )
    int bulkAgePlus(@Param("age") int age);

    @Query(
        """
                   SELECT m
                     FROM Member m
          LEFT JOIN FETCH m.team
        """
    )
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("SELECT m FROM Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(
        value = @QueryHint(name = "org.hibernate.readOnly", value = "true")
    )
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
} 