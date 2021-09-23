package net.smart.moving;

import net.minecraftforge.common.config.Config;

public class SmartMovingConfig
{
    public static final Climb CLIMB = new Climb();
    public static final Swimming SWIMMING = new Swimming();
    public static final Diving DIVING = new Diving();
    public static final Lava LAVA = new Lava();
    public static final StandardSprinting STANDARD_SPRINTING = new StandardSprinting();
    public static final GenericSprinting GENERIC_SPRINTING = new GenericSprinting();
    public static final GenericSneaking GENERIC_SNEAKING = new GenericSneaking();
    public static final Crawling CRAWLING = new Crawling();
    public static final Sliding SLIDING = new Sliding();
    public static final SmartFlying SMART_FLYING = new SmartFlying();
    public static final StandardFlying STANDARD_FLYING = new StandardFlying();
    public static final Falling FALLING = new Falling();
    public static final Jumping JUMPING = new Jumping();
    public static final ChargedJumping CHARGED_JUMPING = new ChargedJumping();
    public static final HeadJumping HEAD_JUMPING = new HeadJumping();
    public static final SideAndBackJumping SIDE_AND_BACK_JUMPING = new SideAndBackJumping();
    public static final ClimbJumping CLIMB_JUMPING = new ClimbJumping();
    public static final ClimbBackJumping CLIMB_BACK_JUMPING = new ClimbBackJumping();
    public static final ClimbBackHeadJumping CLIMB_BACK_HEAD_JUMPING = new ClimbBackHeadJumping();
    public static final WallJumping WALL_JUMPING = new WallJumping();
    public static final WallHeadJumping WALL_HEAD_JUMPING = new WallHeadJumping();
    public static final JumpExhaustion JUMP_EXHAUSTION = new JumpExhaustion();
    public static final Exhaustion EXHAUSTION = new Exhaustion();
    public static final Hunger HUNGER = new Hunger();
    public static final ItemUsage ITEM_USAGE = new ItemUsage();
    public static final ViewpointPerspective VIEWPOINT_PERSPECTIVE = new ViewpointPerspective();
    public static final UserInterface USER_INTERFACE = new UserInterface();

    public static class Climb
    {
        @Config.Comment("To manipulate the ladder and vine climbing mode (possible values are \"free\", \"smart\", \"simple\" and \"standard\")")
        public String base = "free";
        @Config.Comment("To switch on/off free climbing")
        public boolean enable = true;
        @Config.Comment("To switch on/off remaining base climbing behavior on ladders while free climbing is enabled for ladders (also see \"climb.base\")")
        public boolean baseLadder = false;
        @Config.Comment("To switch on/off remaining base climbing behavior on vines while free climbing is enabled for vines (also see \"climb.base\")")
        public boolean baseVine = false;
        @Config.Comment("Climbing up speed factor relative to default climbing up speed (>= 0)")
        public float freeUpSpeedFactor = 1.0F;
        @Config.Comment("Climbing down speed factor relative to default climbing down speed (>= 0)")
        public float freeDownSpeedFactor = 1.0F;
        @Config.Comment("Climbing horizontal speed factor relative to default climbing horizontal speed (>= 0)")
        public float freeHorizontalSpeedFactor = 1.0F;
        @Config.Comment("Climbing N,S,E,W grabbing angle in degrees")
        public int freeDirectionOrthogonalAngle = 90;
        @Config.Comment("Climbing NW,SW,SE,NE grabbing angle in degrees")
        public int freeDirectionDiagonalAngle = 80;
        @Config.Comment("Whether the \"grab\" button will automatically be triggered while being on ladders and looking in the right direction")
        public boolean freeLadderAuto = true;
        @Config.Comment("Whether the \"grab\" button will automatically be triggered while being on standard climbable vines and looking in the right direction")
        public boolean freeVineAuto = true;
        @Config.Comment("Additional speed factor when climbing straight up on one ladder block (>= 0)")
        public float freeLadderOneUpSpeedFactor = 1.0153F;
        @Config.Comment("Additional speed factor when climbing straight up on two ladder blocks (>= 1)")
        public float freeLadderTwoUpSpeedFactor = 1.43F;
        @Config.Comment("Climbing over fences")
        public boolean freeFence = true;
        @Config.Comment("Distance in blocks to fall before suffering fall damage when starting to climb (>= 1, <= 3)")
        public int fallDamageStartDistance = 2;
        @Config.Comment("Damage factor applied to the remaining distance (>= 1)")
        public float fallDamageFactor = 2.0F;
        @Config.Comment("Distance in blocks to fall to block all climbing attempts (>= \"climb.fall.damage.start.distance\")")
        public int fallMaximumDistance = 3;
        @Config.Comment("To switch on/off exhaustion while climbing")
        public boolean exhaustion = false;
        @Config.Comment("Maximum exhaustion to start climbing along ceilings (>= 0)")
        public int exhaustionStart = 60;
        @Config.Comment("Maximum exhaustion to climb (>= 0)")
        public int exhaustionStop = 80;
        @Config.Comment("Exhaustion added every tick while climbing horizontally (>= 0)")
        public float strafeExhaustionGain = 1.1F;
        @Config.Comment("Exhaustion added every tick while climbing up (>= 0)")
        public float upExhaustionGain = 1.2F;
        @Config.Comment("Exhaustion added every tick while climbing down (>= 0)")
        public float downExhaustionGain = 1.05F;
        @Config.Comment("Exhaustion added every tick while climbing diagonally up (>= 0)")
        public float strafeUpExhaustionGain = 1.3F;
        @Config.Comment("Exhaustion added every tick while climbing diagonally down (>= 0)")
        public float strafeDownExhaustionGain = 1.25F;
        @Config.Comment("To switch on/off climbing along ceilings")
        public boolean ceiling = true;
        @Config.Comment("Speed factor while climbing along ceilings (>= 0, relative to default movement speed)")
        public float ceilingSpeedFactor = 0.2F;
        @Config.Comment("To switch on/off exhaustion while climbing along ceilings")
        public boolean ceilingExhaustion = false;
        @Config.Comment("Maximum exhaustion to start climbing along ceilings (>= 0)")
        public int ceilingExhaustionStart = 40;
        @Config.Comment("Maximum exhaustion to climbing along ceilings (>= \"climb.ceiling.exhaustion.start\")")
        public int ceilingExhaustionStop = 60;
        @Config.Comment("Exhaustion added every tick while climbing along ceilings (>= 0)")
        public float ceilingExhaustionGain = 1.3F;
    }

    public static class Swimming
    {
        @Config.Comment("To switch on/off swimming")
        public boolean enable = true;
        @Config.Comment("Speed factor while swimming (>= 0, relative to default movement speed)")
        public float speedFactor = 1.0F;
        @Config.Comment("To switch on/off diving down instead of swimming slow on sneaking while swimming")
        public boolean downSneak = true;
        @Config.Comment("Swim particle spawning period factor (>= 0)")
        public float particlePeriodFactor = 1.0F;
    }

    public static class Diving
    {
        @Config.Comment("To switch on/off diving")
        public boolean enable = true;
        @Config.Comment("Speed factor while diving (>= 0, relative to default movement speed)")
        public float speedFactor = 1.0F;
        @Config.Comment("To switch on/off diving down instead of diving slow on sneaking while diving")
        public boolean downSneak = true;
    }

    public static class Lava
    {
        @Config.Comment("To switch on/off swimming and diving in lava")
        public boolean likeWater = false;
        @Config.Comment("Lava swim particle spawning period factor (>= 0)")
        public float particlePeriodFactor = 4.0F;
    }

    public static class StandardSprinting
    {
        @Config.Comment("To switch on/off standard sprinting")
        public boolean enable = true;
        @Config.Comment("Standard sprinting factor (>= 1.1)")
        public float factor = 1.1F;
        @Config.Comment("To switch on/off standard sprinting exhaustion")
        public boolean exhaustion = false;
        @Config.Comment("Maximum exhaustion to start a standard sprint (>= 0)")
        public int exhaustionStart = 75;
        @Config.Comment("Maximum exhaustion to continue a standard sprint (>= \"exhaustion.run.start\")")
        public int exhaustionStop = 100;
        @Config.Comment("Exhaustion gain factor while standard sprinting (>= 0)")
        public float exhaustionGainFactor = 1.5F;
    }

    public static class GenericSprinting
    {
        @Config.Comment("To switch on/off generic sprinting")
        public boolean enable = true;
        @Config.Comment("Generic sprinting factor (>= 1.1 AND >= 'run.factor' + 0.1 if relevant)")
        public float factor = 1.3F;
        @Config.Comment("To switch on/off sprinting exhaustion")
        public boolean exhaustion = true;
        @Config.Comment("Maximum exhaustion to start a sprint (>= 0)")
        public int exhaustionStart = 50;
        @Config.Comment("Maximum exhaustion to continue a sprint (>= \"exhaustion.sprint.start\")")
        public int exhaustionStop = 100;
        @Config.Comment("Exhaustion gain factor while sprinting (>= 0)")
        public float exhaustionGainFactor = 2.0F;
    }

    public static class GenericSneaking
    {
        @Config.Comment("To switch on/off standard sneaking")
        public boolean enable = true;
        @Config.Comment("Speed factor while sneaking (>= 0, <= 1, relative to default movement speed)")
        public float factor = 0.3F;
        @Config.Comment("Whether to display a name tag above other standard sneaking players")
        public boolean name = false;
    }

    public static class Crawling
    {
        @Config.Comment("To switch on/off crawling")
        public boolean enable = true;
        @Config.Comment("Speed factor while crawling (>= 0, <= 1, relative to default movement speed)")
        public float factor = 0.15F;
        @Config.Comment("Whether to display a name tag above other crawling players")
        public boolean name = false;
        @Config.Comment("Whether to allow crawling over edges")
        public boolean edge = true;
    }

    public static class Sliding
    {
        @Config.Comment("To switch on/off sliding")
        public boolean enable = true;
        @Config.Comment("Sliding control movement factor (>= 0, in degrees per tick)")
        public int controlAngle = 1;
        @Config.Comment("Slipperiness factor while sliding (>= 0)")
        public float slipperinessFactor = 1.0F;
        @Config.Comment("Sliding to crawling transition speed factor (>= 0)")
        public float speedStopFactor = 1.0F;
        @Config.Comment("Sliding particle spawning period factor (>= 0)")
        public float particlePeriodFactor = 0.5F;
    }

    public static class SmartFlying
    {
        @Config.Comment("To switch on/off smart flying")
        public boolean enable = true;
        @Config.Comment("To manipulate smart flying speed (>= 0)")
        public float speedFactor = 1.0F;
    }

    public static class StandardFlying
    {
        @Config.Comment("To switch on/off standard flying small size")
        public boolean small = true;
        @Config.Comment("To switch on/off standard flying animation")
        public boolean animation = true;
    }

    public static class Falling
    {
        @Config.Comment("Minimum fall distance for stopping ground based moves like crawling or sliding (>= 0)")
        public int distanceMinimum = 3;
        @Config.Comment("To switch on/off smart falling animation")
        public boolean animation = true;
        @Config.Comment("Minimum fall distance for the smart falling animation (>= 0, >= \"fall.animation.distance.minimum\")")
        public int animationDistanceMinimum = 3;
    }

    public static class Jumping
    {
        @Config.Comment("To switch on/off jumping")
        public boolean enable = true;
        @Config.Comment("Jumping control movement factor (>= 0, <= 1, relative to default air movement speed)")
        public float controlFactor = 1.0F;
        @Config.Comment("Horizontal jumping factor relative to actual horizontal movement (>= 1)")
        public float horizontalFactor = 1.0F;
        @Config.Comment("Vertical jumping factor relative to default jump height (>= 0)")
        public float verticalFactor = 1.0F;
        @Config.Comment("To switch on/off jumping while standing (Relevant only if \"jump.enable\" is true)")
        public boolean stand = true;
        @Config.Comment("Vertical stand jumping factor relative to default jump height (>= 0)")
        public float standVerticalFactor = 1.0F;
        @Config.Comment("To switch on/off jumping while sneaking (Relevant only if nether \"sneak.enable\" nor \"jump.enable\" are false)")
        public boolean sneak = true;
        @Config.Comment("Horizontal sneak jumping factor relative to actual horizontal movement (>= 1)")
        public float sneakHorizontalFactor = 1.0F;
        @Config.Comment("Vertical sneak jumping factor relative to default jump height (>= 0)")
        public float sneakVerticalFactor = 1.0F;
        @Config.Comment("To switch on/off jumping while walking (Relevant only if \"jump.enable\" is true)")
        public boolean walk = true;
        @Config.Comment("Horizontal walk jumping factor relative to actual horizontal movement (>= 1)")
        public float walkHorizontalFactor = 1.0F;
        @Config.Comment("Vertical walk jumping factor relative to default jump height (>= 0)")
        public float walkVerticalFactor = 1.0F;
        @Config.Comment("To switch on/off jumping while running (Relevant only if nether \"run.enable\" nor \"jump.enable\" are false)")
        public boolean run = true;
        @Config.Comment("Horizontal run jumping factor relative to actual horizontal movement (>= 1)")
        public float runHorizontalFactor = 2.0F;
        @Config.Comment("Vertical run jumping factor relative to default jump height (>= 0)")
        public float runVerticalFactor = 1.0F;
        @Config.Comment("To switch on/off jumping while sprinting (Relevant only if nether \"sprint.enable\" nor \"jump.enable\" are false)")
        public boolean sprint = true;
        @Config.Comment("Horizontal sprint jumping factor relative to actual horizontal movement (>= 1)")
        public float sprintHorizontalFactor = 2.0F;
        @Config.Comment("Vertical sprint jumping factor relative to default jump height (>= 0)")
        public float sprintVerticalFactor = 1.0F;
    }

    public static class ChargedJumping
    {
        @Config.Comment("Relevant only if \"jump.enable\" is not false")
        public boolean enable = true;
        @Config.Comment("Maximum jump charge (counts up one per tick) (>= 0)")
        public int maximum = 20;
        @Config.Comment("Jump speed factor when completely charged (>= 1)")
        public float factor = 1.3F;
        @Config.Comment("To switch between charged jump and charge cancel on sneak button release while jump charging")
        public boolean sneakReleaseCancel = false;
    }

    public static class HeadJumping
    {
        @Config.Comment("Relevant only if \"move.jump\" is not false")
        public boolean enable = true;
        @Config.Comment("Head jump control movement factor (>= 0, <= 1, relative to default air movement speed)")
        public float controlFactor = 0.2F;
        @Config.Comment("Maximum head jump charge (counts up one per tick) (>= 0)")
        public int maximum = 10;
        @Config.Comment("Distance in blocks to fall head ahead before suffering fall damage (>= 1, <= 3)")
        public int damageStartDistance = 2;
        @Config.Comment("Damage factor applied to the remaining distance when impacting head ahead (>= 1)")
        public float damageFactor = 2.0F;
    }

    public static class SideAndBackJumping
    {
        @Config.Comment("To switch on/off side jumping")
        public boolean side = true;
        @Config.Comment("To switch on/off back jumping")
        public boolean back = true;
        @Config.Comment("Horizontal jump speed factor for side and back jumps (>= 0)")
        public float horizontalFactor = 0.3F;
        @Config.Comment("Vertical jump speed factor for side and back jumps (>= 0)")
        public float verticalFactor = 0.2F;
    }

    public static class ClimbJumping
    {
        @Config.Comment("To switch on/off jumping up while climbing")
        public boolean enable = true;
        @Config.Comment("Vertical jump speed factor for jumping while climbing (>= 0, <= 1)")
        public float verticalFactor = 1.0F;
        @Config.Comment("Additional vertical jump speed factor for jumping while climbing with hands only (>= 0, <= 1)")
        public float handsOnlyVerticalFactor = 0.8F;
    }

    public static class ClimbBackJumping
    {
        @Config.Comment("To switch on/off jumping back while climbing")
        public boolean enable = true;
        @Config.Comment("Vertical jump speed factor for jumping back while climbing (>= 0, <= 1)")
        public float verticalFactor = 0.2F;
        @Config.Comment("Horizontal jump speed factor for jumping back while climbing (>= 0, <= 1)")
        public float horizontalFactor = 0.3F;
        @Config.Comment("Additional vertical jump speed factor for jumping back while climbing with hands only (>= 0, <= 1)")
        public float handsOnlyVerticalFactor = 0.8F;
        @Config.Comment("Additional horizontal jump speed factor for jumping back while climbing with hands only (>= 0, <= 1)")
        public float handsOnlyHorizontalFactor = 1.0F;
    }

    public static class ClimbBackHeadJumping
    {
        @Config.Comment("To switch on/off head jumping back while climbing")
        public boolean enable = true;
        @Config.Comment("Additional vertical jump speed factor for head jumping back while climbing(>= 0, <= 1)")
        public float verticalFactor = 0.2F;
        @Config.Comment("Additional horizontal jump speed factor for head jumping back while climbing(>= 0, <= 1)")
        public float horizontalFactor = 0.3F;
        @Config.Comment("Additional vertical jump speed factor for head jumping while climbing with hands only (>= 0, <= 1)")
        public float handsOnlyVerticalFactor = 0.8F;
        @Config.Comment("Additional horizontal jump speed factor for head jumping while climbing with hands only (>= 0, <= 1)")
        public float handsOnlyHorizontalFactor = 1.0F;
    }

    public static class WallJumping
    {
        @Config.Comment("To switch on/off wall jumping")
        public boolean enable = true;
        @Config.Comment("Vertical jump speed factor for wall jumping (>= 0, <= 1)")
        public float verticalFactor = 0.4F;
        @Config.Comment("Horizontal jump speed factor for wall jumping (>= 0, <= 1)")
        public float horizontalFactor = 0.15F;
        @Config.Comment("Distance in blocks to fall to block all wall jumping attempts")
        public int fallMaximumDistance = 2;
        @Config.Comment("Tolerance angle in degree for wall jumping orthogonally (>= 0, <= 45)")
        public int orthogonalTolerance = 5;
    }

    public static class WallHeadJumping
    {
        @Config.Comment("To switch on/off wall head jumping")
        public boolean enable = true;
        @Config.Comment("Vertical jump speed factor for wall head jumping (>= 0, <= 1)")
        public float verticalFactor = 0.3F;
        @Config.Comment("Horizontal jump speed factor for wall head jumping (>= 0, <= 1)")
        public float horizontalFactor = 0.15F;
        @Config.Comment("Distance in blocks to fall to block all wall head jumping attempts (>= \"jump.wall.fall.maximum.distance\")")
        public int fallMaximumDistance = 3;
    }

    public static class JumpExhaustion
    {
        @Config.Comment("To switch on/off jump exhaustion")
        public boolean enable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump (>= 0)")
        public float gainFactor = 1.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump (>= 0)")
        public float stopFactor = 1.0F;
        @Config.Comment("To switch on/off up jump exhaustion")
        public boolean upEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a jump up (>= 0)")
        public float upGainFactor = 1.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump up (>= 0)")
        public float upStopFactor = 1.0F;
        @Config.Comment("To switch on/off climb jump exhaustion")
        public boolean climbEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by jumping while climbing (>= 0)")
        public float climbGainFactor = 1.0F;
        @Config.Comment("To manipulate maximum exhaustion to jumping while climbing (>= 0)")
        public float climbStopFactor = 1.0F;
        @Config.Comment("To switch on/off climb up jump exhaustion")
        public boolean climbUpEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump up while climbing (>= 0)")
        public float climbUpGainFactor = 40.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump up while climbing (>= 0)")
        public float climbUpStopFactor = 60.0F;
        @Config.Comment("To switch on/off climb back jump exhaustion")
        public boolean climbBackEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump back while climbing (>= 0)")
        public float climbBackGainFactor = 40.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump back while climbing (>= 0)")
        public float climbBackStopFactor = 60.0F;
        @Config.Comment("To switch on/off back climb head jump exhaustion")
        public boolean climbBackHeadEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a head jump back while climbing (>= 0)")
        public float climbBackHeadGainFactor = 20.0F;
        @Config.Comment("To manipulate maximum exhaustion to head jump back while climbing (>= 0)")
        public float climbBackHeadStopFactor = 80.0F;
        @Config.Comment("To switch on/off angle jump exhaustion")
        public boolean angleEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a jump to the side or back (>= 0)")
        public float angleGainFactor = 1.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump to the side or back (>= 0)")
        public float angleStopFactor = 1.0F;
        @Config.Comment("To switch on/off wall jump exhaustion")
        public boolean wallEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a wall jump (>= 0)")
        public float wallGainFactor = 1.0F;
        @Config.Comment("To manipulate maximum exhaustion to wall jump (>= 0)")
        public float wallStopFactor = 1.0F;
        @Config.Comment("To switch on/off wall up jump exhaustion")
        public boolean wallUpEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a wall up jump (>= 0)")
        public float wallUpGainFactor = 40.0F;
        @Config.Comment("To manipulate maximum exhaustion to wall up jump (>= 0)")
        public float wallUpStopFactor = 60.0F;
        @Config.Comment("To switch on/off wall head jump exhaustion")
        public boolean wallHeadEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a wall head jump (>= 0)")
        public float wallHeadGainFactor = 20.0F;
        @Config.Comment("To manipulate maximum exhaustion to wall head jump (>= 0)")
        public float wallHeadStopFactor = 80.0F;
        @Config.Comment("To switch on/off stand jump exhaustion")
        public boolean standEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump while standing (>= 0)")
        public float standGainFactor = 40.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump while standing (>= 0)")
        public float standStopFactor = 60.0F;
        @Config.Comment("To switch on/off sneak jump exhaustion")
        public boolean sneakEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump while sneaking (>= \"jump.stand.exhaustion.gain.factor\")")
        public float sneakGainFactor = 40.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump while sneaking (>= 0, <= \"jump.stand.exhaustion.stop.factor\")")
        public float sneakStopFactor = 60.0F;
        @Config.Comment("To switch on/off walk jump exhaustion")
        public boolean walkEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump while walking (>= \"jump.sneak.exhaustion.gain.factor\")")
        public float walkGainFactor = 45.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump while walking (>= 0, <= \"jump.sneak.exhaustion.stop.factor\")")
        public float walkStopFactor = 55.0F;
        @Config.Comment("To switch on/off run jump exhaustion")
        public boolean runEnable = false;
        @Config.Comment("To manipulate the exhaustion increase by a jump while running (>= \"jump.walk.exhaustion.gain.factor\")")
        public float runGainFactor = 60.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump while running (>= 0, <= \"jump.walk.exhaustion.stop.factor\")")
        public float runStopFactor = 40.0F;
        @Config.Comment("To switch on/off sprint jump exhaustion")
        public boolean sprintEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a jump while sprinting (>= \"jump.run.exhaustion.gain.factor\")")
        public float sprintGainFactor = 65.0F;
        @Config.Comment("To manipulate maximum exhaustion to jump while sprinting (>= 0, <= \"jump.run.exhaustion.stop.factor\")")
        public float sprintStopFactor = 35.0F;
        @Config.Comment("To switch on/off up additional jump charge exhaustion")
        public boolean chargeEnable = true;
        @Config.Comment("To manipulate the additional exhaustion for the higher jump (>= 0, is multiplied with the actual charge factor)")
        public float chargeGainFactor = 30.0F;
        @Config.Comment("To manipulate the subtracted maximum exhaustion to jump higher (>= 0, is multiplied with the actual charge factor)")
        public float chargeStopFactor = 30.0F;
        @Config.Comment("To switch on/off slide jump exhaustion")
        public boolean slideEnable = true;
        @Config.Comment("To manipulate the exhaustion increase by a slide jump (>= 0)")
        public float slideGainFactor = 10.0F;
        @Config.Comment("To manipulate maximum exhaustion to slide jump (>= 0)")
        public float slideStopFactor = 90.0F;
    }

    public static class Exhaustion
    {
        @Config.Comment("Exhaustion gain base factor, set to '0' to disable exhaustion (>= 0)")
        public float gainFactor = 1.0F;
        @Config.Comment("Exhaustion loss base factor (>= 0)")
        public float lossFactor = 1.0F;
        @Config.Comment("Smart sprinting exhaustion loss factor (>= 0)")
        public float sprintLossFactor = 0.0F;
        @Config.Comment("Standard sprinting exhaustion loss factor while (>= 0, >= \"exhaustion.sprint.loss.factor\")")
        public float runLossFactor = 0.5F;
        @Config.Comment("Walking exhaustion loss factor (>= 0, >= \"exhaustion.run.loss.factor\")")
        public float walkLossFactor = 1.0F;
        @Config.Comment("Sneaking exhaustion loss factor (>= 0, >= \"exhaustion.walk.loss.factor\")")
        public float sneakLossFactor = 1.5F;
        @Config.Comment("Standing exhaustion loss factor (>= 1, >= \"exhaustion.sneak.loss.factor\")")
        public float standLossFactor = 2.0F;
        @Config.Comment("Falling exhaustion loss factor (>= \"exhaustion.stand.loss.factor\")")
        public float fallLossFactor = 2.5F;
        @Config.Comment("Ceiling climbing exhaustion loss factor (>= 0)")
        public float climbCeilingLossFactor = 1.0F;
        @Config.Comment("Climbing exhaustion loss factor (>= 0)")
        public float climbLossFactor = 1.0F;
        @Config.Comment("Crawling exhaustion loss factor (>= 0)")
        public float crawlLossFactor = 1.0F;
        @Config.Comment("Water walking exhaustion loss factor (>= 0)")
        public float dipLossFactor = 1.0F;
        @Config.Comment("Swimming exhaustion loss factor (>= 0)")
        public float swimLossFactor = 1.0F;
        @Config.Comment("Diving exhaustion loss factor (>= 0)")
        public float diveLossFactor = 1.0F;
        @Config.Comment("Normal movement exhaustion loss factor (>= 0)")
        public float normalLossFactor = 1.0F;
        @Config.Comment("Whether exhaustion loss increases hunger")
        public boolean hunger = true;
        @Config.Comment("How much hunger is generated for exhaustion loss (>= 0)")
        public float hungerFactor = 0.05F;
        @Config.Comment("Until which food level exhaustion is continuously reduced")
        public int foodMinimum = 4;
    }

    public static class Hunger
    {
        @Config.Comment("To switch on/off hunger generation")
        public boolean enable = true;
        @Config.Comment("Hunger generation base factor (>= 0)")
        public float gainFactor = 1.0F;
        @Config.Comment("Smart sprinting hunger generation factor (>= 0)")
        public float sprintGainFactor = 1.0F;
        @Config.Comment("Standard sprinting hunger generation factor (>= 0)")
        public float runGainFactor = 10.0F;
        @Config.Comment("Standard speed movement hunger generation factor (>= 0)")
        public float walkGainFactor = 1.0F;
        @Config.Comment("Sneaking hunger generation factor (>= 0)")
        public float sneakGainFactor = 1.0F;
        @Config.Comment("Sneaking hunger generation factor (>= 0)")
        public float standGainFactor = 0.0F;
        @Config.Comment("Climbing hunger generation factor (>= 0)")
        public float climbGainFactor = 1.0F;
        @Config.Comment("Crawling hunger generation factor (>= 0)")
        public float crawlGainFactor = 1.0F;
        @Config.Comment("Ceiling climbing hunger generation factor (>= 0)")
        public float climbCeilingGainFactor = 1.0F;
        @Config.Comment("Swimming hunger generation factor (>= 0)")
        public float swimGainFactor = 1.5F;
        @Config.Comment("Diving hunger generation factor (>= 0)")
        public float diveGainFactor = 1.5F;
        @Config.Comment("Water walking hunger generation factor (>= 0)")
        public float dipGainFactor = 1.5F;
        @Config.Comment("Normal movement hunger generation factor (>= 0)")
        public float normalGainFactor = 1.0F;
        @Config.Comment("Basic hunger per tick (>= 0)")
        public float perTickGainFactor = 0.0F;
    }

    public static class ItemUsage
    {
        @Config.Comment("Speed factor while using an item, if not defined otherwise (>= 0 AND <= 1)")
        public float speedFactor = 0.2F;
        @Config.Comment("Speed factor while blocking with a sword (>= 0 AND <= 1, defaults to \"usage.speed.factor\" when not present)")
        public float swordSpeedFactor = 0.2F;
        @Config.Comment("Speed factor while pulling back a bow (>= 0 AND <= 1, defaults to \"usage.speed.factor\" when not present)")
        public float bowSpeedFactor = 0.2F;
        @Config.Comment("Speed factor while eating food (>= 0 AND <= 1, defaults to \"usage.speed.factor\" when not present)")
        public float foodSpeedFactor = 0.2F;
        @Config.Comment("To switch on/off generic sprinting while using an item")
        public boolean sprint = false;
    }

    public static class ViewpointPerspective
    {
        @Config.Comment("Fading speed factor between the different perspectives (>= 0.1, <= 1, set to '1' to switch off)")
        public float fadeFactor = 0.5F;
        @Config.Comment("Standard sprinting perspective (set to '0' to switch off)")
        public float runFactor = 1.0F;
        @Config.Comment("Smart on ground sprinting perspective (set to '0' to switch off)")
        public float sprintFactor = 1.5F;
    }

    public static class UserInterface
    {
        @Config.Comment("The maximum number of ticks between two clicks to trigger a side or back jump (>= 2)")
        public int jumpAngleDoubleClickTicks = 3;
        @Config.Comment("Whether wall jumping should be triggered by single or double clicking (and then press and holding) the jump button")
        public boolean jumpWallDoubleClick = true;
        @Config.Comment("The maximum number of ticks between two clicks to trigger a wall jump (>= 2, depends on \"jump.wall.double.click\")")
        public int jumpWallDoubleClickTicks = 3;
        @Config.Comment("Whether pressing or not pressing the grab button while climb jumping back results in a head jump")
        public boolean jumpClimbBackHeadOnGrab = true;
        @Config.Comment("Whether to display the exhaustion bar in the game overlay")
        public boolean guiExhaustionBar = true;
        @Config.Comment("Whether to display the jump charge bar in the game overlay")
        public boolean guiJumpChargeBar = true;
        @Config.Comment("To switch on/off sneak toggling")
        public boolean sneakToggle = false;
        @Config.Comment("To switch on/off crawl toggling")
        public boolean crawlToggle = false;
        @Config.Comment("To switch on/off flying close to the ground")
        public boolean flyGroundClose = false;
        @Config.Comment("To switch on/off flying while colliding with the ground (Relevant only if \"fly.ground.close\" is true)")
        public boolean flyGroundCollide = false;
        @Config.Comment("Whether flying control also depends on where the player looks vertically")
        public boolean flyControlVertical = true;
        @Config.Comment("Whether diving control also depends on where the player looks vertically")
        public boolean diveControlVertical = true;
    }
}