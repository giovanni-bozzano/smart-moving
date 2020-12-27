// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.properties;

import java.io.PrintWriter;
import java.util.*;

public class Property<T>
{
    private static final int printWidth = 69;
    private String comment;
    private String[] header;
    private int gap;
    private boolean explicitlyModified;
    private boolean implicitlyModified;
    private String acquiredString;
    private boolean singular;
    private final int type;
    private String currentVersion;
    private Map<String, Object> versionSources;
    private Map<String, Object> versionDefaults;
    private static int i = 0;
    private static final int Is = i++;
    private static final int And = i++;
    private static final int Or = i++;
    private static final int Not = i++;
    private static final int Plus = i++;
    private static final int EitherOr = i++;
    private static final int Maximum = i++;
    private static final int Minimum = i++;
    private static final int ToKeyName = i++;
    private static final int ToKeyCode = i++;
    private T value;
    private Value<T> systemValue;
    private Value<T> aquiredValue;
    private Object minValue;
    private Object maxValue;
    private Object local;
    private Object left;
    private int operator;
    private Object right;
    private List<Property<Boolean>> depends = null;

    public Property(int type)
    {
        this.type = type;
    }

    private Property(String key)
    {
        this(Properties.Key);
        this.set(new Value(key));
    }

    private Property(Object left, int operator, Object right)
    {
        this(Properties.Operator);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    private Property(Object local, int operator, Object left, Object right)
    {
        this(Properties.Operator);
        this.local = local;
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public void update(String key)
    {
        this.value = this.getKeyValue(key);
    }

    public void setValue(T value)
    {
        this.value = value;
        this.systemValue = new Value<>(value);
        this.aquiredValue = new Value<>(value);
        this.implicitlyModified = false;
        this.explicitlyModified = false;
    }

    public Property<T> singular()
    {
        this.singular = true;
        return this;
    }

    public Property<Boolean> is(Object value)
    {
        return new Property<>(this, Is, value);
    }

    public Property<Boolean> and(Object value)
    {
        return new Property<>(this, And, value);
    }

    public Property<Boolean> or(Object value)
    {
        return new Property<>(this, Or, value);
    }

    public Property<Boolean> andNot(Property<?> value)
    {
        return this.and(value.not());
    }

    public Property<Boolean> not()
    {
        return new Property<>(this, Not, null);
    }

    public Property<Float> plus(Object value)
    {
        return new Property<>(this, Plus, value);
    }

    public Property<?> eitherOr(Object either, Object or)
    {
        return new Property<>(this, EitherOr, either, or);
    }

    public Property<Float> maximum(Object value)
    {
        return new Property<>(this, Maximum, value);
    }

    public Property<Float> minimum(Object value)
    {
        return new Property<>(this, Minimum, value);
    }

    public Property<String> toKeyName()
    {
        return new Property<>(this, ToKeyName, null);
    }

    public Property<Integer> toKeyCode(Integer defaultValue)
    {
        return new Property<Integer>(this, ToKeyCode, null).defaults(defaultValue);
    }

    public Property<T> depends(Property<Boolean>... conditions)
    {
        if (this.depends == null) {
            this.depends = new LinkedList<>();
        }

        this.depends.addAll(Arrays.asList(conditions));

        return this;
    }

    public Property<T> values(Object defaultValue, Object minValue, Object maxValue)
    {
        return this.defaults(defaultValue).range(minValue, maxValue);
    }

    public Property<T> up(Object defaultValue, Object minValue)
    {
        return this.defaults(defaultValue).min(minValue);
    }

    public Property<T> down(Object defaultValue, Object maxValue)
    {
        return this.defaults(defaultValue).max(maxValue);
    }

    public Property<T> range(Object minValue, Object maxValue)
    {
        return this.min(minValue).max(maxValue);
    }

    public Property<T> defaults(Object defaultValue, String... versions)
    {
        this.versionDefaults = addVersioned(this.versionDefaults, defaultValue, versions);
        return this;
    }

    public Property<T> min(Object minValue)
    {
        this.minValue = minValue;
        return this;
    }

    public Property<T> max(Object maxValue)
    {
        this.maxValue = maxValue;
        return this;
    }

    public Property<T> key(String key, String... versions)
    {
        if (this.currentVersion == null) {
            if (versions != null && versions.length > 0) {
                this.currentVersion = versions[0];
            } else {
                this.currentVersion = Current;
            }
        }

        return this.source(new Property(key), versions);
    }

    public Property<T> source(Object source, String... versions)
    {
        this.versionSources = addVersioned(this.versionSources, source, versions);
        return this;
    }

    public Property<T> comment(String comment)
    {
        this.comment = comment;
        return this;
    }

    public Property<T> section(String... header)
    {
        this.gap = 1;
        this.header = header;
        return this;
    }

    public Property<T> chapter(String... header)
    {
        this.gap = 2;
        this.header = header;
        return this;
    }

    public Property<T> book(String... header)
    {
        this.gap = 3;
        this.header = header;
        return this;
    }

    public void reset()
    {
        this.explicitlyModified = false;
        this.implicitlyModified = false;

        this.value = null;
        this.systemValue = null;
        this.aquiredValue = null;

        reset(this.minValue);
        reset(this.maxValue);

        reset(this.left);
        reset(this.right);
        if (this.depends != null) {
            for (Property<Boolean> depend : this.depends) {
                reset(depend);
            }
        }
    }

    private static void reset(Object value)
    {
        if (value instanceof Property) {
            ((Property<?>) value).reset();
        }
    }

    public boolean load(Properties... propertiesList)
    {
        if (this.systemValue != null) {
            return true;
        }

        if (this.type == Properties.Constant) {
            return true;
        }

        if (this.type == Properties.Operator) {
            if (this.operator == EitherOr) {
                if (this.getValue(this.left) == null || this.getValue(this.right) == null || this.getValue(this.local) == null) {
                    return false;
                }
            }

            if (this.operator == Is || this.operator == And || this.operator == Or || this.operator == Plus || this.operator == Maximum || this.operator == Minimum) {
                if (this.getValue(this.left) == null || this.getValue(this.right) == null) {
                    return false;
                }
            }

            if (this.operator == Not || this.operator == ToKeyName || this.operator == ToKeyCode) {
                if (this.getValue(this.left) == null) {
                    return false;
                }
            }

            Object operatorValue = null;
            if (this.operator == Is) {
                operatorValue = this.getValue(this.left).is(this.getValue(this.right));
            } else if (this.operator == And) {
                operatorValue = this.getValue(this.left).and(this.getValue(this.right));
            } else if (this.operator == Or) {
                operatorValue = this.getValue(this.left).or(this.getValue(this.right));
            } else if (this.operator == Not) {
                operatorValue = this.getValue(this.left).not();
            } else if (this.operator == Plus) {
                operatorValue = this.getValue(this.left).plus(this.getValue(this.right));
            } else if (this.operator == EitherOr) {
                operatorValue = this.getValue(this.local).eitherOr(this.getValue(this.left), this.getValue(this.right));
            } else if (this.operator == Maximum) {
                operatorValue = this.getValue(this.left).maximum(this.getValue(this.right));
            } else if (this.operator == Minimum) {
                operatorValue = this.getValue(this.left).minimum(this.getValue(this.right));
            } else if (this.operator == ToKeyName) {
                operatorValue = this.getValue(this.left).toKeyName();
            } else if (this.operator == ToKeyCode) {
                operatorValue = this.getValue(this.left).toKeyCode();
            }

            if (operatorValue == null) {
                throw new RuntimeException("Unknown operator '" + this.operator + "' found");
            }

            return this.set(this.getValue(operatorValue));
        }

        if (propertiesList == null || this.versionSources == null) {
            return false;
        }

        if (this.depends != null) {
            for (Property<Boolean> depend : this.depends) {
                if (this.getValue(depend) == null) {
                    return false;
                }
            }
        }

        Object minObject = this.getMinimumValue();
        Value<T> minValue = this.getValue(minObject);
        if (minObject != null && minValue == null) {
            return false;
        }

        Object maxObject = this.getMaximumValue();
        Value<T> maxValue = this.getValue(maxObject);
        if (maxObject != null && maxValue == null) {
            return false;
        }

        Value<T> defaultValue = this.getValue(this.getDefaultValue());
        for (Properties properties : propertiesList) {
            Object source = this.getVersionSource(properties.version);
            if (source != null) {
                String key = getKey(source);
                this.aquiredValue = key != null ? this.getPropertyValue(properties, key) : this.getValue(source);
                if (this.aquiredValue != null) {
                    Value<T> initValue = this.aquiredValue.clone();
                    if (this.depends != null) {
                        for (Property<Boolean> depend : this.depends) {
                            initValue.withDependency(this.getValue(depend), defaultValue);
                        }
                    }

                    if (minObject != null) {
                        initValue.withMinimum(minValue, defaultValue);
                    }

                    if (maxObject != null) {
                        initValue.withMaximum(maxValue, defaultValue);
                    }

                    return this.set(initValue);
                } else if (key == null) {
                    return false;
                }
            }
        }
        return this.set(defaultValue);
    }

    private boolean set(Value<T> initValue)
    {
        this.systemValue = initValue;
        this.update(null);
        return true;
    }

    private static Map<String, Object> addVersioned(Map<String, Object> versioned, Object value, String... versions)
    {
        if (versioned == null) {
            versioned = new Hashtable<>(1);
        }

        if (versions == null || versions.length == 0) {
            versions = CurrentArray;
        }

        for (String version : versions) {
            versioned.put(version, value);
        }
        return versioned;
    }

    public T getKeyValue(String key)
    {
        T value = this.systemValue.get(key);
        if (value == null) {
            value = this.getValue(this.getDefaultValue()).get(null);
        }
        if (value == null) {
            value = (T) Properties.getDefaultValue(this.type);
        }
        return value;
    }

    private Value<T> getValue(Object value)
    {
        if (value instanceof Property) {
            Property<?> property = (Property<?>) value;
            property.load((Properties[]) null);
            return (Value<T>) property.systemValue;
        }
        if (value instanceof Value<?>) {
            return (Value<T>) value;
        }
        return new Value<T>((T) value);
    }

    private Object getDefaultValue(String version)
    {
        Object defaultValue = null;
        if (this.versionDefaults != null) {
            if (version != null) {
                defaultValue = this.versionDefaults.get(version);
            }
            if (defaultValue == null) {
                defaultValue = this.versionDefaults.get(Current);
            }
        }
        if (defaultValue == null) {
            defaultValue = Properties.getDefaultValue(this.type);
        }
        return defaultValue;
    }

    private Object getDefaultValue()
    {
        return this.getDefaultValue(Current);
    }

    private Object getMinimumValue()
    {
        if (this.minValue != null) {
            return this.minValue;
        }
        return Properties.getMinimumValue(this.type);
    }

    private Object getMaximumValue()
    {
        if (this.maxValue != null) {
            return this.maxValue;
        }
        return Properties.getMaximumValue(this.type);
    }

    private Object getVersionSource(String version)
    {
        if (this.versionSources == null) {
            return null;
        }

        Object source = null;
        if (version != null) {
            source = this.versionSources.get(version);
        }
        if (source == null) {
            source = this.versionSources.get(Current);
        }
        return source;
    }

    private Value<T> getPropertyValue(Properties properties, String key)
    {
        String propertyString = properties.getProperty(key);
        if (propertyString != null) {
            this.acquiredString = propertyString;
        }

        String stringToParse = propertyString;
        if (propertyString != null) {
            stringToParse = propertyString.trim();
            this.explicitlyModified = stringToParse.endsWith("!");
            if (this.explicitlyModified) {
                stringToParse = stringToParse.substring(0, stringToParse.length() - 1);
            }
            stringToParse = stringToParse.trim();
        }

        Value<T> value = this.parsePropertyValue(stringToParse);
        this.implicitlyModified = stringToParse != null && (value == null || !value.equals(this.getValue(this.getDefaultValue(properties.version))));
        if (!this.explicitlyModified && !this.implicitlyModified) {
            return this.getValue(this.getDefaultValue());
        }
        return value;
    }

    private Value<T> parsePropertyValue(String stringToParse)
    {
        if (stringToParse != null) {
            return new Value<T>(this.type).load(stringToParse, this.singular);
        }
        return null;
    }

    public boolean print(PrintWriter printer, String[] sorted, String version, boolean comments)
    {
        if (!this.isPersistent() || this.systemValue == null) {
            return false;
        }

        if (this.getVersionSource(version) == null) {
            return false;
        }

        int gap = this.gap + (this.comment == null ? -1 : 1);
        for (int i = 0; i < gap; i++) {
            printer.println();
        }

        if (this.header != null && this.header.length > 0) {
            this.printHeader(printer);
        }

        if (this.comment != null && comments) {
            printer.print("# ");
            printer.print(this.comment);
            printer.println();
        }

        if (this.acquiredString == null) {
            this.printValue(printer, sorted, false);
            return true;
        }

        boolean error = false;

        Iterator<String> unparsed = this.aquiredValue.getUnparsableStrings();
        while (unparsed != null && unparsed.hasNext()) {
            String unparsableString = unparsed.next();
            printErrorPrefix(printer);
            printer.print("Could not interpret string \"");
            printer.print(unparsableString);
            printer.print("\" as ");
            printer.print(Properties.getBaseTypeName(Properties.getBaseType(this.type)));
            printer.print(" value, used ");
            printer.print(!this.acquiredString.isEmpty() && this.aquiredValue.get(null) != null ? "local" : "system");
            printer.print(" default");
            this.printValuePostfix(printer, null);
            printErrorPostfix(printer);
            error = true;
        }

        Object minObject = this.getMinimumValue();
        Value<T> minValue = this.getValue(minObject);

        Object maxObject = this.getMaximumValue();
        Value<T> maxValue = this.getValue(maxObject);

        Iterator<String> keys = Value.GetAllKeys(this.systemValue);
        while (keys.hasNext()) {
            String key = keys.next();

            T parsedSingleValue = this.aquiredValue.getStored(key);
            T aquiredSingleValue = this.aquiredValue.get(key);
            T usedSingleValue = this.systemValue.getStored(key);

            if (parsedSingleValue == null) {
                if (aquiredSingleValue == null) {
                    continue;
                } else if (aquiredSingleValue.equals(usedSingleValue)) {
                    continue;
                } else {
                    // local default value "aquiredSingleValue" invalid for specific key
                }
            } else {
                if (parsedSingleValue.equals(usedSingleValue)) {
                    continue;
                }
                // else
                // local keyed value "parsedSingleValue" invalid
            }

            if (Properties.getBaseType(this.type) == Properties.Boolean && this.depends != null && !this.depends.isEmpty()) {
                String dependKey = null;
                for (Property<Boolean> depend : this.depends) {
                    String currentDependKey = depend.getCurrentKey();
                    if (currentDependKey != null && !depend.getKeyValue(key)) {
                        dependKey = currentDependKey;
                        break;
                    }
                }

                printWarnPrefix(printer);
                this.printValuePrefix(printer, key);
                printer.print("is ignored because ");

                if (dependKey != null) {
                    printer.print("the ");

                    if (key.equals(Value.Null)) {
                        printer.print("default");
                    } else {
                        printer.print("\"" + key + "\"");
                    }

                    printer.print(" value of property \"");
                    printer.print(dependKey);
                    printer.print("\" is \"false\"");
                } else {
                    printer.print("one of the restricting expressions evaluated to \"false\"");
                }

                printWarnPostfix(printer);
            } else {
                printErrorPrefix(printer);
                this.printValuePrefix(printer, key);
                printer.print("was out of range, used ");

                if (minValue != null && usedSingleValue.equals(minValue.get(key))) {
                    printer.print("minimum");
                } else if (maxValue != null && usedSingleValue.equals(maxValue.get(key))) {
                    printer.print("maximum");
                } else {
                    printer.print("in-range");
                }

                this.printValuePostfix(printer, key);
                printErrorPostfix(printer);
            }
            error = true;
        }

        this.printValue(printer, sorted, error);
        return true;
    }

    private void printValue(PrintWriter printer, String[] sorted, boolean error)
    {
        printer.print(this.getCurrentKey());
        printer.print(":");

        if (this.acquiredString != null && (error || this.explicitlyModified)) {
            printer.print(this.acquiredString);
        } else if (this.implicitlyModified && this.systemValue.equals(this.getValue(this.getDefaultValue()))) {
            printer.print(this.acquiredString);
            printer.print("!");
        } else {
            this.systemValue.print(printer, sorted);
        }
    }

    private void printHeader(PrintWriter printer)
    {
        String title = this.header[0];
        String body = this.header.length > 1 ? this.header[1] : null;
        char separator = this.gap == 3 ? '=' : (this.gap == 2 ? '-' : ' ');
        printSeparation(printer, separator, printWidth);
        printer.print("# ");
        printer.println(title);
        if (body != null) {
            printSeparation(printer, '-', title.length());
            int lineWidth = printWidth - 2;
            while (true) {
                printer.print("# ");
                if (body.length() <= lineWidth) {
                    printer.println(body);
                    break;
                }

                int i;
                for (i = lineWidth; i > 0; i--) {
                    if (body.charAt(i) == ' ') {
                        break;
                    }
                }

                printer.println(body.substring(0, i));
                body = body.substring(i + 1);
            }
        }
        printSeparation(printer, separator, printWidth);
        printer.println();
    }

    private static void printSeparation(PrintWriter printer, char seperator, int length)
    {
        printer.print("# ");
        for (int i = 0; i < length; i++) {
            printer.print(seperator);
        }
        printer.println();
    }

    private void printValuePrefix(PrintWriter printer, String key)
    {
        printer.print("Interpreted ");

        if (!key.equals(Value.Null)) {
            printer.print("\"");
            printer.print(key);
            printer.print("\" ");
        }

        printer.print("value \"");
        printer.print(this.aquiredValue.get(key));
        printer.print("\" ");
    }

    private void printValuePostfix(PrintWriter printer, String key)
    {
        printer.print(" value \"");
        printer.print(this.acquiredString == null || this.acquiredString.isEmpty() ? this.getValue(this.getDefaultValue()) : this.getKeyValue(key));
        printer.print("\" instead");
    }

    private static void printErrorPrefix(PrintWriter printer)
    {
        printErrorPrefix(printer, false);
    }

    private static void printWarnPrefix(PrintWriter printer)
    {
        printErrorPrefix(printer, true);
    }

    private static void printErrorPrefix(PrintWriter printer, boolean warning)
    {
        printer.print("#");
        if (!warning) {
            printer.print("!!");
        }
        printer.print("! ");
    }

    private static void printErrorPostfix(PrintWriter printer)
    {
        printErrorPostfix(printer, false);
    }

    private static void printWarnPostfix(PrintWriter printer)
    {
        printErrorPostfix(printer, true);
    }

    private static void printErrorPostfix(PrintWriter printer, boolean warning)
    {
        printer.print(" !");
        if (!warning) {
            printer.print("!!");
        }
        printer.println("#");
    }

    public boolean isPersistent()
    {
        return this.versionSources != null && this.versionSources.size() > 0;
    }

    public String getCurrentKey()
    {
        if (this.isPersistent()) {
            return getKey(this.getVersionSource(this.currentVersion));
        }
        return null;
    }

    private static String getKey(Object candidate)
    {
        if (candidate instanceof Property) {
            Property<?> property = (Property<?>) candidate;
            if (property.type == Properties.Key) {
                return (String) property.value;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        if (this.isPersistent()) {
            return this.getCurrentKey();
        }
        return super.toString();
    }

    public String getKeyValueString(String key)
    {
        return this.getValueString(this.getKeyValue(key));
    }

    public String getValueString()
    {
        return this.getValueString(this.value);
    }

    public String getValueString(T value)
    {
        return value != null ? Value.createDisplayString(value) : null;
    }

    public T getValue()
    {
        return this.value;
    }

    private static final String Current = "";
    private static final String[] CurrentArray = new String[]{Current};
}