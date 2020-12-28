
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerPatternUsingCustomBlockingQueue {

    public static void main(String[] args) throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(5);

        Thread producer = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {
                Integer item = new Random().nextInt(10);
                if (queue.offer(item))
                    System.out.println("Item produced : " + item + " queue size :" + queue.size());
            }
            System.out.println("Producer is Interrupted");
        });

        Thread consumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Integer item = queue.poll();
                if (null != item)
                    System.out.println("Item consumed : " + item + " queue size :" + queue.size());
                if(queue.size()==0) {
                    Thread.currentThread().interrupt();
                    System.out.println("Nothing in queue so stopping !!");
                }
            }
            System.out.println("Consumer is Interrupted, END");
        });

        producer.start();
        consumer.start();

        Thread.sleep(3000);

        producer.interrupt();
        
    }

}

class CustomBlockingQueue<E> {
    private Queue<E> queue;
    int max = 10;
    final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public CustomBlockingQueue(int size) {
        queue = new LinkedList<>();
        this.max = size;
    }

    public int size() {
        return queue.size();
    }

    public boolean offer(E e) {
        boolean isAdded = false;
        lock.lock();
        try {
            while (queue.size() == max) {
                notFull.await();
            }
            isAdded = queue.add(e);
            notEmpty.signalAll();
        } catch (InterruptedException exc) {
            System.out.println("InterruptedException in PRODUCER");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return isAdded;
    }

    public E poll() {
        E item = null;
        lock.lock();
        try {
            while (queue.size() == 0) {
                notEmpty.await();
            }
            item = queue.remove();
            notFull.signalAll();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException in CONSUMER");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        return item;
    }
}

