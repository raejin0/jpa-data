package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED) // default constructor
@ToString(of = {"id", "username", "age"}) // team을 포함하고, Team class 에서도 연관된 필드까지 toString 할 경우 호출 무한루프
public class Member {

	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String username;
	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	// private일 경우 jap의 구현체인 hibernate가 proxy 기술을 사용할 때 막힐 수 있다.
//	protected Member() {
//	}

	protected Member() {   }

	public Member(String username) {
		this.username = username;
	}

	public Member(String username, int age) {
		this.username = username;
		this.age = age;
	}

	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;

		if(team != null) {
			changeTeam(team);
		}
	}

	public void changeUserName(String username) {
		this.username = username;
	}

	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}
