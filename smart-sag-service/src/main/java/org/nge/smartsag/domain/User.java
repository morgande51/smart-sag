package org.nge.smartsag.domain;

import java.util.HashSet;
import java.util.Set;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "sag_user")
@Data
@EqualsAndHashCode(exclude = {"primaryOrgs","adminOrgs"})
@ToString(exclude = {"primaryOrgs", "adminOrgs"})
@NamedQueries({
	@NamedQuery(name = "User.getWithOrgs", query = "select user from User user join fetch user.primaryOrgs where user.id = ?1"),
	@NamedQuery(name = "User.findByPhone", query = "select user from User user where user.phone = ?1"),
	@NamedQuery(name = "User.findByEmail", query = "select user from User user where user.email like ?1"),
	@NamedQuery(name = "User.findAdminByOrgId", query = "select user from User user join user.adminOrgs org where org.id = :orgId")
})
public class User implements IdentifiableDomain<Long> {

	@Id
	@SequenceGenerator(name = "userSeq", sequenceName = "sag_user_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
	private Long id;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Column(name = "phone", nullable = false, length = 10)
	private String phone;
	
	@OneToMany(mappedBy = "primaryContact", orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonbTransient
	private Set<Organization> primaryOrgs;
	
	@ManyToMany(mappedBy = "admins")
	@JsonbTransient
	private Set<Organization> adminOrgs;
	
	public Organization createOrg(String name) {
		Organization org = Organization.createOrg(name, this);
		if (primaryOrgs == null) {
			primaryOrgs = new HashSet<>();
			System.out.print("yep, creating new instance");
		}
		else {
			checkName(name);
		}
		primaryOrgs.add(org);
		return org;
	}
	
	public void removeOrg(Long id) {
		Organization org = getOrg(id);
		if (!org.canRemove()) {
			// TODO: fix me
			throw new RuntimeException("Oranization: " + id + " cannot be deleted");
		}
		primaryOrgs.remove(org);
		org.getAdmins().clear();
	}
	
	public Organization updateOrg(Long id, String name) {
		Organization org = getOrg(id);
		checkName(name);
		org.setName(name);
		return org;
	}
	
	protected Organization getOrg(Long id) {
		return primaryOrgs.stream()
				.filter(o -> o.getId().equals(id))
				.findAny()
				.orElseThrow(UnknownDomainException::new);
	}
	
	protected void checkName(String name) {
		primaryOrgs.stream()
			.filter(o -> o.getName().equalsIgnoreCase(name))
			.findAny()
			.ifPresent(o -> {
				throw new UniqueNameException(name);
			});
	}
	
	public Organization getAdminOrg(Long id) {
		return adminOrgs.stream()
				.filter(o -> o.getId().equals(id))
				.findAny()
				.orElseThrow(() -> new InvalidAdminException(this));
	}
	
	public Organization getPopUpOrg() {
		return primaryOrgs.stream().filter(Organization::isPopup).findAny().get();
	}
	
	public static final String GET_WITH_ORGS = "#User.getWithOrgs";

	public static final String FIND_BY_PHONE = "#User.findByPhone";

	public static final String FIND_BY_EMAIL = "#User.findByEmail";

	public static final String FIND_ADMIN_BY_ORG_ID = "#User.findAdminByOrgId";
}