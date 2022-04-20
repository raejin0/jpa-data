package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepository;

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
		List<Member> helloBy = memberRepository.findHelloBy();
	}
}