package clast.census.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@NotNull
	private String username;

	@NotNull
	private Password password;

	private String name;

	@Column(name = "last_name")
	private String lastname;

	private String email;

	private String phone;

	public User() {
		password = new Password();
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = new Password(password);
	}
	
	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password.getValue();
	}

	public void setPassword(String encryptedPassword) {
		this.password.setValue(encryptedPassword);
	}
	
	public void encryptPassword(String plainPassword) {
		this.password.encryptPassword(plainPassword);
	}
	
	public boolean checkPassword(String plainPassword) {
		return password.checkPassword(plainPassword);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
