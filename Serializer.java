import org.jdom2.Document;
import org.jdom2.Element;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.*;
import java.lang.reflect.*;


public class Serializer
{
    public IdentityHashMap table;
    private Document doc;
    
    public Serializer()
    {
        table = new IdentityHashMap();
        doc = new Document(new Element("serialized"));
    }
    
    public org.jdom2.Document serialize(Object obj) throws Exception
    {
        serializeObject(obj);
        return doc;
    }
    
    // Serialize each object and return parent element
    public void serializeObject(Object obj) throws Exception
    {
        Class classObj = obj.getClass();
        String id = Integer.toString(obj.hashCode());
        table.put(obj, id);
        
        // Add the object into the child of the root
        Element objectElement = new Element("object");
        objectElement.setAttribute("class", classObj.getName());
        objectElement.setAttribute("id", id);
        
        // Check if array then add length attribute
        if(obj.getClass().isArray())
        {
            objectElement.setAttribute("length", Integer.toString(Array.getLength(obj)));
            serializeArray(obj, objectElement);
        }
        

        
        doc.getRootElement().addContent(objectElement);
        if(!(obj instanceof Collection<?>))
            serializeFields(obj, objectElement);
        else
            serializeCollection(obj, objectElement);
    }
    
    public void serializeFields(Object obj, Element parentElement) throws Exception
    {
        Field[] objFields = obj.getClass().getDeclaredFields();
        if(objFields.length > 0)
        {
            for(int i = 0; i < objFields.length; i++)
            {
                objFields[i].setAccessible(true);
                //Add field to object child
                Element fieldElement = new Element("field");
                fieldElement.setAttribute("name", objFields[i].getName());
                fieldElement.setAttribute("declaringclass", objFields[i].getDeclaringClass().getName());
                parentElement.addContent(fieldElement);
                
                serializeValue(objFields[i], obj, fieldElement);
            }
        }
    }
    
    // Check field type to see if its collection, primitive, array or object reference
    public void serializeValue(Field field, Object obj, Element parentElement) throws Exception
    {
        if(field.getType().equals(Collection.class))
        {
            Element refElement = new Element("reference");
            Object objValue = field.get(obj);
            serializeObject(objValue);
            refElement.addContent(table.get(objValue).toString());
            parentElement.addContent(refElement);
        }
        else if(field.getType().isPrimitive())
        {
            Element valueElement = new Element("value");
            Object value = field.get(obj);
            serializePrimitive(value, parentElement);
        }
        else if(field.getType().isArray())
        {
            // Go through array
            Element refElement = new Element("reference");
            Object objValue = field.get(obj);
            serializeObject(objValue);
            refElement.addContent(table.get(objValue).toString());
            parentElement.addContent(refElement);
        }
        else
        {
            // Object reference field
            Element refElement = new Element("reference");
            Object objValue = field.get(obj);
            System.out.println(obj.getClass());
            serializeObject(objValue);
            refElement.addContent(table.get(objValue).toString());
            parentElement.addContent(refElement);
            
        }
    }
    
    // Serialize primitive values
    public void serializePrimitive(Object obj, Element parentElement)
    {
        Element valueElement = new Element("value");
        valueElement.addContent(obj.toString());
        parentElement.addContent(valueElement);
    }
    
    // Serialize each element in the array
    public void serializeArray(Object obj, Element parentElement) throws Exception
    {
        if(!obj.getClass().getComponentType().isPrimitive())
        {
            for(int i = 0; i < Array.getLength(obj); i++)
            {
                Element refElement = new Element("reference");
                Object objValue = Array.get(obj, i);
                serializeObject(objValue);
                refElement.addContent(table.get(objValue).toString());
                parentElement.addContent(refElement);
            }
        }
        else
        {
            for(int i = 0; i < Array.getLength(obj); i++)
            {
                Element value = new Element("value");
                Object objValue = Array.get(obj, i);
                value.addContent(objValue.toString());
                parentElement.addContent(value);
            }
        }
    }
    
    
    // Serialize Collection class of objects
    public void serializeCollection(Object obj, Element parentElement) throws Exception
    {
        Iterator collectionObjects = ((Collection) obj).iterator();
        while(collectionObjects.hasNext())
        {
            Element refElement = new Element("reference");
            Object objValue = collectionObjects.next();
            serializeObject(objValue);
            System.out.println(objValue);
            refElement.addContent(table.get(objValue).toString());
            parentElement.addContent(refElement);
        }
    }

}