package sus.keiger.bsripoff.command;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class RotationNode extends CoordinateNode
{
    // Constructors.
    public RotationNode(BiConsumer<CommandData, HashMap<String, Object>> executor, String parsedDataKey)
    {
        super(executor, 2, parsedDataKey);
    }


    // Inherited methods.
    @Override
    public double GetCoordinateByIndex(Location location, int index)
    {
        switch (index)
        {
            case 0 -> { return location.getYaw(); }
            case 1 -> { return location.getPitch(); }
            default -> throw new IllegalArgumentException("Index (%d) outside of accepted range".formatted(index));
        }
    }

    @Override
    public Location ParsedCoordinatesToLocation(World world, List<Double> coordinates)
    {
        return new Location(world, 0d, 0d, 0d, coordinates.get(0).floatValue(), (coordinates.get(1).floatValue()));
    }
}