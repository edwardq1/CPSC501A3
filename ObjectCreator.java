import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

public class ObjectCreator {
	private Scanner in =  new Scanner(System.in);

	public int objectCreator(){
		System.out.println("Select the following object(s) you want to serialize:");
		System.out.println("	1)Car\n	2)Mercedes\n	3)Year\n	4)Collections\n	5)Dealership");
		int userInput = in.nextInt();
		if (userInput == 1)
			return 1;
		else if(userInput == 2)
			return 2;
		else if(userInput == 3)
			return 3;
		else if(userInput == 4)
			return 4;
		else if(userInput == 5)
			return 5;
		return 6;
	}
	
	public Object initializeObject(Object obj) throws IllegalArgumentException, IllegalAccessException{
		setField(obj);
		return obj;
	}
	
	public void setField(Object obj) throws IllegalArgumentException, IllegalAccessException{
		Class classObject = obj.getClass();
		Field[] field = classObject.getDeclaredFields();
		for (Field f : field){
			f.setAccessible(true);
			if(f.getType().isPrimitive()){
				setPrimitive(obj, f);
			}
			else if (f.getType().isArray()){
				setArrayValues(obj, f);
			}
			else if (f.getType().equals(Collection.class)){
				setCollections(obj, f);
			}
			else{
				System.out.println(f.getName() + "(" + f.get(obj).getClass() + "):");
				initializeObject(f.get(obj));
			}
		}
		
	}
	
	public void setPrimitive(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException{
		System.out.println("Change field " + field.getType()+" "+ field.getName() + " to: ");
		String value = in.next();
		field.set(obj, typeCheck(field.getType(), value));
	}
	
	public void setArrayValues(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException{
		Object array = field.get(obj);
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++){
			if (!array.getClass().getComponentType().isPrimitive()){
				System.out.println("Object reference to " + array.getClass().getComponentType() + " at index " + i);
				initializeObject(Array.get(array, i));
			}
			else{
				System.out.println("Value of " +field.getName() + "["+i+"]" + Array.get(array, i));
				System.out.println("Enter a new value for it: ");
				String user = in.next();
				Array.set(array, i, typeCheck(array.getClass().getComponentType(), user));
			}
		}
	}
	
	public void setCollections(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException{
		Iterator collectionOfObjects = ((Collection) field.get(obj)).iterator();
		while (collectionOfObjects.hasNext()){
			Object value = collectionOfObjects.next();
			initializeObject(value);
		}
	}
	
	
    // Extracted to check primitive types
    public Object typeCheck(Class type, String input)
    {
        if(type.getName() == "int")
            return Integer.parseInt(input);
        else if(type.getName() == "char")
            return input.charAt(0);
        else if(type.getName() == "double")
            return Double.parseDouble(input);
        else if(type.getName() == "float")
            return Float.parseFloat(input);
        else if(type.getName() =="long")
            return Long.parseLong(input);
        else if(type.getName() == "short")
            return Short.parseShort(input);
        else if(type.getName() == "boolean")
        {
            if(input == "true")
                return Boolean.TRUE;
            else if(input == "false")
                return Boolean.FALSE;
            return null;
        }
        else
            return null;
    }
}
