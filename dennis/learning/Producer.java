package dennis.learning;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * This is the Producer class of the Producer/Consumer pattern. This will produce a resource and add it to the tail of the resourceManager
 * Producing is defined by creating a resource (integer object in this case) and adding it to the end of the resourceManager's queue.
 * 
 * @author Dennis Huang
 * 
 */
public class Producer implements Callable<Integer> {
	/**
	 * Holds date formatter to output timestamps, but not dates
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	/**
	 * Holds the thread number so we have an ID of each thread
	 */
	private int threadNumber;
	/**
	 * Holds the maximum amount of resources to be added to the resourceManager
	 */
	private int maxProductionCount;
	/**
	 * Holds the resourceManager so this consumer has access to the resources to consume
	 */
	private ResourceManager resourceManager;

	/**
	 * The producer constructor. It must take in parameters
	 * 
	 * @param threadNumber
	 *            the ID of the thread
	 * @param resourceManager
	 *            the resourceManager to pull the data from
	 */
	public Producer(int threadNumber, int maxProductionCount, ResourceManager resourceManager) {
		this.threadNumber = threadNumber;
		this.maxProductionCount = maxProductionCount;
		this.resourceManager = resourceManager;
	}

	/**
	 * The main function of the producer task to be called. This polls from the resourceManager and adds to the tail.
	 * This will sleep depending on the rateOfConsumption
	 */
	@Override
	public Integer call() {
		String beginDate = DATE_FORMAT.format(new Date());
		System.out.println(beginDate + " Producer thread [" + threadNumber + "] has started.");
		try {
			for (int i = 0; i < maxProductionCount; i++) {
				// A little extra processing so we know what ID produced what value.
				int threadAndCount = threadNumber * 10000 + i;
				resourceManager.put(threadAndCount);
			}
			String endDate = DATE_FORMAT.format(new Date());
			System.out.println(endDate + " Producer thread [" + threadNumber + "] has finished producing " + maxProductionCount + " resources.");
		} catch (InterruptedException ex) {
			String errorDate = DATE_FORMAT.format(new Date());
			System.out.println(errorDate + " Producer thread [" + threadNumber + "] has been interrupted.");
			ex.printStackTrace();
		}
		return threadNumber;
	}

}
