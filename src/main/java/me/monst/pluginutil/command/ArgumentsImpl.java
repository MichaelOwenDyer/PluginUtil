package me.monst.pluginutil.command;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ArgumentsImpl implements Arguments {

    private final String[] args;
    private final int startIndex; // inclusive
    private final int endIndex; // exclusive
    
    public ArgumentsImpl(String[] args) {
        this.args = combineQuotedArguments(args);
        this.startIndex = 0;
        this.endIndex = this.args.length;
    }
    
    private static String[] combineQuotedArguments(String[] args) {
        if (args.length == 0)
            return args;
        String[] combined = new String[args.length];
        int combinedIndex = 0;
        StringBuilder builder = new StringBuilder();
        boolean inQuotes = false;
        for (String arg : args) {
            if (inQuotes) {
                if (arg.endsWith("\"")) {
                    builder.append(' ').append(arg, 0, arg.length() - 1);
                    combined[combinedIndex++] = builder.toString();
                    builder.setLength(0);
                    inQuotes = false;
                } else {
                    builder.append(' ').append(arg);
                }
            } else {
                if (arg.startsWith("\"")) {
                    if (arg.length() > 1 && arg.endsWith("\"")) {
                        combined[combinedIndex++] = arg.substring(1, arg.length() - 1);
                    } else {
                        builder.append(arg, 1, arg.length());
                        inQuotes = true;
                    }
                } else {
                    combined[combinedIndex++] = arg;
                }
            }
        }
        if (inQuotes)
            combined[combinedIndex++] = "\"" + builder; // append the opening quote again
        return Arrays.copyOf(combined, combinedIndex);
    }
    
    private ArgumentsImpl(String[] args, int startIndex, int endIndex) {
        this.args = args;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
    @Override
    public boolean isEmpty() {
        return startIndex == endIndex;
    }
    
    @Override
    public int size() {
        return endIndex - startIndex;
    }
    
    @Override
    public Argument<String> get(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("index = " + index);
        if (index >= size())
            return Argument.empty();
        return Argument.of(args[startIndex + index]);
    }
    
    @Override
    public Arguments between(int fromIndex, int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        return new ArgumentsImpl(args, startIndex + fromIndex, startIndex + toIndex);
    }
    
    @Override
    public List<String> asList() {
        return Arrays.asList(args).subList(startIndex, endIndex);
    }
    
    @Override
    public Stream<String> stream() {
        return asList().stream();
    }
    
    @Override
    public String join(CharSequence delimiter) {
        return String.join(delimiter, this);
    }
    
    @Override
    public String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return prefix + join(delimiter) + suffix;
    }
    
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int index = startIndex;
            @Override
            public boolean hasNext() {
                return index < endIndex;
            }
            @Override
            public String next() {
                if (!hasNext())
                    throw new IndexOutOfBoundsException("index = " + index + ", endIndex = " + endIndex);
                return args[index++];
            }
        };
    }
    
}
