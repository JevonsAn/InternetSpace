package hit.internetopo;

//import org.neo4j.driver.AuthTokens;
//import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.neo4j.springframework.data.core.Neo4jClient;
import org.neo4j.driver.Driver;

@SpringBootApplication
public class TopoApplication {

	public static void main(String[] args) {
//		Driver driver = GraphDatabase.driver("neo4j://10.10.11.141:7687", AuthTokens.basic("neo4j", "1q2w3e4r"));
		SpringApplication.run(TopoApplication.class, args);
	}

}
