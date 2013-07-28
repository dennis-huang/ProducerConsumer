package dennis.learning;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main class to run the Producer/Consumer Pattern
 * It requires 3 inputs:
 * The number of consumer threads, The number of producer threads, and The rate of consumption per second
 * This will display how many resources were produced per thread, and how many were consumed per second
 * Resources (also known as units of work in some conventions) are elements in a queue that the producers and consumers handle.
 * Requires JDK 1.6
 * 
 * @see dennis.learning.Producer
 * @see dennis.learning.Consumer
 * @see dennis.learning.ResourceManager
 * @author Dennis Huang
 * 
 */
public class ProducerConsumerPattern {
	/**
	 * The main class to demonstrate the producer/consumer pattern
	 * 
	 * @param args
	 *            It requires 3 inputs in respective order:
	 *            The number of consumer threads, The number of producer threads, and The rate of consumption per second
	 */
	public static void main(String args[]) {
		if (args == null || args.length != 4) {
			System.err.println("Invalid input. The correct usage is:");
			System.err.println("java dennis.learning.ProducerConsumerPattern #_of_consumer_threads #_of_producer_threads rate_of_consumption_per_second #_of_resources_to_produce_per_thread");
			System.out.println("Example: java dennis.learning.ProducerConsumerPattern 5 5 10 100");
			System.exit(0);
		}
		// The total number of consumer threads
		int consumerThreadCount = 0;
		// The total number of producer threads
		int producerThreadCount = 0;
		// The rate of consumption
		int rateOfConsumption = 0;
		// The max production count per thread
		int maxProductionCount = 0;
		try {
			consumerThreadCount = Integer.parseInt(args[0]);
			producerThreadCount = Integer.parseInt(args[1]);
			rateOfConsumption = Integer.parseInt(args[2]);
			maxProductionCount = Integer.parseInt(args[3]);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid input. The correct usage is:");
			System.err.println("java dennis.learning.ProducerConsumerPattern #_of_consumer_threads #_of_producer_threads rate_of_consumption_per_second #_of_resources_to_produce_per_thread");
			System.out.println("Example: java dennis.learning.ProducerConsumerPattern 5 5 10 100");
			System.exit(0);
		}
		//Size can be anything, but to avoid space issues in the queue, 
		//the size of the arrayBlockingQueue is the same as the total possible resources that can be created at once
		BlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<Integer>(maxProductionCount * producerThreadCount);
		ResourceManager resourceManager = new ResourceManager(arrayBlockingQueue, true);
		ExecutorService producerExecutorService = Executors.newFixedThreadPool(producerThreadCount);
		ExecutorService consumerExecutorService = Executors.newFixedThreadPool(consumerThreadCount);
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		try {
			// Start the consumers
			for (int count = 0; count < consumerThreadCount; count++) {
				consumerExecutorService.submit(new Consumer(count, rateOfConsumption, resourceManager));
			}
			// Prepare the list of producers to execute
			List<Callable<Integer>> callableList = new ArrayList<Callable<Integer>>();
			for (int count = 0; count < consumerThreadCount; count++) {
				callableList.add(new Producer(count, maxProductionCount, resourceManager));
			}
			/*
			 * invokeAll() is blocking, so no new code will execute until all producer tasks have finished.
			 * Since all consumer threads have started already, they will keep processing until completed
			 */
			producerExecutorService.invokeAll(callableList);
			// Set the acceptingResources flag so the consumers know to stop consuming since the producers have finished.
			resourceManager.setAcceptingResources(false);
			String producerFinishDate = dateFormat.format(new Date());
			System.out.println(producerFinishDate + " All producer threads have finished. The consumers still have to process " + arrayBlockingQueue.size() + " resources.");
			// Shut down the executor services
			producerExecutorService.shutdown();
			producerExecutorService.awaitTermination(1, TimeUnit.HOURS);
			consumerExecutorService.shutdown();
			consumerExecutorService.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException ex) {
			System.out.println("An error has occurred:");
			ex.printStackTrace();
		} finally {
			// Force shutdown of the services in case if they have not been properly shutdown
			if (!producerExecutorService.isShutdown()) {
				producerExecutorService.shutdownNow();
			}
			if (!consumerExecutorService.isShutdown()) {
				consumerExecutorService.shutdownNow();
			}
		}
		String endDate = dateFormat.format(new Date());
		System.out.println(endDate + " Producer/Consumer pattern example has completed.");
	}
}
