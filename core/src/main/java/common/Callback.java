package common;

@FunctionalInterface
public interface Callback<InputType, OutputType> {
    OutputType call(InputType in);
}
