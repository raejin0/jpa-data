package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class  Team {

	@Id @GeneratedValue
	@Column(name = "team_id")
	private Long id;
	private String name;

	@OneToMany(mappedBy = "team") // It is recommended to write mappedBy on where foreign key doesn't exist.
	private List<Member> members = new ArrayList<>();

	protected Team() { }

	public Team(String name) {
		this.name = name;
	}

}
