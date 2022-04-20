package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

	@PersistenceContext // springboot 컨테이너가 jpa의 영속성 컨텍스트인 EntityManager를 가져다 준다.
	private EntityManager em;

	public Member save(Member member) {
		em.persist(member);
		return member;
	}

	public void delete(Member member) {
		em.remove(member);
	}

	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}

	public Optional<Member> findById(Long id) {
		Member findMember = em.find(Member.class, id);
		return Optional.ofNullable(findMember); // It could be nullable or not. method of Java 8
	}

	public long count() {
		return em.createQuery("select count(m) from Member m", Long.class)
				.getSingleResult();
	}

	public Member find(Long id) {
		return em.find(Member.class, id);
	}
}
