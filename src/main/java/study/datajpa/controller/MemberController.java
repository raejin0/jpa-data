package study.datajpa.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    // Web extension - domain class converter
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // Web extension - paging and sorting
    /* http://localhost:8080/members?page=0
    *  http://localhost:8080/members?page=1&size=3
    *  http://localhost:8080/members?page=1&size=3&sort=id,desc
    *  http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username */
    /*@GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5) Pageable pageable) {
    //public Page<Member> list(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }*/

    // convert into DTO
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);                    // method reference
                //.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
                //.map(member -> new MemberDto(member)); // DTO can see entity. However an entity must not see dto.
    }

    @PostConstruct
    public void init() {
        // memberRepository.save(new Member("userA")); // domain class converter

        // paging and sorting
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }
    }
}
