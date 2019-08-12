package com.devopsbuddy.backend.persistence.domain.backend;

import com.devopsbuddy.enums.RolesEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Role implements Serializable {

    private static final long serialVersionUID = 6177771288063156243L;

    @Id
    private int id;
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    public Role() {
    }

    /**
     * Full constructor
     * @param rolesEnum
     */
    public Role(RolesEnum rolesEnum) {
        this.id = rolesEnum.getId();
        this.name = rolesEnum.getRoleName();
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id == role.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
