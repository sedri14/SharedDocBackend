package docSharing.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@SuperBuilder
@Inheritance(strategy= InheritanceType.JOINED)
@NoArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
}