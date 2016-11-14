import java.util.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Vector;
public class Inspector {

	public void inspect(Object obj, boolean recursive){
		List<Field> toDoList = new ArrayList();
		Class classObject = obj.getClass();
		Class superClass = classObject.getSuperclass();

		if (classObject.isArray()){
			System.out.println("THIS IS AN ARRAY OF OBJECTS");
		}
		else{
			inspectClass(classObject);
			inspectInterfaces(classObject);
			inspectMethods(classObject);
			inspectConstructors(classObject);
			inspectClassFields(classObject, obj, recursive, toDoList);
		}
		if (!toDoList.isEmpty() && recursive){
			Field currentField;
			System.out.println("******************Printing field information******************");
			for (int i = 0; i< toDoList.size(); i++){
				currentField = toDoList.get(i);
				System.out.println("******************Field Name: "+ currentField.getName() +"******************");
				try {
					inspect(currentField.get(obj), currentField.get(obj).getClass(), recursive);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			toDoList.clear();
		}
		if ((superClass != null) && (!superClass.isInstance(Object.class))){
			System.out.println("******************Entering superclass******************");
			inspect(obj, classObject.getSuperclass(), recursive);
		}

	}
	//This is the recursive inspect that gets called
	//Takes in 3 parameters
	public void inspect(Object obj, Class c, boolean recursive){
		List<Field> toDoList = new ArrayList();
		Class superClass = c.getSuperclass();
		if (c.isArray()){
			System.out.println("THIS IS AN ARRAY OF OBJECTS");
		}
		else{
			inspectClass(c);
			inspectInterfaces(c);
			inspectMethods(c);
			inspectConstructors(c);
			inspectClassFields(c, obj, recursive, toDoList);
		}
		if (!toDoList.isEmpty() && recursive){
			Field currentField;
			System.out.println("******************Printing field information******************");
			for (int i = 0; i< toDoList.size(); i++){
				currentField = toDoList.get(i);
				System.out.println("******************Field Name: "+ currentField.getName() +"******************");
				try {
					inspect(currentField.get(obj), currentField.get(obj).getClass(), recursive);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			toDoList.clear();
		}
		if ((superClass != null) && (!superClass.isInstance(Object.class))){
			System.out.println("******************Entering superclass******************");
			inspect(obj, c.getSuperclass(), recursive);
		}


	}
	//Inspect a class by getting its declaring class and superclass
	public void inspectClass(Class classObject){
		String className = classObject.getSimpleName();
		// print the declaring class
		System.out.println("Declaring class: " + className);
		Class superClass = classObject.getSuperclass();
		className = superClass.getSimpleName();
		System.out.println("Super class: " + className);

	}
	//Inspect a class by getting its interfaces
	public void inspectInterfaces(Class classObject){
		// obtaining the interfaces
		Class[] interfaces = classObject.getInterfaces();
		System.out.print("Interfaces:");
		if (interfaces.length != 0 ){
			for (int i = 0; i < interfaces.length; i++)
				System.out.print(" " + interfaces[i].getSimpleName() + ",");
		}
		else{
			System.out.print(" No interfaces");
		}
	}
	
	//Method that will handle grabbing methods of a class
	public void inspectMethods(Class classObject){
		System.out.println("");
		Method[] classMethods = classObject.getDeclaredMethods();
		// information for a method
		System.out.println("Class methods:");
		for(Method method : classMethods){
			System.out.println("	" + method.getName() + ": ");
			Class[] methodParameters = method.getParameterTypes();
			System.out.print("		Parameters: ");
			if (methodParameters.length != 0){
				for (Class parameter : methodParameters)
					System.out.print(" " + parameter.getSimpleName());
			}
			else
				System.out.print(" No parameters");
			System.out.println();
			System.out.print("		Exceptions: ");
			Class[] methodException = method.getExceptionTypes();
			if (methodException.length != 0){
				for (Class exception : methodException)
					System.out.print(" " + exception.getName());
			}
			else
				System.out.print(" No exceptions");
			System.out.println();
			System.out.print("		Return type: " + method.getReturnType());
			System.out.println();
			int temp = method.getModifiers();
			System.out.println("		Modifier: " + Modifier.toString(temp));
			System.out.println("");

		}
	}
	
	//Method that will handle grabbing the fields of a class
	public void inspectClassFields(Class classObject, Object object, Boolean recursive, List<Field> l){
		//obtain fields
		Object o = null;
		Field[] classFields = classObject.getDeclaredFields();
		System.out.println("Fields:");
		for(Field field : classFields){
			field.setAccessible(true);
			boolean isObject = field.getType().isPrimitive();
			try {
				o = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			inspectField(field, o, isObject, recursive, object, l);
			// supposed to recurse but it doesnt work
		}
	}
	public void inspectField(Field field, Object o, boolean isObject, boolean recursive, Object object, List<Field> l ){
		if (field.getType().isArray() && o != null){
			System.out.println("	Array field: ");
			System.out.println("		Component type: " + field.getType().getComponentType());
			System.out.println("		Name: " + field.getName());
			System.out.println("		Length: " + Array.getLength(o));
			
			System.out.print("		Contents: ");
			for (int i = 0; i < Array.getLength(o); i++){
				System.out.print(Array.get(o, i) + ", ");
			}
		}
		else if ((!isObject) && recursive == true && o != null){
			try {
				l.add(field);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("******************Entering object******************");
			//inspect(field.getType().getName(), false);
		}
		else if ((!isObject) && recursive == false && o != null)
			try {
				System.out.print("	Reference value of object: " + field.getType().getSimpleName() + System.identityHashCode(field.get(object)));
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else{
			int temp = field.getModifiers();
			System.out.print("	" + Modifier.toString(temp)
					+ " " + field.getType() + " "+ field.getName() + " = ");
			try {
				System.out.print(field.get(object)+ "");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
	}
	
	
	//Method that will handle grabbing the constructors of a class
	public void inspectConstructors(Class classObject){
		// Constructors
		Constructor[] constructors = classObject.getConstructors();
		System.out.println("Constructors:");
		for (Constructor constructor : constructors){
			int temp = constructor.getModifiers();
			System.out.print("	" + constructor.getName() + ":");
			Class[] parameterList = constructor.getParameterTypes();
			System.out.println();
			System.out.print("		Parameters:");
			if (parameterList.length != 0){
				for(Class parameter : parameterList)
					System.out.print(" " + parameter.getName() + ", ");
			}
			else
				System.out.print(" No parameters");
			System.out.println();
			System.out.print("		Modifier: " + Modifier.toString(temp));
			System.out.println();
		}
	}
	
	//Testing methods
	public String testingMethod(Class object){
		Method[] classMethods = object.getMethods();
		for (Method method : classMethods)
			return method.getName();
		return null;
	}
	//Testing methods
	public String testingVariable(Class object){
		Field[] field = object.getDeclaredFields();
		for (Field f : field)
			return f.getName();
	
		return null;
	}
	
	//Testing methods
	public String testSuperClass(Class object){
		return object.getSuperclass().getSimpleName();
	}
}