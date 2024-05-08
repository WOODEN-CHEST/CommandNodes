package sus.keiger.bsripoff.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class KeywordNode extends CommandNode
{
    // Private fields.
    private final String _keyword;


    // Constructors.
    public KeywordNode(String keyword, BiConsumer<CommandData, HashMap<String, Object>> executor, String parsedDataKey)
    {
        super(executor, parsedDataKey);

        if (keyword == null)
        {
            throw new IllegalArgumentException("Keyword is null");
        }
        if (keyword.isBlank())
        {
            throw new IllegalArgumentException("Keyword may not be blank.");
        }

        _keyword = keyword;
    }


    // Methods.
    public String GetKeyword()
    {
        return _keyword;
    }



    // Inherited methods.
    @Override
    public List<String> GetSelfSuggestions(CommandData data)
    {
        return Collections.singletonList(_keyword);
    }

    @Override
    public boolean ParseCommand(CommandData data, HashMap<String, Object> parsedData)
    {
        data.MoveIndexToNextNonWhitespace();
        String Keyword = data.ReadWord();
        if (Keyword.equals(_keyword))
        {
            AddParsedData(Keyword, parsedData);
            return true;
        }
        return false;
    }
}
