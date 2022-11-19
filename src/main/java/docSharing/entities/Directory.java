package docSharing.entities;

import docSharing.entities.abstracts.INode;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Set;

@Entity
@Table(name="Directory")
public class Directory extends INode {

//    @OneToMany
//    private Set<INode> children;

    @Override
    public boolean isDirectory() {
        return true;
    }
}
