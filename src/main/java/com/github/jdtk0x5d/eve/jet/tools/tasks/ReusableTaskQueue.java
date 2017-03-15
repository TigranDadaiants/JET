package com.github.jdtk0x5d.eve.jet.tools.tasks;

import com.github.jdtk0x5d.eve.jet.exception.ApplicationException;
import com.github.jdtk0x5d.eve.jet.tools.Action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class ReusableTaskQueue<T> implements TaskQueue<T> {

  private final LinkedList<Task> taskList;
  private LinkedList<Action> finallyActionList = new LinkedList<>();

  private volatile boolean running;
  private volatile boolean finished;

  private Task currentTask;
  private T object;

  public ReusableTaskQueue() {
    taskList = new LinkedList<>();
  }

  private ReusableTaskQueue(LinkedList<Task> taskList) {
    this.taskList = taskList;
  }

  @Override
  public TaskQueue<T> run(Action action) {
    taskList.add(new TaskImpl(action));
    return this;
  }

  @Override
  public TaskQueue<T> consume(Consumer<T> consumer) {
    taskList.add(new TaskImpl(() -> consumer.accept(object)));
    return this;
  }

  @Override
  public <R> TaskQueue<R> process(Function<T, R> function) {
    ReusableTaskQueue<R> taskQueue = new ReusableTaskQueue<>(taskList);
    taskQueue.taskList.add(new TaskImpl(() -> taskQueue.object = function.apply(object)));
    return taskQueue;
  }

  @Override
  public <R> TaskQueue<R> supply(Supplier<R> supplier) {
    ReusableTaskQueue<R> taskQueue = new ReusableTaskQueue<>(taskList);
    taskQueue.taskList.add(new TaskImpl(() -> taskQueue.object = supplier.get()));
    return taskQueue;
  }

  @Override
  public TaskQueue<T> onStop(Action action) {
    taskList.getLast().onStop(action);
    return this;
  }

  @Override
  public TaskQueue<T> finallyAction(Action action) {
    finallyActionList.add(action);
    return this;
  }

  @Override
  public TaskQueue<T> execute() {
    try {
      begin();

      Iterator<Task> taskIterator = taskList.iterator();

      while (taskIterator.hasNext() && running) {
        setNextTask(taskIterator);
        currentTask.run();
      }

    } finally {

      finish();
    }
    return this;
  }

  private synchronized void begin() {
    if (running || !finished) stopAndWait();
    running = true;
    finished = false;
  }

  private synchronized void setNextTask(Iterator<Task> taskIterator) {
    if (running) {
      currentTask = taskIterator.next();
    }
  }

  private synchronized void finish() {
    finallyActionList.forEach(Action::doAction);
    running = false;
    finished = true;
    notifyAwaiting();
  }


  @Override
  public synchronized void stop() {
    stopInternal();
  }

  @Override
  public synchronized void stopAndWait() {
    if (stopInternal()) await();
  }

  private boolean stopInternal() {
    if (finished || !running) return false;

    running = false;
    currentTask.stop();

    return true;
  }

  @Override
  public T getResult() {
    return object;
  }

  @Override
  public boolean isFinished() {
    return finished;
  }

  private synchronized void notifyAwaiting() {
    notifyAll();
  }

  private void await() {
    try {
      wait();
    } catch (InterruptedException e) {
      throw new ApplicationException(e);
    }
  }
}
