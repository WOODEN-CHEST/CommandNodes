package sus.keiger.plugincommon;

import org.bukkit.util.Vector;

import java.util.Objects;

public class DirectionAxis
{
    // Private fields.
    private final Vector _forwards;
    private final Vector _up;
    private final Vector _left;



    // Constructors.
    public DirectionAxis(Vector forwards, Vector up, Vector left)
    {
        _forwards = Objects.requireNonNull(forwards, "forwards is null").clone();
        _up = Objects.requireNonNull(up, "up is null").clone();
        _left = Objects.requireNonNull(left, "left is null").clone();
    }


    // Methods.
    public Vector GetForwards()
    {
        return _forwards.clone();
    }

    public Vector GetUp()
    {
        return _up.clone();
    }

    public Vector GetLeft()
    {
        return _left.clone();
    }
}