import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

public class Deserializer {
	private HashMap map;
	
	public Deserializer(){
		map = new HashMap();
	}
	
	public void deserializeObject(Document doc) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object obj = null;
		List<Element> list = doc.getRootElement().getChildren();
		Object objInstance;
		int id;
		
		for (Element e : list){
			String className = e.getAttributeValue("class");
			System.out.println(className);
			Class classObj = Class.forName(className);
			
			if (!classObj.isArray()){
				Constructor constructor = classObj.getDeclaredConstructor(new Class[]{});
				constructor.setAccessible(true);
				objInstance = constructor.newInstance();
			}
			else{
				objInstance = Array.newInstance(classObj.getComponentType(), Integer.parseInt(e.getAttributeValue("length")));
			}
			
			map.put(e.getAttributeValue("id"), objInstance);
		}
	}
    public void deserializeFields(List<Element> objectList) throws Exception
    {
        for(int i = 0; i < objectList.size();i++)
        {
            Object instance = map.get(objectList.get(i).getAttributeValue("id"));
            List<Element> fieldList = objectList.get(i).getChildren();
             
            if(instance.getClass().isArray())
            {
                Class componentType = instance.getClass().getComponentType();
                for(int j = 0; j < fieldList.size(); j++)
                {
                    Array.set(instance, j, componentType);
                }
                 
            }
            else if(instance instanceof Collection<?>)
            {
                HashSet hashObject = (HashSet) instance;
                System.out.println(instance.getClass());
                for(int j = 0; j < fieldList.size(); j++)
                {
                    hashObject.add(map.get(fieldList.get(j).getText()));
                    System.out.println(fieldList.get(j).getText());
                }
                Iterator collectionObjects = ((Collection) hashObject).iterator();
                while(collectionObjects.hasNext())
                {
                    System.out.println(collectionObjects.next());
                }
            }
            else
            {
                for(int j = 0; j < fieldList.size(); j++)
                {
                    String declaringClassName = fieldList.get(j).getAttributeValue("declaringclass");
                    Class declaredClass = Class.forName(declaringClassName);
                 
                    String fieldName = fieldList.get(j).getAttributeValue("name");
                    Field field = declaredClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                 
                    //Set fields
                    deserializeValue(field, instance, fieldList.get(j).getChildren().get(0));
                 
                }
            }
        }
    }
    
    public void deserializeValue(Field field, Object instance, Element valueElement) throws Exception
    {
        if(valueElement.getName().equals("reference"))
        {
            field.set(instance, map.get(valueElement.getText()));
        }
        else
        {
            // Cast each field type to a specific class
            Class fieldType = field.getType();
            field.set(instance, valueElement.getText());
        }
    }
}
