package com.example.clinicalCenter.model;

import com.example.clinicalCenter.model.enums.UserType;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private UserType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String getAuthority() {
        return this.type.toString();
    }
}
