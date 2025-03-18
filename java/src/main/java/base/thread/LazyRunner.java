package base.thread;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

public class LazyRunner {
	private Queue<Runnable> queue;
	private Queue<Runnable> finishQueue;
	private Thread thread;
	private ThreadState state;

	public LazyRunner() {
		this.thread = new Thread(this::exec);
		this.queue = new ConcurrentLinkedQueue<Runnable>();
		this.finishQueue = new ConcurrentLinkedQueue<Runnable>();
		this.state = ThreadState.NEW;
	}

	private void execQueue(Queue<Runnable> queue) {
		while (!queue.isEmpty()) {
			try {
				queue.poll().run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void waitFor() {
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void exec() {
		while (true) {
			if (state.equals(ThreadState.EXIT)) {
				this.state = ThreadState.FINISHING;
				break;
			}
			execQueue(queue);
			waitFor();
		}
		execQueue(finishQueue);
		this.state = ThreadState.FINISHED;
	}

	private void verify() {
		synchronized (this) {
			this.notify();
		}
	}

	public void push(Runnable r) {
		Objects.requireNonNull(r);
		this.queue.add(r);
		verify();
	}

	public void onExit(Runnable r) {
		Objects.requireNonNull(r);
		this.finishQueue.add(r);
	}

	public void start() {
		if (state.equals(ThreadState.RUNNING))
			throw new IllegalThreadStateException("Thread já iniciado");
		if (state.equals(ThreadState.FINISHED))
			throw new IllegalThreadStateException("Thread finalizada");
		if (!state.equals(ThreadState.NEW))
			throw new IllegalThreadStateException("Thread com status diferente de novo");
		thread.start();
		this.state = ThreadState.RUNNING;
	}

	public void exit() {
		this.state = ThreadState.EXIT;
	}

  public ThreadState getState(){
    return  this.state;
  }
  public enum ThreadState {
		NEW, RUNNING, EXIT, FINISHING, FINISHED
	}
}
