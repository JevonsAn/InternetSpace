package hit.internetopo.domain;

import lombok.Value;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;

/**
 * @author Ayh
 */
@Node("merge_node_table")
@Value(staticConstructor = "of")  // 不加这个注解，就取消下面的一片注释
public class IPNodeEntity {

    @Id @GeneratedValue
    private Long id;

    private String ip;
    private Long asn;

    @Property("is_dest")
    private String isDest;

//    public IPNodeEntity() {
//    }
//
//    public IPNodeEntity(int asn, String isDest, String ip) {
//        this.asn = asn;
//        this.isDest = isDest;
//        this.ip = ip;
//    }

//    public Long getId() {
//        return id;
//    }
//
//    public int getAsn() {
//        return asn;
//    }
//
//    public String getIsDest() {
//        return isDest;
//    }
//
//    public String getIp() {
//        return ip;
//    }
//
//    public IPNodeEntity withId(Long id) {
//        if (this.id.equals(id)) {
//            return this;
//        } else {
//            IPNodeEntity newObject = new IPNodeEntity(this.asn, this.isDest, this.ip);
//            newObject.id = id;
//            return newObject;
//        }
//    }
}