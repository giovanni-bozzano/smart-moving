// ==================================================================
// This file is part of Smart Render and Smart Moving.
//
// Smart Render and Smart Moving is free software: you can
// redistribute it and/or modify it under the terms of the GNU General
// Public Licenses published by the Free Software Foundation, either
// version 3 of the License, or (at your option) any later version.
//
// Smart Render and Smart Moving is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Render and Smart Moving. If not, see
// <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect
{
    public static Object NewInstance(Class<?> base, Name name)
    {
        try
        {
            return LoadClass(base, name, true).getConstructor().newInstance();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(name.deobfuscated, exception);
        }
    }

    public static boolean CheckClasses(Class<?> base, Name... names)
    {
        for (Name name : names)
        {
            if (LoadClass(base, name, false) == null)
            {
                return false;
            }
        }

        return true;
    }

    public static Class<?> LoadClass(Class<?> base, Name name, boolean throwException)
    {
        ClassLoader loader = base.getClassLoader();

        if (name.obfuscated != null)
        {
            try
            {
                return loader.loadClass(name.obfuscated);
            }
            catch (ClassNotFoundException ignored)
            {
            }
        }
        try
        {
            return loader.loadClass(name.deobfuscated);
        }
        catch (ClassNotFoundException exception)
        {
            if (throwException)
            {
                throw new RuntimeException(exception);
            }
            return null;
        }
    }

    public static void SetField(Field field, Object object, Object value)
    {
        try
        {
            field.set(object, value);
        }
        catch (IllegalAccessException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static Object GetField(Field field, Object object)
    {
        try
        {
            return field.get(object);
        }
        catch (IllegalAccessException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static void SetField(Class<?> theClass, Object object, Name name, Object value)
    {
        try
        {
            GetField(theClass, name).set(object, value);
        }
        catch (IllegalAccessException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static Object GetField(Class<?> theClass, Object object, Name name)
    {
        try
        {
            return GetField(theClass, name).get(object);
        }
        catch (IllegalAccessException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static Field GetField(Class<?> theClass, Name name)
    {
        return GetField(theClass, name, true);
    }

    public static Field GetField(Class<?> clazz, Name name, boolean throwException)
    {
        if (clazz == null && !throwException)
        {
            return null;
        }

        Field field = null;
        try
        {
            field = GetRawField(clazz, name);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException exception)
        {
            if (throwException)
            {
                throw new RuntimeException(GetFieldMessage(clazz, name), exception);
            }
        }
        return field;
    }

    private static String GetFieldMessage(Class<?> theClass, Name name)
    {
        Field[] fields = theClass.getDeclaredFields();
        StringBuffer message = GetMessage(theClass, name, "field");
        for (Field field : fields)
        {
            AppendField(message, field);
        }
        return message.toString();
    }

    private static Field GetRawField(Class<?> theClass, Name name) throws NoSuchFieldException
    {
        if (name.obfuscated != null)
        {
            try
            {
                return theClass.getDeclaredField(name.obfuscated);
            }
            catch (NoSuchFieldException ignored)
            {
            }
        }

        if (name.forgefuscated != null)
        {
            try
            {
                return theClass.getDeclaredField(name.forgefuscated);
            }
            catch (NoSuchFieldException ignored)
            {
            }
        }

        return theClass.getDeclaredField(name.deobfuscated);
    }

    public static Method GetMethod(Class<?> theClass, Name name, Class<?>... paramArrayOfClass)
    {
        return GetMethod(theClass, name, true, paramArrayOfClass);
    }

    public static Method GetMethod(Class<?> clazz, Name name, boolean throwException, Class<?>... paramArrayOfClass)
    {
        if (clazz == null && !throwException)
        {
            return null;
        }

        Method method = null;
        try
        {
            method = GetRawMethod(clazz, name, paramArrayOfClass);
            method.setAccessible(true);
        }
        catch (NoSuchMethodException exception)
        {
            if (throwException)
            {
                throw new RuntimeException(GetMethodMessage(clazz, name), exception);
            }
        }
        return method;
    }

    private static String GetMethodMessage(Class<?> theClass, Name name)
    {
        Method[] methods = theClass.getDeclaredMethods();
        StringBuffer message = GetMessage(theClass, name, "method");
        for (Method method : methods)
        {
            AppendMethod(message, method);
        }
        return message.toString();
    }

    private static Method GetRawMethod(Class<?> theClass, Name name, Class<?>... paramArrayOfClass) throws NoSuchMethodException
    {
        if (name.obfuscated != null)
        {
            try
            {
                return theClass.getDeclaredMethod(name.obfuscated, paramArrayOfClass);
            }
            catch (NoSuchMethodException ignored)
            {
            }
        }

        if (name.forgefuscated != null)
        {
            try
            {
                return theClass.getDeclaredMethod(name.forgefuscated, paramArrayOfClass);
            }
            catch (NoSuchMethodException ignored)
            {
            }
        }

        return theClass.getDeclaredMethod(name.deobfuscated, paramArrayOfClass);
    }

    public static Object Invoke(Method method, Object paramObject, Object... paramArrayOfObject)
    {
        try
        {
            return method.invoke(paramObject, paramArrayOfObject);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(method.getName(), exception);
        }
    }

    private static StringBuffer GetMessage(Class<?> theClass, Name name, String elementName)
    {
        StringBuffer message = new StringBuffer().append("Can not find ").append(elementName).append(" \"").append(name.deobfuscated).append("\"");

        if (name.obfuscated != null)
        {
            message.append(" (ofuscated \"").append(name.obfuscated).append("\")");
        }

        message.append(" in class \"").append(theClass.getName()).append("\".\nExisting ").append(elementName).append("s are:");

        return message;
    }

    private static void AppendMethod(StringBuffer message, Method method)
    {
        message.append("\n\t\t").append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");

        Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++)
        {
            if (i != 0)
            {
                message.append(", ");
            }
            message.append(types[i].getName());
        }

        message.append(")");
    }

    private static void AppendField(StringBuffer message, Field field)
    {
        message.append("\n\t\t").append(field.getType().getName()).append(" ").append(field.getName());
    }
}