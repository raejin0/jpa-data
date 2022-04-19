package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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

	}
}