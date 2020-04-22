package com.bezkoder.springjwt.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Role extends BaseEntity {
	
	@Size(min = 3, max = 60, message = "BASE_E_11")
	@NotNull(message = "BASE_E_7")
	@Pattern(regexp = "[^0-9]*", message = "BASE_E_12")
	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private ERole name;

	@NotNull(message = "BASE_E_7")
	private String description;

}