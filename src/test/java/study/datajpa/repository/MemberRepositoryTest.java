package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	@Autowired MemberQueryRepository memberQueryRepository;

	/*public MemberRepositoryTest(MemberQueryRepository memberQueryRepository) {
		this.memberQueryRepository = memberQueryRepository;
	}*/

	/*  Same transaction, same EntityManager
	* It is the same EntityManager used in two repositories above. */
	@PersistenceContext EntityManager em;

	@Test
	public void testMember() {

		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		// Null 일수도 있기 때문에 Optional로 가져옴
		Optional<Member> find = memberRepository.findById(savedMember.getId());

		Member findMember = find.get();  // 이렇게 쓰는건 좋지 않은 방법! -> 조건문으로 값이 없을경우 마땅한 처리를 해줘야함

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);

		/*if(find == null) {
			'...'
		} else {
			...
		}*/
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);


		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();

		// single inquiry verification
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// dirty checking test
		// findMember1.changeUserName("member!!!!");

		// list inquiry verification
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// count verification
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// delete verificatiion
		memberRepository.delete(member1);
		memberRepository.delete(member2);

		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void findHelloByTest() {
		List<Member> helloBy = memberRepository.findTop3HelloBy();
	}

	@Test
	public void namedQueryTest() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsername("AAA");
		Member member = result.get(0);

		assertThat(member.getUsername()).isEqualTo("AAA");
	}

	@Test
	public void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findUser("AAA", 10);
		Member member = result.get(0);

		assertThat(member).isEqualTo(m1);
	}

	@Test
	public void findUsernaeListTest() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<String> result = memberRepository.findUsernameList();

		for (String s : result) {
			System.out.println("s = " + s); /* 실무에서는 다 assert로 테스트 */
		}
	}

	@Test
	public void findMemberDtoTest() {
		Team teamA = new Team("teamA");
		teamRepository.save(teamA);

		Member m1 = new Member("AAA", 10);
		m1.changeTeam(teamA);
		memberRepository.save(m1);

		List<MemberDto> memberDto = memberRepository.findMemberDto();

		for (MemberDto dto : memberDto) {
			System.out.println("dto = " + dto);
		}
	}


	// collection parameter binding
	@Test
	public void findByNamesTest() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

		for (Member member : result) {
			System.out.println("member = " + member);
		}
	}

	// return type
	@Test
	public void returnTypeTest() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		/*  return null if there's no data ( spring data jpa)
		* in case pure jpa, no result exception occurs
		* The null checking conditional is a bad code.
		* */
		Member findMember = memberRepository.findMemberByUsername("AAA");
		//if(findMember != null) { } // bad code!!!

		/*  USE OPTIONAL to find a single row.
		* return Optional.empty if there's no data -> how to deal with it?
		* Exception occurs if there are more than one row. */
		Optional<Member> option = memberRepository.findOptionByUsername("AAA");

		/* return empty collection if there's no data */
		List<Member> aaa = memberRepository.findListByUsername("AAA");

		System.out.println("aaa = " + aaa);
		System.out.println("option = " + option);
		System.out.println("findMember = " + findMember);
	}

	// paging and sorting_spring data jpa
	@Test
	public void paging() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		memberRepository.save(new Member("member6", 10));

		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

		// when
		// Total count is included in Page class
		Page<Member> page = memberRepository.findByAge(age, pageRequest);

		// page calculating formula is not needed

		// invert into dto
		page.map(member-> new MemberDto(member.getId(), member.getUsername(), null));

		// then
		List<Member> content = page.getContent();

		assertThat(content.size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(6);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();
	}

	// slice
	@Test
	public void slicing() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		memberRepository.save(new Member("member6", 10));

		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

		// when
		// Total count is included in Page class
		Slice<Member> page = memberRepository.findAsSliceByAge(age, pageRequest);

		// page calculating formula is not needed

		// invert into dto
		page.map(member-> new MemberDto(member.getId(), member.getUsername(), null));

		// then
		List<Member> content = page.getContent();

		assertThat(content.size()).isEqualTo(3);
		// assertThat(page.getTotalElements()).isEqualTo(6);
		assertThat(page.getNumber()).isEqualTo(0);
		// assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();
	}

	// count query separation
	@Test
	public void countQuerySeparation() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		memberRepository.save(new Member("member6", 10));

		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

		// when
		// Total count is included in Page class
		Page<Member> page = memberRepository.findCountSeparationByAge(age, pageRequest);

		// invert into dto
		page.map(member-> new MemberDto(member.getId(), member.getUsername(), null));

		// page calculating formula is not needed

		// then
		List<Member> content = page.getContent();

		assertThat(content.size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(6);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();
	}

	// bulk operation
	@Test
	public void bulkUpdate() {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));

		// when
		int resultCount = memberRepository.bulkAgePlus(20);
		/*em.flush();
		em.clear();*/

		List<Member> result = memberRepository.findByUsername("member5");
		Member member5 = result.get(0);
		System.out.println("member5 = " + member5);

		// then
		assertThat(resultCount).isEqualTo(3);
	}

	// @EntityGraph
	// before and after overriding
	@Test
	public void findMemberLazy() {
		// given
		// member1 -> teamA
		// member2 -> teamB
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

		// when
		//List<Member> members = memberRepository.findAll();
		//List<Member> members = memberRepository.findMemberEntityGraph();
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");

		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());    // proxy
			System.out.println("member.teamName = " + member.getTeam().getName());
			System.out.println("member.teamClass = " + member.getTeam().getClass());    // proxy
		}
	}

	@Test
	public void findMemberFetch() {
		// given
		// member1 -> teamA
		// member2 -> teamB
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

		// when
		List<Member> members = memberRepository.findMemberFetchJoin();

		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());    // class
			System.out.println("member.teamName = " + member.getTeam().getName());
			System.out.println("member.teamClass = " + member.getTeam().getClass());    // class
		}
	}

	// JPA hint
	@Test
	public void queryHint() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush(); // synchronize database with persistent context
		em.clear(); // empty persistent context

		// when
		// dirty checking
		/*Member findMember = memberRepository.findById(member1.getId()).get();
		findMember.changeUserName("member2");
		em.flush();*/

		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.changeUserName("member2");
	}

	// JPA hint
	@Test
	public void lock() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush(); // synchronize database with persistent context
		em.clear(); // empty persistent context

		// when
		List<Member> findMember = memberRepository.findLockByUsername("member1");
	}

	// Implementing a custom repository
	@Test
	public void callCuston() {
		List<Member> result = memberRepository.findMemberCustom();
	}

	//
	@Test
	public void test() {
		List<Member> result = memberQueryRepository.findAllMembers();
		for (Member member : result) {
			System.out.println("member = " + member);
		}
	}

}