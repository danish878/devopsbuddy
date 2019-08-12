package com.devopsbuddy.backend.persistence.domain.backend;

import com.devopsbuddy.enums.PlanEnum;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Plan implements Serializable {

    private static final long serialVersionUID = 7640062836482955705L;

    @Id
    private int id;
    private String name;

    public Plan() {
    }

    public Plan(PlanEnum planEnum) {
        this.id = planEnum.getId();
        this.name = planEnum.getName();
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
        Plan plan = (Plan) o;
        return id == plan.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
