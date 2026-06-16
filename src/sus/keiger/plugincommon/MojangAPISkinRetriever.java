package sus.keiger.plugincommon;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.*;
import org.json.simple.parser.*;
import sus.keiger.plugincommon.player.*;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
public class MojangAPISkinRetriever
{
    // Private fields.
    private final Logger _logger;

    private final String JSON_KEY_PROPERTIES = "properties";
    private final String JSON_KEY_NAME = "name";
    private final String JSON_KEY_VALUE = "value";
    private final String JSON_KEY_SIGNATURE = "signature";


    // Constructors.
    public MojangAPISkinRetriever(Logger logger)
    {
        _logger = logger;
    }


    // Private methods.
    private PlayerSkin TryGetSkinFromOnlinePlayer(Player player)
    {
        if (player == null)
        {
            return null;
        }
        ProfileProperty Property = player.getPlayerProfile().getProperties().stream().filter(property ->
                property.getName().equals(PlayerFunctions.PROFILE_KEY_TEXTURES)).findFirst().orElse(null);
        if (Property == null)
        {
            return null;
        }
        return new PlayerSkin(Property.getValue(), Property.getSignature());
    }

    private String GetSkinRequestURL(UUID uuid, boolean isUnsigned)
    {
        return "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=%s".formatted(
                uuid.toString(), Boolean.toString(isUnsigned).toLowerCase());
    }

    private PlayerSkin GetSkinFromProperty(JSONObject json)
    {
        String Name = (String) json.get(JSON_KEY_NAME);
        String Value = (String) json.get(JSON_KEY_VALUE);
        String Signature = (String) json.get(JSON_KEY_SIGNATURE);

        if (!PlayerFunctions.PROFILE_KEY_TEXTURES.equals(Name) || (Value == null) || (Signature == null))
        {
            return null;
        }
        return new PlayerSkin(Value, Signature);
    }

    private PlayerSkin GetSkinFromResponse(JSONObject json)
    {
        try
        {
            JSONArray Properties = (JSONArray)json.get(JSON_KEY_PROPERTIES);
            if (Properties == null)
            {
                return null;
            }

            for (Object Property : Properties)
            {
                PlayerSkin Skin = GetSkinFromProperty((JSONObject)Property);
                if (Skin != null)
                {
                    return Skin;
                }
            }
            return null;
        }
        catch (ClassCastException e)
        {
            if (_logger != null)
            {
                _logger.severe(PCString.ExceptionToString(e));
            }
            return null;
        }
    }

    private PlayerSkin RequestSkin(UUID playerUUID)
    {
        try
        {
            URL TargetURL = URI.create(GetSkinRequestURL(playerUUID, false)).toURL();
            URLConnection Connection = TargetURL.openConnection();
            try (InputStream InStream = Connection.getInputStream())
            {
                String ReadData = new String(InStream.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject ParsedJSON = (JSONObject)new JSONParser().parse(ReadData);
                return GetSkinFromResponse(ParsedJSON);
            }
        }
        catch (IOException | ParseException | ClassCastException e)
        {
            if (_logger != null)
            {
                _logger.severe(PCString.ExceptionToString(e));
            }
            return null;
        }
    }


    // Methods.
    public PlayerSkin GetSkin(UUID playerUUID)
    {
        Objects.requireNonNull(playerUUID, "playerUUID is null");

        PlayerSkin OnlineSkin = TryGetSkinFromOnlinePlayer(Bukkit.getPlayer(playerUUID));
        if (OnlineSkin != null)
        {
            return OnlineSkin;
        }

        return RequestSkin(playerUUID);
    }
}
