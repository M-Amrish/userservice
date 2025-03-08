package dev.amrish.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@Entity()
public class User extends BaseClass {

    private String name;;
    private String email;
    private String hashedPassword;
    @ManyToMany
    private List<Role> role;

}
