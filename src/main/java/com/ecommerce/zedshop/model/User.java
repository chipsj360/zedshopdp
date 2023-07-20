package com.ecommerce.zedshop.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;

    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    private boolean enabled;
    private Date date;

    @OneToOne(mappedBy = "user")
    private ShoppingCart shoppingCart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    public void setRoles(List<Role> newRoles) {
        roles.clear();
        roles.addAll(newRoles);
    }

    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = this.roles.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

    private String resetPasswordToken;

}
