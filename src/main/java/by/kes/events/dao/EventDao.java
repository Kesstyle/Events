package by.kes.events.dao;

import static java.util.Arrays.asList;

import by.kes.events.model.Event;
import by.kes.events.model.KesListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EventDao extends AbstractListenableDao<Event> {

  private AtomicLong idCounter = new AtomicLong(10);

  private Set<Event> events = new HashSet<>();

  public Flux<Event> getAll() {
    return Flux.fromIterable(events).mergeWith(Flux.create(emitter -> {
      final KesListener<Event> listener = emitter::next;
      this.registerListener(listener);
    }, FluxSink.OverflowStrategy.LATEST));
  }

  public Mono<Void> add(final Mono<Event> event) {
    return event
        .map(e -> {
          e.setId(String.valueOf(idCounter.getAndIncrement()));
          return e;
        }).flatMap(e -> {
          events.add(e);
          notifyListeners(asList(e));
          return Mono.empty();
        });
  }

  public Mono<Void> remove(final Flux<String> id) {
    return id.map(i -> events.stream()
        .filter(e -> e.getId().equals(i))
        .findFirst().orElse(null))
        .filter(e -> e != null)
        .map(e -> {
          events.remove(e);
          e.setId("--" + e.getId());
          notifyListeners(asList(e));
          return e;
        })
        .then();
  }

  public Mono<Void> update(final Flux<Event> event) {
    return event.map(e -> {
      events.stream()
          .filter(ev -> ev.getId().equals(e.getId()))
          .forEach(ev -> BeanUtils.copyProperties(e, ev));
      return e;
    }).map(e -> {
      e.setId("*" + e.getId());
      notifyListeners(asList(e));
      return e;
    }).then();
  }

  public void addAll(final Collection<Event> events) {
    this.events.addAll(events);
  }

  private void notifyListeners(final Collection<Event> event) {
    listeners.stream().forEach(l -> event.stream().forEach(e -> l.listen(e)));
  }
}
