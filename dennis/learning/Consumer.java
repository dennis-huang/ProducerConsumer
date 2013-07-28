package dennis.learning;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * This is the consumer class of the Producer/Consumer pattern. This will take a resource from the resourceManager's queue and consume it.
 * Consuming is defined by removing the head element of the resourceManager's queue
 * This will output the number of resources it has consumed.
 * 
 * @author Dennis Huang
 * 
 */
public class Consumer implements Callable<Integer> {
	/**
	 * Holds date formatter to output timestamps, but not dates
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	/**
	 * Holds the duration, in milliseconds, for the consumer thread to sleep.
	 * The thread will sleep for this time after consuming the number provided by the rate of consumption.
	 */
	private static final int THREAD_SLEEP_TIME = 1000;
	/**
	 * Holds the thread number so we have an ID of each thread
	 */
	private int threadNumber;
	/**
	 * Holds the resourceManager so this consumer has access to the resources to consume
	 */
	private ResourceManager resourceManager;
	/**
	 * Holds the rate of consumption. This is the number of resources the consumer will consume per second
	 */
	private int rateOfConsumption;

	/**
	 * The consumer constructor. It must take in parameters
	 * 
	 * @param threadNumber
	 *            the ID of the thread
	 * @param rateOfConsumption
	 *            the number of resources to consume per second
	 * @param resourceManager
	 *            the resourceManager to pull the data from
	 */
	public Consumer(int threadNumber, int rateOfConsumption, ResourceManager resourceManager) {
		this.threadNumber = threadNumber;
		this.rateOfConsumption = rateOfConsumption;
		this.resourceManager = resourceManager;
	}

	/**
	 * The main function of the consumer thread to run. This polls from the resourceManager and removes the head and outputs.
	 * This will sleep depending on the rateOfConsumption
	 */
	@Override
	public Integer call() {
		try {
			// Loop until there is no more data and resourceManager is no longer accepting resources
			while (resourceManager.size() > 0 || resourceManager.isAcceptingResources()) {
				int consumedResources = 0;
				// Manage the consumption time.  Quit if there are no more resources to consume
				for (int count = 0; count < rateOfConsumption && resourceManager.size() > 0; count++) {
					Integer data = resourceManager.poll();
					consumedResources++;
				}
				Thread.sleep(THREAD_SLEEP_TIME);
				String consumeDate = DATE_FORMAT.format(new Date());
				System.out.println(consumeDate + " Consumer [" + threadNumber + "] consumed " + consumedResources + " resources.");
			}
			String endDate = DATE_FORMAT.format(new Date());
			System.out.println(endDate + " Consumer thread [" + threadNumber + "] has finished.");
		} catch (InterruptedException ex) {
			String errorDate = DATE_FORMAT.format(new Date());
			System.out.println(errorDate + " Consumer thread [" + threadNumber + "] has been interrupted.");
			ex.printStackTrace();
		}
		return threadNumber;
	}
}
