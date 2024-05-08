package sus.keiger.bsripoff.command;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StringNode extends CommandNode
{
    // Static fields.
    public static final char QUOTE = '"';
    public static final char ESCAPE_CHARACTER = '\\';


    // Private fields.
    private boolean _requireQuotes = false;
    private final Function<CommandData, List<String>> _suggestionSupplier;


    // Constructors.
    public StringNode(BiConsumer<CommandData, HashMap<String, Object>> executor,
                     boolean requireQuotes,
                     Function<CommandData, List<String>> suggestionSupplier,
                      String parsedDataKeyword)
    {
        super(executor, parsedDataKeyword);
        _requireQuotes = requireQuotes;
        _suggestionSupplier = suggestionSupplier;
    }


    // Static methods.
    public static String ParseQuotedString(CommandData data)
    {
        if (!data.IsMoreDataAvailable() || (data.GetCommand().charAt(data.GetIndex()) != QUOTE))
        {
            return null;
        }

        data.SetIndex(data.GetIndex() + 1);

        StringBuilder ParsedData = new StringBuilder();
        boolean HadEscapeChar = false;
        while (data.IsMoreDataAvailable() && ((data.GetCommand().charAt(data.GetIndex()) != QUOTE) || HadEscapeChar))
        {
            char Character = data.GetCommand().charAt(data.GetIndex());

            if (!HadEscapeChar && (Character != ESCAPE_CHARACTER))
            {
                ParsedData.append(Character);
                data.SetIndex(data.GetIndex() + 1);
                continue;
            }
            else if (HadEscapeChar)
            {
                ParsedData.append(GetEscapeChar(Character));
                HadEscapeChar = false;
                data.SetIndex(data.GetIndex() + 1);
                continue;
            }

            HadEscapeChar = true;
            data.SetIndex(data.GetIndex() + 1);
        }

        if (!data.IsMoreDataAvailable())
        {
            return null;
        }

        data.SetIndex(data.GetIndex() + 1);
        return ParsedData.toString();
    }


    // Private static methods.
    private static char GetEscapeChar(char character)
    {
        switch (character)
        {
            case 'n' -> { return '\n'; }
            case 't' -> { return '\t'; }
            case '\\' -> { return '\\'; }
            case 'r' -> { return '\r'; }
            default -> { return character; }
        }
    }


    // Inherited methods./
    @Override
    public boolean ParseCommand(CommandData data, HashMap<String, Object> parsedData)
    {
        if (_requireQuotes || (data.IsMoreDataAvailable() && data.GetCommand().charAt(data.GetIndex()) == QUOTE))
        {
            String ParsedString = ParseQuotedString(data);
            if (ParsedString != null)
            {
                AddParsedData(ParsedString, parsedData);
                return true;
            }
            return false;
        }

        AddParsedData(data.GetCommand().substring(data.GetIndex()), parsedData);
        return true;
    }

    @Override
    public List<String> GetSelfSuggestions(CommandData data)
    {
        return _suggestionSupplier != null ? _suggestionSupplier.apply(data) : new ArrayList<>();
    }
}