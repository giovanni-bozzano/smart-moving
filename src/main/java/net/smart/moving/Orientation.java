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
package net.smart.moving;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.smart.moving.asm.interfaces.IBlockWall;
import net.smart.moving.climbing.ClimbGap;
import net.smart.moving.climbing.FeetClimbing;
import net.smart.moving.climbing.HandsClimbing;

import java.util.*;

public class Orientation extends ContextBase
{
    public static final Set<Orientation> Orthogonals = new HashSet<>();
    public static final Set<Orientation> Diagonals = new HashSet<>();
    public static final Set<Orientation> All = new HashSet<>();
    private static final Map<EnumFacing, Orientation> FacingToOrientation = new HashMap<>();
    public static final Orientation ZZ = new Orientation(0, 0);
    public static final Orientation PZ = new Orientation(1, 0, EnumFacing.WEST);
    public static final Orientation ZP = new Orientation(0, 1, EnumFacing.NORTH);
    public static final Orientation NZ = new Orientation(-1, 0, EnumFacing.EAST);
    public static final Orientation ZN = new Orientation(0, -1, EnumFacing.SOUTH);
    public static final Orientation PP = new Orientation(1, 1, EnumFacing.NORTH, EnumFacing.WEST);
    public static final Orientation NN = new Orientation(-1, -1, EnumFacing.SOUTH, EnumFacing.EAST);
    public static final Orientation PN = new Orientation(1, -1, EnumFacing.SOUTH, EnumFacing.WEST);
    public static final Orientation NP = new Orientation(-1, 1, EnumFacing.NORTH, EnumFacing.EAST);
    public static final int DefaultMeta = -1;
    public static final int VineFrontMeta = 0;
    public static final int VineSideMeta = 1;
    private static final int top = 2;
    private static final int middle = 1;
    private static final int base = 0;
    private static final int sub = -1;
    private static final int subSub = -2;
    private static final int NoGrab = 0;
    private static final int HalfGrab = 1;
    private static final int AroundGrab = 2;
    protected int _i, _k;
    private final boolean _isDiagonal;
    private final Set<EnumFacing> _facings;
    final EnumFacing _facing;
    private float _directionAngle;
    private float _mimimumClimbingAngle;
    private float _maximumClimbingAngle;

    private Orientation(int i, int k, EnumFacing... facings)
    {
        this._i = i;
        this._k = k;
        this._isDiagonal = this._i != 0 && this._k != 0;
        this.setClimbingAngles();

        this._facings = new HashSet<>();
        Collections.addAll(this._facings, facings);

        All.add(this);
        this._facing = facings.length > 0 ? facings[0] : null;
        if (facings.length == 1) {
            Orthogonals.add(this);
            FacingToOrientation.put(this._facing, this);
        } else {
            Diagonals.add(this);
        }
    }

    public Orientation rotate(int angle)
    {
        if (this == ZZ) {
            throw new RuntimeException("unrotatable orientation");
        }

        switch (angle) {
            case 0:
                return this;
            case 45:
                if (this == PZ) {
                    return PP;
                }
                if (this == PP) {
                    return ZP;
                }
                if (this == ZP) {
                    return NP;
                }
                if (this == NP) {
                    return NZ;
                }
                if (this == NZ) {
                    return NN;
                }
                if (this == NN) {
                    return ZN;
                }
                if (this == ZN) {
                    return PN;
                }
                if (this == PN) {
                    return PZ;
                }
                throw new RuntimeException("unknown orientation \"" + this + "\"");
            case -45:
                if (this == PZ) {
                    return PN;
                }
                if (this == PN) {
                    return ZN;
                }
                if (this == ZN) {
                    return NN;
                }
                if (this == NN) {
                    return NZ;
                }
                if (this == NZ) {
                    return NP;
                }
                if (this == NP) {
                    return ZP;
                }
                if (this == ZP) {
                    return PP;
                }
                if (this == PP) {
                    return PZ;
                }
                throw new RuntimeException("unknown orientation \"" + this + "\"");
            case 90:
                return this.rotate(45).rotate(45);
            case -90:
                return this.rotate(-45).rotate(-45);
            case 135:
                return this.rotate(180).rotate(-45);
            case -135:
                return this.rotate(-180).rotate(45);
            case 180:
            case -180:
                if (this == PZ) {
                    return NZ;
                }
                if (this == PN) {
                    return NP;
                }
                if (this == ZN) {
                    return ZP;
                }
                if (this == NN) {
                    return PP;
                }
                if (this == NZ) {
                    return PZ;
                }
                if (this == NP) {
                    return PN;
                }
                if (this == ZP) {
                    return ZN;
                }
                if (this == PP) {
                    return NN;
                }
                throw new RuntimeException("unknown orientation");
        }
        throw new RuntimeException("angle \"" + angle + "\" not supported");
    }

    public static Orientation getOrientation(EntityPlayer p, float tolerance, boolean orthogonals, boolean diagonals)
    {
        float rotation = p.rotationYaw % 360F;
        if (rotation < 0) {
            rotation += 360F;
        }

        float minimumRotation = rotation - tolerance;
        if (minimumRotation < 0) {
            minimumRotation += 360F;
        }

        float maximumRotation = rotation + tolerance;
        if (maximumRotation >= 360F) {
            maximumRotation -= 360F;
        }

        if (orthogonals) {
            if (NZ.isWithinAngle(minimumRotation, maximumRotation)) {
                return NZ;
            }
            if (PZ.isWithinAngle(minimumRotation, maximumRotation)) {
                return PZ;
            }
            if (ZN.isWithinAngle(minimumRotation, maximumRotation)) {
                return ZN;
            }
            if (ZP.isWithinAngle(minimumRotation, maximumRotation)) {
                return ZP;
            }
        }
        if (diagonals) {
            if (NP.isWithinAngle(minimumRotation, maximumRotation)) {
                return NP;
            }
            if (PN.isWithinAngle(minimumRotation, maximumRotation)) {
                return PN;
            }
            if (NN.isWithinAngle(minimumRotation, maximumRotation)) {
                return NN;
            }
            if (PP.isWithinAngle(minimumRotation, maximumRotation)) {
                return PP;
            }
        }
        return null;
    }

    private double getHorizontalBorderGap()
    {
        return this.getHorizontalBorderGap(base_id, base_kd);
    }

    private double getHorizontalBorderGap(double i, double k)
    {
        if (this == NZ) {
            return i % 1;
        }
        if (this == PZ) {
            return 1 - (i % 1);
        }
        if (this == ZN) {
            return k % 1;
        }
        if (this == ZP) {
            return 1 - (k % 1);
        }
        return 0D;
    }

    public boolean isTunnelAhead(World world, int i, int j, int k)
    {
        IBlockState state = getState(world, i + this._i, j + 1, k + this._k);
        if (isFullEmpty(state)) {
            Material aboveMaterial = world.getBlockState(new BlockPos(i + this._i, j + 2, k + this._k)).getMaterial();
            return isSolid(aboveMaterial);
        }
        return false;
    }

    public static HashSet<Orientation> getClimbingOrientations(EntityPlayer p, boolean orthogonals, boolean diagonals)
    {
        float rotation = p.rotationYaw % 360F;
        if (rotation < 0) {
            rotation += 360F;
        }

        if (_getClimbingOrientationsHashSet == null) {
            _getClimbingOrientationsHashSet = new HashSet<>();
        } else {
            _getClimbingOrientationsHashSet.clear();
        }

        if (orthogonals) {
            NZ.addTo(rotation);
            PZ.addTo(rotation);
            ZN.addTo(rotation);
            ZP.addTo(rotation);
        }
        if (diagonals) {
            NP.addTo(rotation);
            PN.addTo(rotation);
            NN.addTo(rotation);
            PP.addTo(rotation);
        }
        return _getClimbingOrientationsHashSet;
    }

    private static HashSet<Orientation> _getClimbingOrientationsHashSet = null;

    private void addTo(float rotation)
    {
        if (this.isRotationForClimbing(rotation)) {
            _getClimbingOrientationsHashSet.add(this);
        }
    }

    public boolean isFeetLadderSubstitute(World world, int bi, int j, int bk)
    {
        int i = bi + this._i;
        int k = bk + this._k;

        return this.isLadderSubstitute(world, i, j, k, middle) > 0 || this.isLadderSubstitute(world, i, j, k, base) > 0;
    }

    public boolean isHandsLadderSubstitute(World world, int bi, int j, int bk)
    {
        int i = bi + this._i;
        int k = bk + this._k;

        return this.isLadderSubstitute(world, i, j, k, middle) > 0 || this.isLadderSubstitute(world, i, j, k, base) > 0 || this.isLadderSubstitute(world, i, j, k, sub) > 0;
    }

    private int isLadderSubstitute(World worldObj, int i, int j, int k, int halfOffset)
    {
        world = worldObj;
        remote_i = i;
        all_j = j;
        remote_k = k;
        all_offset = 0;
        return this.isLadderSubstitute(halfOffset, null);
    }

    public void seekClimbGap(float rotation, World world, int i, double id, double jhd, int k, double kd, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, HandsClimbing[] inout_handsClimbing, FeetClimbing[] inout_feetClimbing, ClimbGap out_handsClimbGap, ClimbGap out_feetClimbGap)
    {
        if (this.isRotationForClimbing(rotation)) {
            this.initialize(world, i, id, jhd, k, kd);

            inout_handsClimbing[0] = inout_handsClimbing[0].max(this.handsClimbing(isClimbCrawling, isCrawlClimbing, isCrawling), out_handsClimbGap, _climbGapOuterTemp);
            inout_feetClimbing[0] = inout_feetClimbing[0].max(this.feetClimbing(isClimbCrawling, isCrawlClimbing, isCrawling), out_feetClimbGap, _climbGapOuterTemp);
        }
    }

    private HandsClimbing handsClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling)
    {
        Orientation._climbGapOuterTemp.reset();
        _climbGapTemp.reset();

        initializeOffset(3D, isClimbCrawling, isCrawlClimbing, isCrawling);

        HandsClimbing result = HandsClimbing.None;
        int gap;

        if (this.isLadderSubstitute(middle, _climbGapTemp) > 0) {
            if (jh_offset > 1D - _handClimbingHoldGap) {
                result = result.max(HandsClimbing.Up, Orientation._climbGapOuterTemp, _climbGapTemp);
            } else {
                result = result.max(HandsClimbing.None, Orientation._climbGapOuterTemp, _climbGapTemp); // No climbing (hands not long enough - up)
            }
        }

        if (this.isLadderSubstitute(base, _climbGapTemp) > 0) {
            if (jh_offset < _handClimbingHoldGap) {
                result = result.max(HandsClimbing.BottomHold, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (pulling weight up) or hold when climbing down
            } else {
                result = result.max(HandsClimbing.Up, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (pulling weight up)
            }
        }

        _climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

        if ((gap = this.isLadderSubstitute(sub, _climbGapTemp)) > 0 && !(isCrawling && gap > 1)) {
            if (!isClimbCrawling && gap > 2) {
                result = result.max(HandsClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (pulling upper body into gap)
            } else if (isClimbCrawling && gap > 1) {
                result = result.max(HandsClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (crawling into upper gap)
            } else // (no gap for balancing upper body)
                if (jh_offset < _handClimbingHoldGap) {
                    if (grabType == AroundGrab) {
                        result = result.max(HandsClimbing.Up, Orientation._climbGapOuterTemp, _climbGapTemp); // Lower climbing up ladder
                    } else {
                        result = result.max(HandsClimbing.TopHold, Orientation._climbGapOuterTemp, _climbGapTemp); // Lower holding
                    }
                } else if (grabType == AroundGrab) {
                    result = result.max(HandsClimbing.TopHold, Orientation._climbGapOuterTemp, _climbGapTemp); // Sinking to lower holding level
                } else {
                    result = result.max(HandsClimbing.Sink, Orientation._climbGapOuterTemp, _climbGapTemp); // Sinking to lower holding level
                }
        }

        if ((gap = this.isLadderSubstitute(subSub, _climbGapTemp)) > 0 && !isCrawling) {
            if ((gap > 2 && !isCrawlClimbing) || grabType == AroundGrab || (gap > 1 && isClimbCrawling)) // (hands not long enough - down)
            {
                if (jh_offset < _handClimbingHoldGap && !isClimbCrawling) {
                    result = result.max(HandsClimbing.TopHold, Orientation._climbGapOuterTemp, _climbGapTemp); // Upper holding
                } else if (isClimbCrawling) {
                    result = result.max(HandsClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp); // Sinking to upper holding level
                } else {
                    result = result.max(HandsClimbing.Sink, Orientation._climbGapOuterTemp, _climbGapTemp); // Sinking to upper holding level
                }
            }
        }

        return result;
    }

    private FeetClimbing feetClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling)
    {
        Orientation._climbGapOuterTemp.reset();
        _climbGapTemp.reset();

        initializeOffset(0D, isClimbCrawling, isCrawlClimbing, isCrawling);
        FeetClimbing result = FeetClimbing.None;
        int gap;

        if (this.isLadderSubstitute(top, _climbGapTemp) > 0) // No climbing (feet not long enough - up)
        {
            result = result.max(FeetClimbing.None, Orientation._climbGapOuterTemp, _climbGapTemp);
        }

        _climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

        if ((gap = this.isLadderSubstitute(middle, _climbGapTemp)) > 0 && !isCrawling) {
            if (gap > 3 && !isClimbCrawling) {
                if (!isCrawlClimbing) {
                    result = result.max(FeetClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 2 (pushing upper body up into big gap)
                } else {
                    result = result.max(FeetClimbing.None, Orientation._climbGapOuterTemp, _climbGapTemp);
                }
            } else if ((isClimbCrawling || isCrawlClimbing) && gap > 1) {
                if (isCrawlClimbing) {
                    result = result.max(FeetClimbing.BaseWithHands, Orientation._climbGapOuterTemp, _climbGapTemp);
                } else {
                    result = result.max(FeetClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp);
                }
            } else if (gap > 2) {
                result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (no gap for balancing upper body)
            } else {
                result = result.max(FeetClimbing.TopWithHands, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing with hands only
            }
        }

        if ((gap = this.isLadderSubstitute(base, _climbGapTemp)) > 0) {
            if (gap > 3 && !isCrawling && !isCrawlClimbing) {
                result = result.max(FeetClimbing.FastUp, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 2 (pushing whole body up into big gap)
            } else if (gap > 2 && !isCrawling) {
                if (!isClimbCrawling) {
                    if (jh_offset < _handClimbingHoldGap) {
                        result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, Orientation._climbGapOuterTemp, _climbGapTemp);
                    } else {
                        result = result.max(FeetClimbing.SlowUpWithSinkWithoutHands, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing speed 1 (no gap for balancing whole body)
                    }
                } else {
                    result = result.max(FeetClimbing.None, Orientation._climbGapOuterTemp, _climbGapTemp);
                }
            } else if (jh_offset < 1D - _handClimbingHoldGap) {
                result = result.max(FeetClimbing.BaseWithHands, Orientation._climbGapOuterTemp, _climbGapTemp); // Climbing with hands only
            } else {
                result = result.max(FeetClimbing.BaseHold, Orientation._climbGapOuterTemp, _climbGapTemp);
            }
        }

        if ((this.isLadderSubstitute(sub, _climbGapTemp)) > 0) {
            result = result.max(FeetClimbing.None, Orientation._climbGapOuterTemp, _climbGapTemp); // No climbing (feet not long enough - down)
        }

        if (isCrawlClimbing || isCrawling) {
            result = result.max(FeetClimbing.BaseWithHands, Orientation._climbGapOuterTemp, _climbGapTemp);
        }

        return result;
    }

    private int isLadderSubstitute(int local_Offset, ClimbGap out_climbGap)
    {
        initializeLocal(local_Offset);

        int gap;
        if (local_half == 1) {
            if (this.hasHalfHold()) {
                if (!grabRemote) {
                    boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0);
                    boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1);
                    boolean overAccessible = isBaseAccessible(1, false, true);
                    boolean overOverAccessible = isBaseAccessible(2, false, true);
                    boolean overFullAccessible = overAccessible && this.isFullAccessible(1, grabRemote);
                    boolean overOverFullAccessible = overAccessible && this.isFullExtentAccessible(2, grabRemote);

                    if (overLadder) {
                        if (overOverLadder) {
                            gap = 1;
                        } else if (overOverAccessible) {
                            gap = 1;
                        } else {
                            gap = 1;
                        }
                    } else if (overAccessible) {
                        if (overFullAccessible) {
                            if (overOverFullAccessible) {
                                gap = 5;
                            } else {
                                gap = crawl ? 3 : 5;
                            }
                        } else if (overOverLadder) {
                            gap = 5;
                        } else {
                            gap = 1;
                        }
                    } else {
                        gap = 1;
                    }
                } else if (isBaseAccessible(0)) {
                    if (this.isUpperHalfFrontEmpty(remote_i, 0, remote_k)) {
                        if (this.isFullAccessible(1, grabRemote)) {
                            if (this.isFullExtentAccessible(2, grabRemote)) {
                                gap = 5;
                            } else if (this.isJustLowerHalfExtentAccessible(2)) {
                                gap = 4;
                            } else {
                                gap = 3;
                            }
                        } else if (this.isLowerHalfAccessible(1, grabRemote)) {
                            gap = 2;
                        } else {
                            gap = 1;
                        }
                    } else {
                        gap = 1;
                    }
                } else {
                    gap = 0;
                }
            } else {
                gap = 0;
            }
        } else if (this.hasBottomHold()) {
            if (!grabRemote) {
                boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0);
                boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1);
                boolean overAccessible = isBaseAccessible(0, false, true);
                boolean overOverAccessible = isBaseAccessible(1, false, true);
                boolean overFullAccessible = overAccessible && this.isFullAccessible(0, grabRemote);
                boolean overOverFullAccessible = overAccessible && this.isFullExtentAccessible(1, grabRemote);

                if (overLadder) {
                    if (overOverLadder) {
                        gap = 1;
                    } else if (overOverAccessible) {
                        gap = 1;
                    } else {
                        gap = 1;
                    }
                } else if (overAccessible) {
                    if (overFullAccessible) {
                        if (overOverAccessible) {
                            if (overOverFullAccessible) {
                                gap = 4;
                            } else {
                                gap = crawl ? 2 : 4;
                            }
                        } else {
                            gap = 2;
                        }
                    } else if (overOverLadder) {
                        gap = 2;
                    } else {
                        gap = 1;
                    }
                } else {
                    gap = 1;
                }
            } else if (isBaseAccessible(0)) {
                if (this.isFullAccessible(0, grabRemote)) {
                    if (this.isFullExtentAccessible(1, grabRemote)) {
                        gap = 4;
                    } else {
                        gap = 2;
                    }
                } else {
                    gap = 1;
                }
            } else {
                gap = 0;
            }
        } else {
            gap = 0;
        }

        if (out_climbGap != null && gap > 0) {
            out_climbGap.Block = grabBlock;
            out_climbGap.Meta = grabMeta;
            out_climbGap.CanStand = gap > 3;
            out_climbGap.MustCrawl = gap > 1 && gap < 4;
            out_climbGap.Direction = this;
        }
        return gap;
    }

    private boolean hasHalfHold()
    {
        if (Config.isFreeBaseClimb()) {
            if (isOnLadder(0) && this.isOnLadderFront(0)) {
                return this.setHalfGrabType(AroundGrab, getBaseBlock(0), false);
            }

            if (this.remoteLadderClimbing(0)) {
                return this.setHalfGrabType(AroundGrab, getRemoteBlock(0), true);
            }
        }

        IBlockState remoteState = getRemoteBlockState(0);
        if (isEmpty(base_i, 0, base_k)) {
            if (remoteState.getBlock() == Block.getBlockFromName("iron_bars") && this.headedToFrontWall(remote_i, 0, remote_k, remoteState)) {
                return this.setHalfGrabType(HalfGrab, remoteState);
            }
        }

        IBlockState wallId = getBlockState(base_i, 0, base_k);
        if (wallId.getBlock() == Block.getBlockFromName("iron_bars") && this.headedToBaseWall(0, wallId)) {
            return this.setHalfGrabType(HalfGrab, wallId, false);
        }

        if (Config._freeFenceClimbing.getValue()) {
            if (isFence(remoteState) && this.headedToFrontWall(remote_i, 0, remote_k, remoteState)) {
                if (!isFence(getBaseBlockState(0))) {
                    return this.setHalfGrabType(HalfGrab, remoteState);
                } else if (this.headedToFrontSideWall(remote_i, 0, remote_k, remoteState)) {
                    return this.setHalfGrabType(HalfGrab, remoteState);
                }
            }

            IBlockState remoteBelowState = getRemoteBlockState(-1);
            if (isFence(remoteBelowState) && this.headedToFrontWall(remote_i, -1, remote_k, remoteBelowState)) {
                if (!isFence(getBaseBlockState(-1))) {
                    return this.setHalfGrabType(HalfGrab, remoteState);
                } else if (this.headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowState)) {
                    return this.setHalfGrabType(HalfGrab, remoteState);
                }
            }

            if (isFence(wallId) && this.headedToBaseWall(0, wallId)) {
                return this.setHalfGrabType(HalfGrab, wallId, false);
            }

            IBlockState belowWallId = getWallBlockId(base_i, -1, base_k);
            if (isFence(belowWallId) && this.headedToBaseWall(-1, belowWallId)) {
                return this.setHalfGrabType(HalfGrab, belowWallId, false);
            }

            if (remoteState.getBlock() == Block.getBlockFromName("cobblestone_wall") && !this.headedToRemoteFlatWall(remoteState, 0)) {
                return this.setHalfGrabType(HalfGrab, remoteState);
            }

            if (remoteBelowState.getBlock() == Block.getBlockFromName("cobblestone_wall") && !this.headedToRemoteFlatWall(remoteBelowState, -1)) {
                return this.setHalfGrabType(HalfGrab, remoteBelowState);
            }
        }

        if (isBottomHalfBlock(remoteState) || (isStairCompact(remoteState) && this.isBottomStairCompactNotBack(remoteState) && !(isStairCompact(getBaseBlockState(-1)) && this.isBottomStairCompactFront(getBaseBlockState(-1))))) {
            return this.setHalfGrabType(HalfGrab, remoteState);
        }

        if (isTrapDoor(remoteState) && isClosedTrapDoor(remoteState)) {
            return this.setHalfGrabType(HalfGrab, remoteState);
        }

        IBlockState baseState = getBaseBlockState(0);
        if (isTrapDoor(baseState) && !isClosedTrapDoor(baseState)) {
            return this.setHalfGrabType(HalfGrab, baseState, false);
        }

        if (Config.isFreeBaseClimb()) {
            int meta = this.baseVineClimbing(0);
            if (meta > -1) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }
            meta = this.remoteVineClimbing(0);
            if (meta > -1) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }
        }

        return this.setHalfGrabType(NoGrab);
    }

    private boolean hasBottomHold()
    {
        if (Config.isFreeBaseClimb()) {
            if (isOnLadder(-1) && this.isOnLadderFront(-1)) {
                return this.setBottomGrabType(AroundGrab, getBaseBlock(-1), false);
            }

            if (isOnLadder(0) && this.isOnLadderFront(0)) {
                return this.setBottomGrabType(AroundGrab, getBaseBlock(0), false);
            }

            if (this.remoteLadderClimbing(-1)) {
                return this.setBottomGrabType(AroundGrab, getRemoteBlock(-1), true);
            }

            if (this.remoteLadderClimbing(0)) {
                return this.setBottomGrabType(AroundGrab, getRemoteBlock(0), true);
            }
        }

        IBlockState remoteState = getRemoteBlockState(0);
        IBlockState remoteBelowState = getRemoteBlockState(-1);
        boolean remoteLowerHalfEmpty = this.isLowerHalfFrontFullEmpty(remote_i, 0, remote_k);

        if (isEmpty(base_i, -1, base_k)) {
            if (remoteBelowState.getBlock() == Block.getBlockFromName("iron_bars") && this.headedToFrontWall(remote_i, -1, remote_k, remoteBelowState)) {
                return this.setBottomGrabType(HalfGrab, remoteBelowState);
            }
        }

        if (Config._freeFenceClimbing.getValue()) {
            IBlockState baseBelowState = getBaseBlockState(-1);
            if (isFence(remoteBelowState) && this.headedToFrontWall(remote_i, -1, remote_k, remoteBelowState)) {
                if (!isFence(baseBelowState)) {
                    return this.setBottomGrabType(HalfGrab, remoteBelowState);
                } else if (this.headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowState)) {
                    return this.setBottomGrabType(HalfGrab, remoteBelowState);
                }
            }

            if (remoteBelowState.getBlock() == Block.getBlockFromName("cobblestone_wall") && !this.headedToRemoteFlatWall(remoteBelowState, -1)) {
                return this.setHalfGrabType(HalfGrab, remoteBelowState);
            }

            if (remoteState.getBlock() == Block.getBlockFromName("cobblestone_wall") && !this.headedToRemoteFlatWall(remoteState, 0)) {
                return this.setHalfGrabType(HalfGrab, remoteState);
            }
        }

        IBlockState belowWallState = getWallBlockId(base_i, -1, base_k);
        if (belowWallState != null) {
            if (isEmpty(base_i - this._i, 0, base_k - this._k) && isEmpty(base_i - this._i, -1, base_k - this._k)) {
                if (belowWallState.getBlock() == Block.getBlockFromName("iron_bars") && this.headedToBaseWall(-1, belowWallState)) {
                    return this.setBottomGrabType(HalfGrab, belowWallState, false);
                }

                if (this.headedToBaseGrabWall(-1, belowWallState)) {
                    return this.setBottomGrabType(HalfGrab, belowWallState, false);
                }
            }

            if (Config._freeFenceClimbing.getValue() && isFence(belowWallState) && this.headedToBaseWall(-1, belowWallState)) {
                return this.setBottomGrabType(HalfGrab, belowWallState, false);
            }

            return false;
        }

        if (remoteLowerHalfEmpty && isBaseAccessible(-1, true, false)) {
            if (this.isUpperHalfFrontAnySolid(remote_i, -1, remote_k)) {
                if (!isBottomHalfBlock(remoteBelowState)) {
                    if (!isStairCompact(remoteBelowState) || !this.isBottomStairCompactFront(remoteBelowState)) {
                        if (!isDoor(remoteBelowState) || isDoorTop(remoteBelowState)) {
                            if (!isDoor(getBaseBlockState(0)) || !this.isDoorFrontBlocked(base_i, 0, base_k)) {
                                if (Config._freeFenceClimbing.getValue() || !isFence(getRemoteBlockState(-1))) {
                                    return this.setBottomGrabType(HalfGrab, remoteBelowState);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isStairCompact(remoteState)) {
            if (isTopStairCompact(remoteState) && !this.isTopStairCompactBack(remoteState) && isUpperHalfFrontFullSolid(remote_i, -1, remote_k)) {
                return this.setBottomGrabType(HalfGrab, remoteBelowState);
            }
        }

        IBlockState baseBelowState = getBaseBlockState(-1);

        // for trap door bottom hold
        //if(isTrapDoor(remoteState) && isClosedTrapDoor(remoteState))
        //	return setBottomGrabType(HalfGrab, remoteState);

        // for trap door top hold
        if (isTrapDoor(baseBelowState) && !isClosedTrapDoor(baseBelowState)) {
            return this.setBottomGrabType(HalfGrab, baseBelowState, false);
        }

        if (isDoor(baseBelowState) && isDoorTop(baseBelowState) && this.isDoorFrontBlocked(base_i, -1, base_k) && isBaseAccessible(0)) {
            return this.setBottomGrabType(HalfGrab, baseBelowState, false);
        }

        if (Config.isFreeBaseClimb()) {
            int meta = this.baseVineClimbing(-1);
            if (meta != DefaultMeta) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }

            meta = this.baseVineClimbing(0);
            if (meta != DefaultMeta) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }

            meta = this.remoteVineClimbing(-1);
            if (meta != DefaultMeta) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }

            meta = this.remoteVineClimbing(0);
            if (meta != DefaultMeta) {
                return this.setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            }
        }

        return this.setBottomGrabType(NoGrab);
    }

    private boolean setHalfGrabType(int type)
    {
        return this.setHalfGrabType(type, (Block) null);
    }

    private boolean setHalfGrabType(int type, Block block)
    {
        return this.setHalfGrabType(type, block, true);
    }

    private boolean setHalfGrabType(int type, IBlockState state)
    {
        return this.setHalfGrabType(type, state.getBlock());
    }

    private boolean setHalfGrabType(int type, Block block, boolean remote)
    {
        return this.setHalfGrabType(type, block, remote, -1);
    }

    private boolean setHalfGrabType(int type, IBlockState state, boolean remote)
    {
        return this.setHalfGrabType(type, state.getBlock(), remote);
    }

    private boolean setHalfGrabType(int type, Block block, boolean remote, int metaClimb)
    {
        boolean hasGrab = type != NoGrab;
        if (hasGrab && remote && this._isDiagonal) {
            boolean edgeConnectCCW = this.rotate(90).isUpperHalfFrontEmpty(base_i, 0, remote_k);
            boolean edgeConnectCW = this.rotate(-90).isUpperHalfFrontEmpty(remote_i, 0, base_k);
            hasGrab = edgeConnectCCW && edgeConnectCW;
        }
        return setGrabType(type, block, remote, hasGrab, metaClimb);
    }

    private boolean setBottomGrabType(int type)
    {
        return this.setBottomGrabType(type, (Block) null);
    }

    private boolean setBottomGrabType(int type, Block block)
    {
        return this.setBottomGrabType(type, block, true);
    }

    private boolean setBottomGrabType(int type, IBlockState state)
    {
        return this.setBottomGrabType(type, state.getBlock());
    }

    private boolean setBottomGrabType(int type, Block block, boolean remote)
    {
        return this.setBottomGrabType(type, block, remote, -1);
    }

    private boolean setBottomGrabType(int type, IBlockState state, boolean remote)
    {
        return this.setBottomGrabType(type, state.getBlock(), remote);
    }

    private boolean setBottomGrabType(int type, Block block, boolean remote, int metaClimb)
    {
        boolean hasGrab = type != NoGrab;
        if (hasGrab && remote && this._isDiagonal) {
            boolean edgeConnectCCW = this.rotate(90).isLowerHalfFrontFullEmpty(base_i, 0, remote_k);
            boolean edgeConnectCW = this.rotate(-90).isLowerHalfFrontFullEmpty(remote_i, 0, base_k);
            hasGrab = edgeConnectCCW && edgeConnectCW;
        }
        return setGrabType(type, block, remote, hasGrab, metaClimb);
    }

    private static boolean setGrabType(int type, Block block, boolean remote, boolean hasGrab, int metaClimb)
    {
        grabRemote = remote;
        grabType = hasGrab ? type : NoGrab;
        grabBlock = block;
        grabMeta = metaClimb;
        return hasGrab;
    }

    private boolean setClimbingAngles()
    {
        switch (this._i) {
            case -1:
                switch (this._k) {
                    case -1:
                        return this.setClimbingAngles(135); //NN
                    case 0:
                        return this.setClimbingAngles(90); // NZ
                    case 1:
                        return this.setClimbingAngles(45); // NP
                }
                break;
            case 0:
                switch (this._k) {
                    case -1:
                        return this.setClimbingAngles(180); // ZN
                    case 0:
                        return this.setClimbingAngles(0, 360); // ZZ
                    case 1:
                        return this.setClimbingAngles(0); // ZP
                }
                break;
            case 1:
                switch (this._k) {
                    case -1:
                        return this.setClimbingAngles(225); // PN
                    case 0:
                        return this.setClimbingAngles(270); // PZ
                    case 1:
                        return this.setClimbingAngles(315); // PP
                }
                break;
        }
        return false;
    }

    private boolean setClimbingAngles(float directionAngle)
    {
        this._directionAngle = directionAngle;
        float halfAreaAngle = (this._isDiagonal ? Config._freeClimbingDiagonalDirectionAngle.getValue() : Config._freeClimbingOrthogonalDirectionAngle.getValue()) / 2F;
        return this.setClimbingAngles(directionAngle - halfAreaAngle, directionAngle + halfAreaAngle);
    }

    private boolean setClimbingAngles(float mimimumClimbingAngle, float maximumClimbingAngle)
    {
        if (mimimumClimbingAngle < 0F) {
            mimimumClimbingAngle += 360F;
        }

        if (maximumClimbingAngle > 360F) {
            maximumClimbingAngle -= 360F;
        }

        this._mimimumClimbingAngle = mimimumClimbingAngle;
        this._maximumClimbingAngle = maximumClimbingAngle;

        return mimimumClimbingAngle != maximumClimbingAngle;
    }

    private boolean isWithinAngle(float minimumRotation, float maximumRotation)
    {
        return isWithinAngle(this._directionAngle, minimumRotation, maximumRotation);
    }

    private boolean isRotationForClimbing(float rotation)
    {
        return isWithinAngle(rotation, this._mimimumClimbingAngle, this._maximumClimbingAngle);
    }

    private static boolean isWithinAngle(float rotation, float minimumRotation, float maximumRotation)
    {
        if (minimumRotation > maximumRotation) {
            return rotation >= minimumRotation || rotation <= maximumRotation;
        } else {
            return rotation >= minimumRotation && rotation <= maximumRotation;
        }
    }

    private int baseVineClimbing(int j_offset)
    {
        boolean result = isOnVine(j_offset);
        if (result) {
            result = this.isOnVineFront(j_offset);
            if (result) {
                return VineFrontMeta;
            }

            if (this.baseVineClimbing(j_offset, PZ) || this.baseVineClimbing(j_offset, NZ) || this.baseVineClimbing(j_offset, ZP) || this.baseVineClimbing(j_offset, ZN)) {
                return VineSideMeta;
            }
        }
        return DefaultMeta;
    }

    private boolean baseVineClimbing(int j_offset, Orientation orientation)
    {
        if (orientation == this) {
            return false;
        }

        return orientation.rotate(180).hasVineOrientation(world, base_i, local_offset + j_offset, base_k) && orientation.getHorizontalBorderGap() >= 0.65;
    }

    private boolean remoteLadderClimbing(int j_offset)
    {
        return isBehindLadder(j_offset) && this.isOnLadderBack(j_offset);
    }

    private int remoteVineClimbing(int j_offset)
    {
        if (isBehindVine(j_offset) && this.isOnVineBack(j_offset)) {
            return VineFrontMeta;
        }

        if (this.remoteVineClimbing(j_offset, PZ) || this.remoteVineClimbing(j_offset, NZ) || this.remoteVineClimbing(j_offset, ZP) || this.remoteVineClimbing(j_offset, ZN)) {
            return VineSideMeta;
        }

        return DefaultMeta;
    }

    private boolean remoteVineClimbing(int j_offset, Orientation orientation)
    {
        if (orientation == this) {
            return false;
        }

        int i = base_i - orientation._i;
        int k = base_k - orientation._k;
        return isVine(getBlockState(i, j_offset, k)) && orientation.hasVineOrientation(world, i, local_offset + j_offset, k) && orientation.getHorizontalBorderGap() >= 0.65F;
    }

    private static boolean isOnLadder(int j_offset)
    {
        IBlockState state = getBaseBlockState(j_offset);
        if (isLadder(state)) {
            return true;
        }
        if (isVine(state)) {
            return false;
        }
        return isClimbable(world, base_i, local_offset + j_offset, base_k);
    }

    private static boolean isBehindLadder(int j_offset)
    {
        IBlockState state = getRemoteBlockState(j_offset);
        if (isLadder(state)) {
            return true;
        }
        if (isVine(state)) {
            return false;
        }
        return isClimbable(world, remote_i, local_offset + j_offset, remote_k);
    }

    private static boolean isOnVine(int j_offset)
    {
        return isVine(getBaseBlockState(j_offset));
    }

    private static boolean isBehindVine(int j_offset)
    {
        return isVine(getRemoteBlockState(j_offset));
    }

    private static boolean isOnLadderOrVine(int j_offset)
    {
        return isLadderOrVine(getBaseBlockState(j_offset)) || grabBlock == Block.getBlockFromName("vine");
    }

    public static boolean isLadder(IBlockState state)
    {
        return state.getBlock() == Block.getBlockFromName("ladder");
    }

    public static boolean isVine(IBlockState state)
    {
        return state.getBlock() == Block.getBlockFromName("vine");
    }

    public static boolean isLadderOrVine(IBlockState state)
    {
        return isLadder(state) || isVine(state);
    }

    public static boolean isKnownLadder(IBlockState state)
    {
        return isLadder(state);
    }

    public static boolean isClimbable(World world, int x, int y, int z)
    {
        BlockPos position = new BlockPos(x, y, z);
        IBlockState blockState = world.getBlockState(position);
        return blockState.getBlock().isLadder(blockState, world, position, Minecraft.getMinecraft().player);
    }

    private boolean isOnLadderFront(int j_offset)
    {
        return this.hasLadderOrientation(base_i, j_offset, base_k);
    }

    private boolean isOnLadderBack(int j_offset)
    {
        return this.rotate(180).hasLadderOrientation(remote_i, j_offset, remote_k);
    }

    private boolean isOnVineFront(int j_offset)
    {
        return this.hasVineOrientation(world, base_i, local_offset + j_offset, base_k);
    }

    private boolean isOnVineBack(int j_offset)
    {
        return this.rotate(180).hasVineOrientation(world, remote_i, local_offset + j_offset, remote_k);
    }

    /**
     * @return Returns null if FACING property is not found in the BlockState.
     */
    public static Orientation getKnownLadderOrientation(World world, int i, int j, int k)
    {
        IBlockState state = getState(world, i, j, k);
        EnumFacing facing = getValue(state, BlockLadder.FACING);
        return facing == null ? null : FacingToOrientation.get(facing);
    }

    public boolean hasVineOrientation(World world, int i, int j, int k)
    {
        IBlockState state = getState(world, i, j, k);
        if (this == NZ) {
            return getValue(state, BlockVine.EAST);
        }
        if (this == PZ) {
            return getValue(state, BlockVine.WEST);
        }
        if (this == ZP) {
            return getValue(state, BlockVine.NORTH);
        }
        if (this == ZN) {
            return getValue(state, BlockVine.SOUTH);
        }
        return false;
    }

    private boolean hasLadderOrientation(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        EnumFacing value = getValue(state, BlockLadder.FACING);
        return value == this._facing;
    }

    public boolean isRemoteSolid(World world, int i, int j, int k)
    {
        return isSolid(world.getBlockState(new BlockPos(i + this._i, j, k + this._k)).getMaterial());
    }

    public static Orientation getOpenTrapDoorOrientation(World world, int i, int j, int k)
    {
        IBlockState state = getState(world, i, j, k);
        if (!isClosedTrapDoor(state)) {
            return FacingToOrientation.get(getValue(state, BlockTrapDoor.FACING));
        }
        return null;
    }

    private static boolean isOnOpenTrapDoor(int j_offset)
    {
        IBlockState state = getBaseBlockState(j_offset);
        return isTrapDoor(state) && !isClosedTrapDoor(state);
    }

    private boolean isTrapDoorFront(IBlockState state)
    {
        return this._facings.contains(getValue(state, BlockTrapDoor.FACING));
    }

    private boolean isBottomStairCompactNotBack(IBlockState state)
    {
        return !isTopStairCompact(state) && !this.isStairCompactBack(state);
    }

    private boolean isBottomStairCompactFront(IBlockState state)
    {
        return !isTopStairCompact(state) && this.isStairCompactFront(state);
    }

    private boolean isTopStairCompactFront(IBlockState state)
    {
        return isTopStairCompact(state) && this.isStairCompactFront(state);
    }

    private boolean isTopStairCompactBack(IBlockState state)
    {
        return isTopStairCompact(state) && this.isStairCompactBack(state);
    }

    private boolean isStairCompactFront(IBlockState state)
    {
        EnumFacing facing = getValue(state, BlockStairs.FACING);
        BlockStairs.EnumShape shape = getValue(state, BlockStairs.SHAPE);

        if (this == NZ) {
            return ((north(facing) && outer_left(shape)) || (south(facing) && outer_right(shape)) || (west(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        }
        if (this == PZ) {
            return ((north(facing) && outer_right(shape)) || (south(facing) && outer_left(shape)) || (east(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        }
        if (this == ZP) {
            return ((east(facing) && outer_right(shape)) || (west(facing) && outer_left(shape)) || (south(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        }
        if (this == ZN) {
            return ((east(facing) && outer_left(shape)) || (west(facing) && outer_right(shape)) || (north(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        }
        if (this == PN) {
            return ((south(facing) && outer_left(shape)) || (west(facing) && outer_right(shape)) || (east(facing) && !inner_right(shape)) || (north(facing) && !inner_left(shape)));
        }
        if (this == PP) {
            return ((north(facing) && outer_right(shape)) || (west(facing) && outer_left(shape)) || (east(facing) && !inner_left(shape)) || (south(facing) && !inner_right(shape)));
        }
        if (this == NN) {
            return ((east(facing) && outer_left(shape)) || (south(facing) && outer_right(shape)) || (north(facing) && !inner_right(shape)) || (west(facing) && !inner_left(shape)));
        }
        if (this == NP) {
            return ((east(facing) && outer_right(shape)) || (north(facing) && outer_left(shape)) || (south(facing) && !inner_left(shape)) || (west(facing) && !inner_right(shape)));
        }
        return false;
    }

    private boolean isStairCompactBack(IBlockState state)
    {
        EnumFacing facing = getValue(state, BlockStairs.FACING);
        BlockStairs.EnumShape shape = getValue(state, BlockStairs.SHAPE);

        if (this == NZ) {
            return ((north(facing) && inner_right(shape)) || (south(facing) && inner_left(shape)) || (east(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        }
        if (this == PZ) {
            return ((north(facing) && inner_left(shape)) || (south(facing) && inner_right(shape)) || (west(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        }
        if (this == ZP) {
            return ((east(facing) && inner_left(shape)) || (west(facing) && inner_right(shape)) || (north(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        }
        if (this == ZN) {
            return ((east(facing) && inner_right(shape)) || (west(facing) && inner_left(shape)) || (south(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        }
        if (this == PN) {
            return ((east(facing) && inner_right(shape)) || (north(facing) && inner_left(shape)) || (south(facing) && !outer_left(shape)) || (west(facing) && !outer_right(shape)));
        }
        if (this == PP) {
            return ((east(facing) && inner_left(shape)) || (south(facing) && inner_right(shape)) || (north(facing) && !outer_right(shape)) || (west(facing) && !outer_left(shape)));
        }
        if (this == NN) {
            return ((north(facing) && inner_right(shape)) || (west(facing) && inner_left(shape)) || (east(facing) && !outer_left(shape)) || (south(facing) && !outer_right(shape)));
        }
        if (this == NP) {
            return ((south(facing) && inner_left(shape)) || (west(facing) && inner_right(shape)) || (east(facing) && !outer_right(shape)) || (north(facing) && !outer_left(shape)));
        }
        return false;
    }

    private static boolean outer_left(BlockStairs.EnumShape shape)
    {
        return shape == BlockStairs.EnumShape.OUTER_LEFT;
    }

    private static boolean inner_left(BlockStairs.EnumShape shape)
    {
        return shape == BlockStairs.EnumShape.INNER_LEFT;
    }

    private static boolean straight(BlockStairs.EnumShape shape)
    {
        return shape == BlockStairs.EnumShape.STRAIGHT;
    }

    private static boolean inner_right(BlockStairs.EnumShape shape)
    {
        return shape == BlockStairs.EnumShape.INNER_RIGHT;
    }

    private static boolean outer_right(BlockStairs.EnumShape shape)
    {
        return shape == BlockStairs.EnumShape.OUTER_RIGHT;
    }

    private static boolean west(EnumFacing facing)
    {
        return facing == EnumFacing.WEST;
    }

    private static boolean south(EnumFacing facing)
    {
        return facing == EnumFacing.SOUTH;
    }

    private static boolean north(EnumFacing facing)
    {
        return facing == EnumFacing.NORTH;
    }

    private static boolean east(EnumFacing facing)
    {
        return facing == EnumFacing.EAST;
    }

    private static boolean isTopStairCompact(IBlockState state)
    {
        return getValue(state, BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
    }

    private boolean isFenceGateFront(IBlockState state)
    {
        EnumFacing facing = getValue(state, BlockDirectional.FACING);
        Orientation orientation = FacingToOrientation.get(facing);
        return orientation != null && (this == orientation.rotate(90) || this == orientation.rotate(-90));
    }

    private boolean headedToFrontWall(int i, int j_offset, int k, IBlockState state)
    {
        boolean zn = this.getWallFlag(ZN, i, j_offset, k, state);
        boolean zp = this.getWallFlag(ZP, i, j_offset, k, state);
        boolean nz = this.getWallFlag(NZ, i, j_offset, k, state);
        boolean pz = this.getWallFlag(PZ, i, j_offset, k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz) {
            zn = zp = nz = pz = true;
        }

        return this.headedToWall(NZ, pz) ||
                this.headedToWall(PZ, nz) ||
                this.headedToWall(ZN, zp) ||
                this.headedToWall(ZP, zn);
    }

    private boolean headedToFrontSideWall(int i, int j_offset, int k, IBlockState state)
    {
        boolean zn = this.getWallFlag(ZN, i, j_offset, k, state);
        boolean zp = this.getWallFlag(ZP, i, j_offset, k, state);
        boolean nz = this.getWallFlag(NZ, i, j_offset, k, state);
        boolean pz = this.getWallFlag(PZ, i, j_offset, k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz) {
            zn = zp = nz = pz = true;
        }

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop) {
            if (kTop) {
                return this.headedToWall(NZ, zp) ||
                        this.headedToWall(PZ, zp) ||
                        this.headedToWall(ZN, pz) ||
                        this.headedToWall(ZP, pz);
            } else {
                return this.headedToWall(NZ, zn) ||
                        this.headedToWall(PZ, zn) ||
                        this.headedToWall(ZN, pz) ||
                        this.headedToWall(ZP, pz);
            }
        } else if (kTop) {
            return this.headedToWall(NZ, zp) ||
                    this.headedToWall(PZ, zp) ||
                    this.headedToWall(ZN, nz) ||
                    this.headedToWall(ZP, nz);
        } else {
            return this.headedToWall(NZ, zn) ||
                    this.headedToWall(PZ, zn) ||
                    this.headedToWall(ZN, nz) ||
                    this.headedToWall(ZP, nz);
        }
    }

    private boolean headedToWall(Orientation base, boolean result)
    {
        if (this == base || this == base.rotate(45) || this == base.rotate(-45)) {
            return result;
        }
        return false;
    }

    private boolean headedToBaseWall(int j_offset, IBlockState state)
    {
        boolean zn = this.getWallFlag(ZN, base_i, j_offset, base_k, state);
        boolean zp = this.getWallFlag(ZP, base_i, j_offset, base_k, state);
        boolean nz = this.getWallFlag(NZ, base_i, j_offset, base_k, state);
        boolean pz = this.getWallFlag(PZ, base_i, j_offset, base_k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz) {
            zn = zp = nz = pz = true;
        }

        boolean leaf = zn || zp || nz || pz;
        boolean coreOnly = !allOnNone && !leaf;

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop) {
            if (kTop) {
                return this.headedToBaseWall(NN, NZ, ZN, zp, nz, pz, zn, coreOnly, leaf);
            } else {
                return this.headedToBaseWall(NP, NZ, ZP, zn, nz, pz, zp, coreOnly, leaf);
            }
        } else if (kTop) {
            return this.headedToBaseWall(PN, PZ, ZN, zp, pz, nz, zn, coreOnly, leaf);
        } else {
            return this.headedToBaseWall(PP, PZ, ZP, zn, pz, nz, zp, coreOnly, leaf);
        }
    }

    private boolean headedToBaseWall(Orientation diagonal, Orientation left, Orientation right, boolean leftFront, boolean rightFrontOpposite, boolean rightFront, boolean leftFrontOpposite, boolean co, boolean leaf)
    {
        if (this == diagonal) {
            return leaf || co;
        }
        if (this == left) {
            return headedToBaseWall(leftFront, rightFrontOpposite, rightFront, leftFrontOpposite, co);
        }
        if (this == right) {
            return headedToBaseWall(rightFront, leftFrontOpposite, leftFront, rightFrontOpposite, co);
        }
        return false;
    }

    private static boolean headedToBaseWall(boolean front, boolean sideOpposite, boolean side, boolean frontOpposite, boolean coreOnly)
    {
        return front || sideOpposite && !side || frontOpposite && !side || coreOnly;
    }

    private boolean headedToBaseGrabWall(int j_offset, IBlockState state)
    {
        boolean zn = this.getWallFlag(ZN, base_i, j_offset, base_k, state);
        boolean zp = this.getWallFlag(ZP, base_i, j_offset, base_k, state);
        boolean nz = this.getWallFlag(NZ, base_i, j_offset, base_k, state);
        boolean pz = this.getWallFlag(PZ, base_i, j_offset, base_k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz) {
            zn = zp = nz = pz = true;
        }

        boolean azn, azp, anz, apz;

        IBlockState aboveBlock = getBlockState(base_i, j_offset + 1, base_k);
        if (isFullEmpty(aboveBlock)) {
            azn = azp = anz = apz = false;
        } else if (isWallBlock(aboveBlock)) {
            azn = this.getWallFlag(ZN, base_i, j_offset + 1, base_k, aboveBlock);
            azp = this.getWallFlag(ZP, base_i, j_offset + 1, base_k, aboveBlock);
            anz = this.getWallFlag(NZ, base_i, j_offset + 1, base_k, aboveBlock);
            apz = this.getWallFlag(PZ, base_i, j_offset + 1, base_k, aboveBlock);
            boolean aboveAllOnNone = Orientation.getAllWallsOnNoWall(aboveBlock);

            if (aboveAllOnNone && !azn && !azp && !anz && !apz) {
                azn = azp = anz = apz = true;
            }
        } else {
            azn = azp = anz = apz = true;
        }

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop) {
            if (kTop) {
                return headedToBaseGrabWall(-this._i, -this._k, zp, pz, nz, zn, azp, apz, anz, azn);
            } else {
                return headedToBaseGrabWall(-this._i, this._k, pz, zn, zp, nz, apz, azn, azp, anz);
            }
        } else if (kTop) {
            return headedToBaseGrabWall(this._i, -this._k, nz, zp, zn, pz, anz, azp, azn, apz);
        } else {
            return headedToBaseGrabWall(this._i, this._k, zn, nz, pz, zp, azn, anz, apz, azp);
        }
    }

    private static boolean headedToBaseGrabWall(int i, int k, boolean front, boolean side, boolean frontOpposite, boolean sideOpposite, boolean aboveFront, boolean aboveSide, boolean aboveFrontOpposite, boolean aboveSideOpposite)
    {
        if (sideOpposite && !aboveSideOpposite && !front && !aboveFront && i == 1) {
            return true;
        }
        if (frontOpposite && !aboveFrontOpposite && !side && !aboveSide && k == 1) {
            return true;
        }
        if (side && !aboveSide && k >= 0) {
            return true;
        }
        if (front && !aboveFront && k >= 0) {
            return true;
        }
        if (frontOpposite && !aboveFrontOpposite && !aboveFront && i == 1 && k >= 0) {
            return true;
        }
        return sideOpposite && !aboveSideOpposite && !aboveSide && k == 1 && i >= 0;
    }

    private boolean headedToRemoteFlatWall(IBlockState state, int j_offset)
    {
        return !this.getWallFlag(this, remote_i, j_offset, remote_k, state) &&
                this.getWallFlag(this.rotate(90), remote_i, j_offset, remote_k, state) &&
                !this.getWallFlag(this.rotate(180), remote_i, j_offset, remote_k, state) &&
                this.getWallFlag(this.rotate(-90), remote_i, j_offset, remote_k, state);
    }

    private boolean getWallFlag(Orientation direction, int i, int j_offset, int k, IBlockState state)
    {
        Block block = state.getBlock();
        if (block instanceof BlockPane) {
            return ((BlockPane) block).canPaneConnectTo(world, new BlockPos(i + direction._i, j_offset, k + direction._k), this._facing);
        } else if (isFenceBase(state)) {
            if (block instanceof BlockFence) {
                return ((BlockFence) block).canConnectTo(world, new BlockPos(i + direction._i, local_offset + j_offset, k + direction._k), this._facing);
            }
            if (block instanceof BlockWall) {
                ((IBlockWall) block).publicCanConnectTo(world, new BlockPos(i + direction._i, local_offset + j_offset, k + direction._k), this._facing);
            }
        } else if (isFenceGate(state)) {
            return isClosedFenceGate(state) && this.isFenceGateFront(state);
        }
        return false;
    }

    private static boolean getAllWallsOnNoWall(IBlockState state)
    {
        return state.getBlock() instanceof BlockPane;
    }

    private static boolean isTopHalf(double d)
    {
        return (int) Math.abs(Math.floor(d * 2D)) % 2 == 1;
    }

    private static boolean isBottomHalfBlock(IBlockState state)
    {
        if (isHalfBlock(state) && isHalfBlockBottomMetaData(state)) {
            return true;
        }
        return state.getBlock() == Block.getBlockFromName("bed");
    }

    private static boolean isTopHalfBlock(IBlockState state)
    {
        return isHalfBlock(state) && isHalfBlockTopMetaData(state);
    }

    private static boolean isHalfBlockBottomMetaData(IBlockState state)
    {
        return getValue(state, BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM;
    }

    private static boolean isHalfBlockTopMetaData(IBlockState state)
    {
        return getValue(state, BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
    }

    private static boolean isHalfBlock(IBlockState state)
    {
        return isBlock(state, BlockSlab.class, _knownHalfBlocks) && !((BlockSlab) state.getBlock()).isOpaqueCube(state);
    }

    private static boolean isStairCompact(IBlockState state)
    {
        return isBlock(state, BlockStairs.class, _knownCompactStairBlocks);
    }

    private static boolean isLowerHalfEmpty(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        if (!empty && isHalfBlock(state) && isHalfBlockTopMetaData(state)) {
            empty = true;
        }

        return empty;
    }

    private boolean isLowerHalfFrontFullEmpty(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        if (!empty && isStairCompact(state) && this.isTopStairCompactFront(state)) {
            empty = true;
        }

        if (!empty && isHalfBlock(state) && isHalfBlockTopMetaData(state)) {
            empty = true;
        }

        if (!empty && isWallBlock(state) && !this.headedToFrontWall(i, j_offset, k, state)) {
            empty = true;
        }

        if (!empty && isDoor(state) && !this.rotate(180).isDoorFrontBlocked(i, j_offset, k)) {
            empty = true;
        }

        if (!empty && isTrapDoor(state) && (isClosedTrapDoor(state) || !this.rotate(180).isTrapDoorFront(state))) {
            empty = true;
        }

        return empty;
    }

    private boolean isUpperHalfFrontAnySolid(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean solid = isUpperHalfFrontFullSolid(i, j_offset, k);
        if (solid && isWallBlock(state) && !this.headedToFrontWall(i, j_offset, k, state)) {
            solid = false;
        }
        return solid;
    }

    private static boolean isUpperHalfFrontFullSolid(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);

        boolean solid = isSolid(state.getMaterial());
        if (solid && state.getBlock() == Block.getBlockFromName("standing_sign")) {
            solid = false;
        }
        if (solid && state.getBlock() == Block.getBlockFromName("wall_sign")) {
            solid = false;
        }
        if (solid && state instanceof BlockPressurePlate) {
            solid = false;
        }
        if (solid && isTrapDoor(state)) {
            solid = false;
        }
        if (solid && isOpenFenceGate(state)) {
            solid = false;
        }
        return solid;
    }

    private static boolean isFullEmpty(IBlockState state)
    {
        if (state == null) {
            return true;
        }

        Block block = state.getBlock();
        boolean empty = !isSolid(state.getMaterial());
        if (!empty && block == Block.getBlockFromName("standing_sign")) {
            empty = true;
        }
        if (!empty && block == Block.getBlockFromName("wall_sign")) {
            empty = true;
        }
        if (!empty && block instanceof BlockPressurePlate) {
            empty = true;
        }
        return empty;
    }

    private static boolean isFenceBase(IBlockState state)
    {
        return isBlock(state, BlockFence.class, _knownFenceBlocks) || isBlock(state, BlockWall.class, _knownWallBlocks);
    }

    private static boolean isFence(IBlockState state)
    {
        return getFenceId(state) != null;
    }

    private static IBlockState getFenceId(IBlockState state)
    {
        if (isFenceBase(state) || isClosedFenceGate(state)) {
            return state;
        }
        return null;
    }

    private static boolean isClosedFenceGate(IBlockState state)
    {
        return isFenceGate(state) && !getValue(state, BlockFenceGate.OPEN);
    }

    private static boolean isFenceGate(IBlockState state)
    {
        return isBlock(state, BlockFenceGate.class, _knownFanceGateBlocks);
    }

    private static boolean isOpenFenceGate(IBlockState state)
    {
        return isFenceGate(state) && !getValue(state, BlockFenceGate.OPEN);
    }

    private static boolean isOpenTrapDoor(int i, int j_offset, int k)
    {
        return isTrapDoor(i, j_offset, k) && !isClosedTrapDoor(getBlockState(i, j_offset, k));
    }

    private static boolean isClosedTrapDoor(int i, int j_offset, int k)
    {
        return isTrapDoor(i, j_offset, k) && isClosedTrapDoor(getBlockState(i, j_offset, k));
    }

    private static boolean isTrapDoor(int i, int j_offset, int k)
    {
        return isTrapDoor(getBlockState(i, j_offset, k));
    }

    public static boolean isTrapDoor(IBlockState block)
    {
        return isBlock(block, BlockTrapDoor.class, _knownTrapDoorBlocks);
    }

    private static boolean isBlock(IBlockState state, Class<?> type, Block[] baseBlocks)
    {
        if (state == null) {
            return false;
        }

        Block block = state.getBlock();
        if (type != null && baseBlocks.length > 1 && isBlockOfType(state, type)) {
            return true;
        }

        for (Block baseBlock : baseBlocks) {
            if (baseBlock != null && block == baseBlock) {
                return true;
            }
        }

        if (type != null && isBlockOfType(state, type)) {
            return true;
        }

        Class<?> blockType = block.getClass();
        for (Block baseBlock : baseBlocks) {
            if (baseBlock != null && baseBlock.getClass().isAssignableFrom(blockType)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isClosedTrapDoor(IBlockState state)
    {
        return !getValue(state, BlockTrapDoor.OPEN);
    }

    private static boolean isDoor(IBlockState state)
    {
        return isBlockOfType(state, BlockDoor.class);
    }

    private static boolean isDoorTop(IBlockState state)
    {
        return getValue(state, BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER;
    }

    private boolean isDoorFrontBlocked(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        if (isDoorTop(state)) {
            return this.isDoorFrontBlocked(i, j_offset - 1, k);
        }

        switch (this.getDoorFacing(state)) {
            case SOUTH:
                return this._k < 0;
            case WEST:
                return this._i > 0;
            case NORTH:
                return this._k > 0;
            case EAST:
                return this._i < 0;
        }

        return true;
    }

    private EnumFacing getDoorFacing(IBlockState state)
    {
        EnumFacing facing = getValue(state, BlockDoor.FACING);
        if (!getValue(state, BlockDoor.OPEN)) {
            return facing;
        }

        switch (getValue(state, BlockDoor.HINGE)) {
            case LEFT:
                switch (facing) {
                    case EAST:
                        return EnumFacing.NORTH;
                    case NORTH:
                        return EnumFacing.WEST;
                    case WEST:
                        return EnumFacing.SOUTH;
                    case SOUTH:
                        return EnumFacing.EAST;
                    default:
                        return facing;
                }
            case RIGHT:
                switch (facing) {
                    case EAST:
                        return EnumFacing.SOUTH;
                    case SOUTH:
                        return EnumFacing.WEST;
                    case WEST:
                        return EnumFacing.NORTH;
                    case NORTH:
                        return EnumFacing.EAST;
                    default:
                        return facing;
                }
            default:
                return facing;
        }
    }

    private static IBlockState getWallBlockId(int i, int j_offset, int k)
    {
        IBlockState block = getBlockState(i, j_offset, k);
        if (isWallBlock(block)) {
            return block;
        }
        return null;
    }

    private static boolean isWallBlock(IBlockState state)
    {
        return isBlock(state, BlockPane.class, _knownThinWallBlocks) || isFence(state);
    }

    private static boolean isBaseAccessible(int j_offset)
    {
        return isBaseAccessible(j_offset, false, false);
    }

    private static boolean isBaseAccessible(int j_offset, boolean bottom, boolean full)
    {
        IBlockState state = getBaseBlockState(j_offset);
        boolean accessible = isEmpty(base_i, j_offset, base_k);

        if (!accessible && isFullEmpty(state)) {
            accessible = true;
        }

        if (!accessible && isOpenTrapDoor(base_i, j_offset, base_k)) {
            accessible = true;
        }

        if (!accessible && bottom && isClosedTrapDoor(base_i, j_offset, base_k)) {
            accessible = true;
        }

        if (!accessible && !full && isWallBlock(state)) {
            accessible = true;
        }

        if (!accessible && isDoor(state)) {
            accessible = true;
        }

        return accessible;
    }

    private boolean isRemoteAccessible(int j_offset)
    {
        boolean accessible = isEmpty(remote_i, j_offset, remote_k);

        if (accessible) {
            IBlockState baseState = getBaseBlockState(j_offset);
            if (isTrapDoor(baseState)) {
                accessible = !this.isTrapDoorFront(baseState);
            }

            if (accessible && isDoor(baseState)) {
                accessible = !this.isDoorFrontBlocked(base_i, j_offset, base_k);
            }

            if (this.remoteLadderClimbing(j_offset)) {
                accessible = false;
            }
        }

        if (!accessible && isTrapDoor(remote_i, j_offset, remote_k)) {
            accessible = isClosedTrapDoor(getRemoteBlockState(j_offset));
        }

        if (!accessible) {
            IBlockState remoteState = getRemoteBlockState(j_offset);
            if (isWallBlock(remoteState) && !this.headedToFrontWall(remote_i, j_offset, remote_k, remoteState) && !isFence(getRemoteBlockState(j_offset - 1))) {
                accessible = true;
            }

            IBlockState remoteBelowState = getRemoteBlockState(j_offset - 1);
            if (!accessible && isFence(remoteBelowState) && (!this.headedToFrontWall(remote_i, j_offset - 1, remote_k, remoteBelowState) || isWallBlock(getBaseBlockState(j_offset - 1)))) {
                if (remoteBelowState.getBlock() != Block.getBlockFromName("cobblestone_wall") || this.headedToRemoteFlatWall(remoteBelowState, -1)) {
                    accessible = true;
                }
            }

            if (!accessible && isDoor(remoteState) && !this.rotate(180).isDoorFrontBlocked(remote_i, j_offset, remote_k)) {
                accessible = true;
            }
        }

        return accessible;
    }

    private boolean isAccessAccessible(int j_offset)
    {
        if (!this._isDiagonal) {
            return true;
        }

        return isEmpty(remote_i, j_offset, base_k) && isEmpty(base_i, j_offset, remote_k);
    }

    private boolean isFullExtentAccessible(int j_offset, boolean grabRemote)
    {
        return this.isFullAccessible(j_offset, grabRemote);
    }

    private boolean isJustLowerHalfExtentAccessible(int j_offset)
    {
        IBlockState remoteState = getRemoteBlockState(j_offset);

        boolean accessible = isTopHalfBlock(remoteState);
        if (!accessible) {
            accessible = isStairCompact(remoteState) && this.isTopStairCompactFront(remoteState);
        }
        return accessible;
    }

    private boolean isFullAccessible(int j_offset, boolean grabRemote)
    {
        if (grabRemote) {
            return isBaseAccessible(j_offset) && this.isRemoteAccessible(j_offset) && this.isAccessAccessible(j_offset);
        } else {
            return isEmpty(base_i, j_offset, base_k);
        }
    }

    private boolean isLowerHalfAccessible(int j_offset, boolean grabRemote)
    {
        if (grabRemote) {
            return isBaseAccessible(1, true, false) && this.rotate(180).isLowerHalfFrontFullEmpty(base_i, 1, base_k) && this.isLowerHalfFrontFullEmpty(remote_i, 1, remote_k);
        } else {
            return isLowerHalfEmpty(base_i, j_offset, base_k);
        }
    }

    private static boolean isEmpty(int i, int j_offset, int k)
    {
        return isFullEmpty(getBlockState(i, j_offset, k)) && !isFence(getBlockState(i, j_offset - 1, k));
    }

    private boolean isUpperHalfFrontEmpty(int i, int j_offset, int k)
    {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        if (!empty) {
            if (isBottomHalfBlock(state)) {
                empty = true;
            }

            if (!empty && isStairCompact(state) && this.isBottomStairCompactFront(state)) {
                empty = true;
            }
        }

        if (!empty && isTrapDoor(state)) {
            empty = true;
        }

        if (!empty) {
            IBlockState wallId = getWallBlockId(i, j_offset, k);
            if (wallId != null && (!this.headedToFrontWall(i, j_offset, k, wallId) || isWallBlock(getBlockState(i - this._i, j_offset, k - this._k)))) {
                empty = true;
            }
        }

        return empty;
    }

    private static boolean isSolid(Material material)
    {
        return material.isSolid() && material.blocksMovement();
    }

    private static IBlockState getBlockState(int i, int j_offset, int k)
    {
        return getState(world, i, local_offset + j_offset, k);
    }

    private static Block getBaseBlock(int j_offset)
    {
        return getBaseBlockState(j_offset).getBlock();
    }

    private static IBlockState getBaseBlockState(int j_offset)
    {
        return getState(world, base_i, local_offset + j_offset, base_k);
    }

    private static boolean isBlockOfType(IBlockState state, Class<?>... types)
    {
        if (types == null || state == null) {
            return false;
        }

        Class<?> blockType = state.getBlock().getClass();
        for (Class<?> type : types) {
            if (type != null && type.isAssignableFrom(blockType)) {
                return true;
            }
        }

        return false;
    }

    private static Block getRemoteBlock(int j_offset)
    {
        return getRemoteBlockState(j_offset).getBlock();
    }

    private static IBlockState getRemoteBlockState(int j_offset)
    {
        return getState(world, remote_i, local_offset + j_offset, remote_k);
    }

    private static String getBlockName(Block block)
    {
        if (block == null) {
            return null;
        }
        return block.getTranslationKey();
    }

    private void initialize(World w, int i, double id, double jhd, int k, double kd)
    {
        world = w;

        base_i = i;
        base_id = id;
        base_jhd = jhd;
        base_k = k;
        base_kd = kd;

        remote_i = i + this._i;
        remote_k = k + this._k;
    }

    private static void initializeOffset(double offset_halfs, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling)
    {
        crawl = isClimbCrawling || isCrawlClimbing || isCrawling;

        double offset_jhd = base_jhd + offset_halfs;
        int offset_jh = MathHelper.floor(offset_jhd);
        jh_offset = offset_jhd - offset_jh;

        all_j = offset_jh / 2;
        all_offset = offset_jh % 2;
    }

    private static void initializeLocal(int localOffset)
    {
        int local_halfOffset = localOffset + all_offset;
        local_half = Math.abs(local_halfOffset) % 2;
        local_offset = all_j + (local_halfOffset - local_half) / 2;
    }

    private final static float _handClimbingHoldGap = Math.min(0.25F, 0.06F * Math.max(Config._freeClimbingUpSpeedFactor.getValue(), Config._freeClimbingDownSpeedFactor.getValue()));
    private static final ClimbGap _climbGapTemp = new ClimbGap();
    private static final ClimbGap _climbGapOuterTemp = new ClimbGap();
    private static World world;
    private static double base_jhd, jh_offset;
    private static int all_j, all_offset;
    private static int base_i, base_k;
    private static double base_id, base_kd;
    private static int remote_i, remote_k;
    private static boolean crawl;
    private static int local_half;
    private static int local_offset;
    private static boolean grabRemote;
    private static int grabType;
    private static Block grabBlock;
    private static int grabMeta;

    @Override
    public String toString()
    {
        if (this == ZZ) {
            return "ZZ";
        }
        if (this == NZ) {
            return "NZ";
        }
        if (this == PZ) {
            return "PZ";
        }
        if (this == ZP) {
            return "ZP";
        }
        if (this == ZN) {
            return "ZN";
        }
        if (this == PN) {
            return "PN";
        }
        if (this == PP) {
            return "PP";
        }
        if (this == NN) {
            return "NN";
        }
        if (this == NP) {
            return "NP";
        }
        return "UNKNOWN(" + this._i + "," + this._k + ")";
    }

    private static final Block[] _knownFanceGateBlocks;
    private static final Block[] _knownFenceBlocks;
    private static final Block[] _knownWallBlocks;
    private static final Block[] _knownHalfBlocks;
    private static final Block[] _knownCompactStairBlocks;
    private static final Block[] _knownTrapDoorBlocks;
    private static final Block[] _knownThinWallBlocks;

    static {
        _knownFanceGateBlocks = new Block[]{Block.getBlockFromName("fence_gate")};
        _knownFenceBlocks = new Block[]{Block.getBlockFromName("fence"), Block.getBlockFromName("nether_brick_fence")};
        _knownWallBlocks = new Block[]{Block.getBlockFromName("cobblestone_wall")};
        _knownHalfBlocks = new Block[]{Block.getBlockFromName("stone_slab"), Block.getBlockFromName("double_stone_slab"), Block.getBlockFromName("wooden_slab"), Block.getBlockFromName("double_wooden_slab")};
        _knownCompactStairBlocks = new Block[]{Block.getBlockFromName("stone_stairs"), Block.getBlockFromName("oak_stairs"), Block.getBlockFromName("dark_oak_stairs"), Block.getBlockFromName("brick_stairs"), Block.getBlockFromName("nether_brick_stairs"), Block.getBlockFromName("sandstone_stairs"), Block.getBlockFromName("stone_brick_stairs"), Block.getBlockFromName("birch_stairs"), Block.getBlockFromName("jungle_stairs"), Block.getBlockFromName("spruce_stairs"), Block.getBlockFromName("quartz_stairs"), Block.getBlockFromName("acacia_stairs")};
        _knownTrapDoorBlocks = new Block[]{Block.getBlockFromName("trapdoor")};
        _knownThinWallBlocks = new Block[]{Block.getBlockFromName("iron_bars"), Block.getBlockFromName("glass_pane")};
    }
}