package org.guardian.params;

import java.util.*;
import org.guardian.Guardian;

public class LogBlockParamsParser implements ParamsParser {

	@SuppressWarnings("unused")
    private final Guardian plugin = Guardian.getInstance();
    private final Map<String, Param> keywords;

    protected LogBlockParamsParser() {
        keywords = new HashMap<String, Param>();
        for (final Param param : Param.values()) {
            keywords.put(param.name().toLowerCase(), param);
        }
        // TODO Reading aliases from config
        // for (final String alias : param.getAliases())
        // keywords.put(alias.toLowerCase(), param);
    }

    @Override
    public boolean isKeyWord(String arg) {
        return keywords.containsKey(arg.toLowerCase());
    }

    @Override
    public Map<Param, List<String>> parse(List<String> args) throws IllegalArgumentException {
        final Map<Param, List<String>> params = new EnumMap<Param, List<String>>(Param.class);
        for (int i = 0; i < args.size(); i++) {
            final Param param = keywords.get(args.get(i).toLowerCase());
            if (param != null) // Is a Keyword
                params.put(param, getValues(param, args, i + 1));
        }
        return params;
    }

    private List<String> getValues(Param param, List<String> args, int offset) throws IndexOutOfBoundsException {
        final int minArgs = param.getMinArgs(), maxArgs = param.getMaxArgs();
        if (minArgs == 0 && maxArgs == 0) // Param doesn't accept arguments
        {
            return new ArrayList<String>();
        }
        if (minArgs > 0 && offset + minArgs > args.size()) // Param needs more
        // arguments than
        // given
        {
            throw new IndexOutOfBoundsException();
        }
        if (minArgs == maxArgs) // Param accepts a given count of arguments
        {
            return args.subList(offset, offset + maxArgs);
        }
        final int len = maxArgs > 0 && offset + maxArgs + 1 < args.size() ? offset + maxArgs : args.size(); // Calculation
        // max
        // position
        // out
        // of
        // available
        // arguments
        // and
        // max
        // required
        // arguments.
        int i;
        for (i = offset + minArgs; i < len; i++) // Searching for the next
        // keyword after minimum
        // arguments
        {
            if (isKeyWord(args.get(i))) {
                break;
            }
        }
        if (i == offset) // No arguments needed and next word is a keyword
        {
            return new ArrayList<String>();
        }
        return args.subList(offset, i);
    }
}
