package common;

@FunctionalInterface
public interface Converter<InputType, OutputType> {
    OutputType convert(InputType in);
}
