package docSharing.entities.abstracts;

import javax.persistence.*;

@MappedSuperclass
public abstract class INode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Column(name = "name", nullable = false)
    protected String name;
//    @ManyToOne
//    protected INode parent;

    public abstract boolean isDirectory();
}
