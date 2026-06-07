package sus.keiger.plugincommon;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public final class PCMath
{
    // Fields.
    public static final int TICKS_IN_SECOND = 20;
    public static final double HEALTH_IN_HEART = 2d;


    // Static functions.
    public static Vector RotationToVectorDeg(float yaw, float pitch)
    {
        return RotationToVectorRad((float)Math.toRadians(yaw), (float)Math.toRadians(pitch));
    }

    public static Vector RotationToVectorRad(float yaw, float pitch)
    {
        Vector RotatedNormal = new Vector(0d, 0d, 1d);
        RotatedNormal.rotateAroundY(-yaw);
        RotatedNormal.rotateAroundAxis(new Vector(RotatedNormal.getZ(), 0d, -RotatedNormal.getX()), pitch);
        return RotatedNormal;
    }

    public static int SecondsToTicks(double seconds)
    {
        return (int)Math.ceil(TICKS_IN_SECOND * seconds);
    }

    public static double TicksToSeconds(int ticks)
    {
        return (double)ticks / (double)TICKS_IN_SECOND;
    }

    public static double HealthToHearts(double health)
    {
        return health / HEALTH_IN_HEART;
    }

    public static double HeartsToHealth(double hearts)
    {
        return hearts * HEALTH_IN_HEART;
    }

    public static boolean AreNumbersEqual(double a, double b, double marginOfError)
    {
        return Math.abs(a - b) <= marginOfError;
    }

    public static boolean AreNumbersEqual(float a, float b, float marginOfError)
    {
        return Math.abs(a - b) <= marginOfError;
    }

    public static boolean AreBoundsColliding(BoundingBox a, BoundingBox b)
    {
        return (a.getMaxX() >= b.getMinX())
                && (a.getMinX() <= b.getMaxX())
                && (a.getMaxY() >= b.getMinY())
                && (a.getMinY() <= b.getMaxY())
                && (a.getMaxZ() >= b.getMinZ())
                && (a.getMinZ() <= b.getMaxZ());
    }

    public static boolean AreBoundsColliding(BoundingBox a, BoundingBox b, double marginOfError)
    {
        return (a.getMaxX() + marginOfError >= b.getMinX() - marginOfError)
                && (a.getMinX() - marginOfError <= b.getMaxX() + marginOfError)
                && (a.getMaxY() + marginOfError >= b.getMinY() - marginOfError)
                && (a.getMinY() - marginOfError <= b.getMaxY() + marginOfError)
                && (a.getMaxZ() + marginOfError >= b.getMinZ() - marginOfError)
                && (a.getMinZ() - marginOfError <= b.getMaxZ() + marginOfError);
    }

    public static int Sign(int value)
    {
        if (value > 0)
        {
            return 1;
        }
        else if (value < 0)
        {
            return - 1;
        }
        return 0;
    }

    public static int Sign(double value)
    {
        if (value > 0)
        {
            return 1;
        }
        else if (value < 0)
        {
            return - 1;
        }
        return 0;
    }

    public static Vector VectorToFacingDirection(Vector vector, Vector fallback)
    {
        Vector WorkingVector = new Vector(vector.getX(), 0d, vector.getZ());
        if (WorkingVector.lengthSquared() == 0.0d)
        {
            return fallback;
        }

        Vector Unit = new Vector(1d, 0d, 0d);
        double Angle = Math.atan2(WorkingVector.getZ(), WorkingVector.getX());
        final double MULTIPLIER = Math.PI / 2d;
        double ClampedAngle = Math.round(Angle / MULTIPLIER) * MULTIPLIER;
        return Unit.rotateAroundY(ClampedAngle);
    }

    public static double GetDirectionYaw(Vector vector)
    {
        return -Math.atan2(vector.getX(), vector.getZ());
    }

    public static double GetDirectionPitch(Vector vector)
    {
        double HorizontalLength = Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
        return Math.atan2(vector.getY(), HorizontalLength);
    }

    public static DirectionAxis GetDirectionAxis(Vector direction)
    {
        Objects.requireNonNull(direction, "direction is null");

        Vector ForwardAxis = direction.lengthSquared() <= 0.00000001d
                ? new Vector(0, 0, 1)
                : direction.clone().normalize();
        Vector UpReference = Math.abs(ForwardAxis.getY()) > 0.99d
                ? new Vector(1, 0, 0)
                : new Vector(0, 1, 0);
        Vector LeftAxis = UpReference.clone().crossProduct(ForwardAxis).normalize();
        Vector UpAxis = ForwardAxis.clone().crossProduct(LeftAxis).normalize();

        return new DirectionAxis(ForwardAxis, UpAxis, LeftAxis);
    }

    public static boolean AreBoundsOnGround(BoundingBox bounds, World world)
    {
        int MinX = (int)Math.floor(bounds.getMinX());
        int MaxX = (int)Math.floor(bounds.getMaxX());
        int MinY = (int)Math.floor(bounds.getMinY()) - 1;
        int MaxY = (int)Math.floor(bounds.getMinY());
        int MinZ = (int)Math.floor(bounds.getMinZ());
        int MaxZ = (int)Math.floor(bounds.getMaxZ());


        double MARGIN_OF_ERROR = 0.00025d;
        for (int x = MinX; x <= MaxX; x++)
        {
            for (int y = MinY; y <= MaxY; y++)
            {
                for (int z = MinZ; z <= MaxZ; z++)
                {
                    Block TargetBlock = world.getBlockAt(x, y, z);
                    for (BoundingBox BlockCollisionBounds : TargetBlock.getCollisionShape().getBoundingBoxes())
                    {
                        BoundingBox RealBlockBounds = BlockCollisionBounds.shift(x, y, z);
                        if (PCMath.AreBoundsColliding(bounds, RealBlockBounds, MARGIN_OF_ERROR))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static double GetDistanceBetweenBounds(BoundingBox a, BoundingBox b)
    {
        if (PCMath.AreBoundsColliding(a, b))
        {
            return 0d;
        }

        double XDistance = Math.min(
                Math.abs(b.getMinX() - a.getMaxX()),
                Math.abs(a.getMinX() - b.getMaxX()));

        double YDistance = Math.min(
                Math.abs(b.getMinY() - a.getMaxY()),
                Math.abs(a.getMinY() - b.getMaxY()));

        double ZDistance = Math.min(
                Math.abs(b.getMinZ() - a.getMaxZ()),
                Math.abs(a.getMinZ() - b.getMaxZ()));

        return Math.sqrt((XDistance * XDistance) + (YDistance * YDistance) + (ZDistance * ZDistance));
    }

    public static List<BoundingBox> CreateNonOverlappingBounds(Collection<BoundingBox> bounds)
    {
        List<BoundingBox> ResultBounds = new ArrayList<>();

        for (BoundingBox CuttingBound : bounds)
        {
            List<BoundingBox> NewResultBounds = new ArrayList<>();
            for (BoundingBox ExistingBound : ResultBounds)
            {
                NewResultBounds.addAll(BoundsCut(ExistingBound, CuttingBound));
            }

            NewResultBounds.add(CuttingBound);
            ResultBounds = NewResultBounds;
        }

        return Collections.unmodifiableList(ResultBounds);
    }

    public static List<BoundingBox> BoundsCut(BoundingBox source, BoundingBox cut)
    {
        if (!source.overlaps(cut))
        {
            return Collections.singletonList(source.clone());
        }

        List<BoundingBox> Result = new ArrayList<>();

        if (source.getMinX() <= cut.getMinX())
        {
            Result.add(new BoundingBox(source.getMinX(), source.getMinY(), source.getMinZ(),
                    cut.getMinX(), source.getMaxY(), source.getMaxZ()));
        }
        if (cut.getMaxX() <= source.getMaxX())
        {
            Result.add(new BoundingBox(cut.getMaxX(), source.getMinY(), source.getMinZ(),
                    source.getMaxX(), source.getMaxY(), source.getMaxZ()));
        }

        double BoundMinX = Math.max(source.getMinX(), cut.getMinX());
        double BoundMaxX = Math.min(source.getMaxX(), cut.getMaxX());
        if (source.getMinY() <= cut.getMinY())
        {
            Result.add(new BoundingBox(BoundMinX, source.getMinY(), source.getMinZ(),
                    BoundMaxX, cut.getMinY(), source.getMaxZ()));
        }
        if (cut.getMaxY() <= source.getMaxY())
        {
            Result.add(new BoundingBox(BoundMinX, cut.getMaxY(), source.getMinZ(),
                    BoundMaxX, source.getMaxY(), source.getMaxZ()));
        }

        double BoundMinY = Math.max(source.getMinY(), cut.getMinY());
        double BoundMaxY = Math.min(source.getMaxY(), cut.getMaxY());

        if (source.getMinZ() <= cut.getMinZ())
        {
            Result.add(new BoundingBox(BoundMinX, BoundMinY, source.getMinZ(),
                    BoundMaxX, BoundMaxY, cut.getMinZ()));
        }
        if (cut.getMaxZ() <= source.getMaxZ())
        {
            Result.add(new BoundingBox(BoundMinX, BoundMinY, cut.getMaxZ(),
                    BoundMaxX, BoundMaxY, source.getMaxZ()));
        }

        return Result.stream().filter(box -> box.getVolume() > 0).toList();
    }
}
