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

import net.smart.properties.Properties;

import java.util.Map;

public class ServerConfig extends ClientConfig
{
    private final Properties properties = new Properties();
    private final Properties topProperties = new Properties();

    public void loadFromProperties(Map<String, String> propertyArray, boolean top)
    {
        for (Map.Entry<String, String> entry : propertyArray.entrySet()) {
            this.properties.put(entry.getKey(), entry.getValue());
            if (top) {
                this.topProperties.put(entry.getKey(), entry.getValue());
            }
        }

        this.load(top);
    }

    public void load(boolean top)
    {
        if (!top && !this.topProperties.isEmpty()) {
            for (Object topKey : this.topProperties.keySet()) {
                this.properties.put(topKey, this.topProperties.get(topKey));
            }
        }
        super.loadFromProperties(this.properties);
    }

    public void reset()
    {
        this.properties.clear();
        this.topProperties.clear();
    }
}