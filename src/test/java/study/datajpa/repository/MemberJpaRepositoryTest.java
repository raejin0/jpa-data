package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest // spring bean을 injection 받아서 쓸 거
@Transactional // 기본적으로 테스트 후 rollback 시키기 때문에 qeury log도 남지 않는다.
@Rollback(false)
class MemberJpaRepositoryTest {

	@Autowired MemberJpaRepository memberJpaRepository;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberJpaRepository.save(member);

		Member findMember = memberJpaRepository.find(savedMember.getId());

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member); // findMember == member;
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);


		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

		// single inquiry verification
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// dirty checking test
		findMember1.changeUserName("member!!!!");

		/*// list inquiry verification
		List<Member> all = memberJpaRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// count verification
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);

		// delete verificatiion
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);

		long deletedCount = memberJpaRepository.count();
		assertThat(deletedCount).isEqualTo(0);*/
	}
}