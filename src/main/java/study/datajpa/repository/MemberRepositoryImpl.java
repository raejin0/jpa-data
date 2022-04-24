package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    // Spring automatically inject if only one construct exists.
    // @PersistenceContext
    private final EntityManager em;

    // @RequiredArgsConstructor
    /*public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }*/

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
