package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	List<Member> findTop3HelloBy();

	// @Query(name = "Member.findByUsername")
	// Spring data JPA find named query by "domain class.method name"
	List<Member> findByUsername(@Param("username") String username);

	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username
						, @Param("age")      int    age);

	// get simple value
	@Query("select m.username from Member m")
	List<String> findUsernameList();

	// get in DTO
	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();

	// collection parameter binding
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	// return type
	Member findMemberByUsername(String username);
	Optional<Member> findOptionByUsername(String username);
	List<Member> findListByUsername(String username);

	// paging and sorting_spring data jpa
	Page<Member> findByAge(int age, Pageable pageable);

	// slice
	Slice<Member> findAsSliceByAge(int age, Pageable pageable);

	// count query separation
	@Query(value = "select m from Member m left join m.team t"
			, countQuery = "select count(m.username) from Member m")
	Page<Member> findCountSeparationByAge(int age, Pageable pageable);

	// bulk operation
	@Modifying(clearAutomatically = true) // --> jpa's executeUpdate() call
	@Query("update Member m " +
			"   set m.age = :age " +
			"   where m.age >= :age")
	int bulkAgePlus(@Param("age")int age);

	// @EntityGraph
	@Query("select m from Member m left join fetch m.team")
	List<Member> findMemberFetchJoin();

	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	@EntityGraph(attributePaths = {"team"})
	@Query("select m from Member m")
	List<Member> findMemberEntityGraph();

	//@EntityGraph("Member.all") // @NamedEntityGraph -> Impractical
	@EntityGraph(attributePaths = {"team"})
	List<Member> findEntityGraphByUsername(@Param("username")String username);

	// jpa hint
	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);

	// jpa lock
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(@Param("username") String username);

}
