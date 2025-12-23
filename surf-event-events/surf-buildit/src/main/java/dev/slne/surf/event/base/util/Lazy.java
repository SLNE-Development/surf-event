package dev.slne.surf.event.base.util;

import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Lazy<T> {

  private final Supplier<T> supplier;
  private T value;

  @Contract(pure = true)
  public Lazy(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  @Contract(value = "_ -> new", pure = true)
  public static <T> @NotNull Lazy<T> of(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

  public T get() {
    if (value == null) {
      value = supplier.get();
    }
    return value;
  }

  public <O> Lazy<O> map(Function<T, O> mapper) {
    return new Lazy<>(() -> mapper.apply(get()));
  }

  public interface Supplier<T> {

    T get();
  }
}
