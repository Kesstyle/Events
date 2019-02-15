package by.kes.events.dao;

import by.kes.events.model.KesListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListenableDao<T> {

  protected List<KesListener<T>> listeners = new ArrayList<>();

  public void registerListener(final KesListener<T> listener) {
    listeners.add(listener);
  }

}
