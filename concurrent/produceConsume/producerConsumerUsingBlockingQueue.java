import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ProducerConsumerPattern {

    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> queue = new ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            
                while(!Thread.currentThread().isInterrupted()) {
                        Integer item = new Random().nextInt(10);
                        if(queue.offer(item))
                        System.out.println("Item produced : " + item + " queue size :" + queue.size());
            }
		Thread.interrupted();
                System.out.println("Producer is Interrupted");
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Integer item = queue.poll();
                if(null != item)
                System.out.println("Item consumed : " + item+ " queue size :" + queue.size());
            }
        });

        producer.start();
        consumer.start();
        
        Thread.sleep(2000);
        
        producer.interrupt();
    }

}

