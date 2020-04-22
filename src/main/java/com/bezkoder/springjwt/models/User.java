package com.bezkoder.springjwt.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User extends BaseEntity {

	@Size(min = 3, max = 60, message = "BASE_E_11")
	@NotNull(message = "BASE_E_7")
	@Pattern(regexp = "[^0-9]*", message = "BASE_E_12")
	private String username;

	@NotNull(message = "BASE_E_7")
	@Email(message = "BASE_E_12")
	private String email;

	@NotNull(message = "BASE_E_7")
	private String phone;

	@NotNull(message = "BASE_E_7")
	private String password;

	@NotNull(message = "BASE_E_7")
	private Boolean admin;

	@NotNull(message = "BASE_E_7")
	private Boolean active;

	@NotNull(message = "BASE_E_7")
	private Boolean emailValid;

	@NotNull(message = "BASE_E_7")
	private Boolean block;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull(message = "BASE_E_7")
	private Date birthday;
	
	private String deviceId;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull(message = "BASE_E_7")
	private Date lastLoginDate;

	@ManyToMany
	@JoinTable(name = "users_roles", joinColumns = {
			@JoinColumn(name = "iduser", nullable = true, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "idrole", nullable = true, updatable = false) })
	private Set<Role> roles = new HashSet<>();

}
