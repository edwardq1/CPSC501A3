import java.util.Collection;
import java.util.HashSet;

public class Collections {
	private Collection<Object> carCollections;
	
	public Collections(){
		carCollections = new HashSet<Object>();
		carCollections.add(new Mercedes());
		carCollections.add(new Car());
		carCollections.add(new Car());
		carCollections.add(new Year());
	}
}
