package common;

@FunctionalInterface
public interface Converter<InputType, OutputType> {
    OutputType call(InputType in);
}
