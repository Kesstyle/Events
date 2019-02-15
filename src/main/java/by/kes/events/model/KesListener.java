package by.kes.events.model;

public interface KesListener<T> {
  void listen(final T t);
}
