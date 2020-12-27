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
package net.smart.moving.config;

import net.smart.properties.Property;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public abstract class Properties extends net.smart.properties.Properties
{
    public final static String Enabled = "enabled";
    public final static String Disabled = "disabled";
    private final static String[] _defaultKeys = new String[1];
    private int toggler = -2;
    private String[] keys = _defaultKeys;
    public boolean enabled;

    protected void load(net.smart.properties.Properties... propertiesList)
    {
        List<Property<?>> propertiesToLoad = this.getProperties();
        if (this.toggler != -2) {
            for (Property<?> property : propertiesToLoad) {
                property.reset();
            }
        }
        while (propertiesToLoad.size() > 0) {
            propertiesToLoad.removeIf(property -> property.load(propertiesList));
        }
        this.toggler = 0;
        this.update();
    }

    protected void save(File file, String version, boolean header, boolean comments) throws Exception
    {
        List<Property<?>> propertiesToSave = this.getProperties();

        FileOutputStream stream = new FileOutputStream(file);
        PrintWriter printer = new PrintWriter(stream);

        if (header) {
            this.printHeader(printer);
        }

        if (version != null) {
            this.printVersion(printer, version, comments);
        }

        for (int i = 0; i < propertiesToSave.size(); i++) {
            if (propertiesToSave.get(i).print(printer, this.keys, version, comments) && i < propertiesToSave.size() - 1) {
                printer.println();
            }
        }

        printer.close();
    }

    protected abstract void printVersion(PrintWriter printer, String version, boolean comments);

    protected abstract void printHeader(PrintWriter printer);

    public void toggle()
    {
        int length = this.keys == null ? 0 : this.keys.length;
        this.toggler++;
        if (this.toggler == length) {
            this.toggler = -1;
        }
        this.update();
    }

    public void setKeys(String[] keys)
    {
        if (keys == null || keys.length == 0) {
            keys = _defaultKeys;
        }
        this.keys = keys;
        this.toggler = 0;
        this.update();
    }

    public String getKey(int index)
    {
        if (this.keys[index] == null) {
            return Enabled;
        }
        return this.keys[index];
    }

    public String getNextKey(String key)
    {
        if (key == null || key.equals("disabled")) {
            return this.getKey(0);
        }
        int index;
        for (index = 0; index < this.keys.length; index++) {
            if (key.equals(this.keys[index])) {
                break;
            }
        }
        index++;
        if (index < this.keys.length) {
            return this.keys[index];
        }
        return Disabled;
    }

    public void setCurrentKey(String key)
    {
        if (key == null || key.equals(Disabled)) {
            this.toggler = -1;
        } else if (this.keys.length == 1 && this.keys[0] == null && key.equals(Enabled)) {
            this.toggler = 0;
        } else {
            for (this.toggler = 0; this.toggler < this.keys.length; this.toggler++) {
                if (key.equals(this.keys[this.toggler])) {
                    break;
                }
            }

            if (this.toggler == this.keys.length) {
                this.toggler = -1;
            }
        }
        this.update();
    }

    public String getCurrentKey()
    {
        if (this.toggler == -1) {
            return Disabled;
        }
        return this.keys[this.toggler];
    }

    public boolean hasKey(String key)
    {
        if (Enabled.equals(key)) {
            return this.keys[0] == null;
        }
        if (Disabled.equals(key)) {
            return true;
        }

        for (String s : this.keys) {
            if (key == null && s == null || key != null && key.equals(s)) {
                return true;
            }
        }
        return false;
    }

    protected void update()
    {
        List<Property<?>> properties = this.getProperties();
        Iterator<Property<?>> iterator = properties.iterator();

        String currentKey = this.getCurrentKey();
        while (iterator.hasNext()) {
            iterator.next().update(currentKey);
        }
        this.enabled = this.toggler != -1;
    }
}