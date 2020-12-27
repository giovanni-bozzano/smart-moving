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
package net.smart.moving.climbing;

import net.minecraft.block.Block;
import net.smart.moving.Orientation;

public class ClimbGap
{
    public Block Block;
    public int Meta;
    public boolean CanStand;
    public boolean MustCrawl;
    public Orientation Direction;
    public boolean SkipGaps;

    public ClimbGap()
    {
        this.reset();
    }

    public void reset()
    {
        this.Block = null;
        this.Meta = -1;
        this.CanStand = false;
        this.MustCrawl = false;
        this.Direction = null;
        this.SkipGaps = false;
    }
}