import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;

public class Serializer {
	public IdentityHashMap map;
	private Document doc;
	
	public Serializer(){
		map = new IdentityHashMap();
		doc = new Document(new Element("serialized"));
				
	}
	
	public org.jdom2.Document serialize(Object obj) throws IllegalArgumentException, IllegalAccessException{
		serializeObject(obj);
		return doc;
		
	}
	
	public void serializeObject(Object obj) throws IllegalArgumentException, IllegalAccessException{
		Class classObj = obj.getClass();
		String id = Integer.toString(obj.hashCode());
		map.put(obj, id);
		
		Element objectElement = new Element("object");
		objectElement.setAttribute("class", classObj.getName());
		objectElement.setAttribute("id", id);
		
		if (obj.getClass().isArray()){
			objectElement.setAttribute("length", Integer.toString(Array.getLength(obj)));
			serializeArray(obj, objectElement);
		}
		
		doc.getRootElement().addContent(objectElement);
		if(!(obj instanceof Collection<?>))
			serializeFields(obj, objectElement);
		else
			serializeCollection(obj,objectElement);
	}

	public void serializeFields(Object obj, Element element) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = obj.getClass().getDeclaredFields();
		if(fields.length > 0){
			for(Field field : fields){
				field.setAccessible(true);
				Element fieldElement = new Element("field");
				fieldElement.setAttribute("name", field.getName());
				fieldElement.setAttribute("declaringclass", field.getDeclaringClass().getName());
				element.addContent(fieldElement);
				
				serializeFieldValue(field, obj, element);
			}
		}
	}
	
	public void serializeFieldValue(Field field, Object obj, Element element) throws IllegalArgumentException, IllegalAccessException{
		if (field.getType().equals(Collection.class)){
			Element referenceElement = new Element("reference");
			Object value = field.get(obj);
			serializeObject(value);
			referenceElement.addContent(map.get(value).toString());
			element.addContent(referenceElement);
		}
		else if(field.getType().isPrimitive()){
			Element valueElement = new Element("value");
			Object value = field.get(obj);
			serializePrimitive(value, element);
		}
		else if(field.getType().isArray()){
			Element referenceElement = new Element("reference");
			Object value = field.get(obj);
			serializeObject(value);
			referenceElement.addContent(map.get(value).toString());
			element.addContent(referenceElement);
		}
		else {
			Element referenceElement = new Element("reference");
			Object value = field.get(obj);
			serializeObject(value);
			referenceElement.addContent(map.get(value).toString());
			element.addContent(referenceElement);
		}
	}
	
	public void serializeArray(Object obj, Element element) throws IllegalArgumentException, IllegalAccessException{
		if (!(obj.getClass().getComponentType().isPrimitive())){
			for (int i = 0; i < Array.getLength(obj); i++){
				Element referenceElement = new Element("reference");
				Object value = Array.get(obj, i);
				serializeObject(value);
				referenceElement.addContent(map.get(value).toString());
				element.addContent(referenceElement);
			}
		}
		else{
			for (int i = 0; i < Array.getLength(obj); i++){
				Element valueElement = new Element("value");
				Object value = Array.get(obj, i);
				valueElement.addContent(value.toString());
				element.addContent(valueElement);
			}
		}
	}
	
	
	public void serializePrimitive(Object obj, Element element){
		Element valueElement = new Element("value");
		valueElement.addContent(obj.toString());
		element.addContent(valueElement);
	}
	
	public void serializeCollection(Object obj, Element element) throws IllegalArgumentException, IllegalAccessException{
		Iterator collectionOfObjects = ((Collection) obj).iterator();
		while (collectionOfObjects.hasNext()){
			Element referenceElement = new Element("reference");
			Object value = collectionOfObjects.next();
			serializeObject(value);
			referenceElement.addContent(map.get(value).toString());
			element.addContent(referenceElement);
					
		}
	}
}
