package dennis.learning;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The resource manager for the producer/consumer pattern.
 * This serves as the intermediary to manage the resource flow between the producer and consumer
 * The resources are represented by Integer objects.
 * Resources (also known as units of work in some conventions) are elements in a queue that the producers and consumers handle.
 * 
 * @author Dennis Huang
 * 
 */
public class ResourceManager {
	/**
	 * Holds the main collection to store or retrieve the resources.
	 * This is to be accessed by producers and consumers
	 */
	private BlockingQueue<Integer> resourceBlockingQueue;

	/**
	 * Holds the flag of whether the ResourceManager can accept any more resources as input.
	 * True if the ResourceManager will still accept more input. This should be set if the producers are still using this manager.
	 * False if the ResourceManager will not accept more input. This should be set when the producers no longer have any resources to give
	 */
	private boolean acceptingResources;

	/**
	 * Default Constructor
	 */
	public ResourceManager() {
		resourceBlockingQueue = new ArrayBlockingQueue<Integer>(100);
		acceptingResources = true;
	}

	/**
	 * Constructor for resource manager that can have predefined settings
	 * 
	 * @param blockingQueue
	 *            a BlockingQueue that can hold the resources
	 * @param acceptingResources
	 *            Whether the ResourceManager can accept any more resources.
	 */
	public ResourceManager(BlockingQueue<Integer> blockingQueue, boolean acceptingResources) {
		this.resourceBlockingQueue = blockingQueue;
		this.acceptingResources = acceptingResources;
	}

	/**
	 * Get the acceptingResources boolean
	 * 
	 * @return acceptingResources if the ResourceManager is not accepting any more resources
	 */
	public boolean isAcceptingResources() {
		return acceptingResources;
	}

	/**
	 * Set the acceptingResources boolean
	 * 
	 * @param acceptingResources
	 */
	public void setAcceptingResources(boolean acceptingResources) {
		this.acceptingResources = acceptingResources;
	}

	/**
	 * Puts the resource at the end of the queue
	 * This will only put resources if it is acceptingResources
	 * 
	 * @see #acceptingResources
	 * @param resource
	 *            as an Integer
	 * @throws InterruptedException
	 *             if the queue is interrupted while waiting for the resource to be put
	 */
	public void put(Integer resource) throws InterruptedException {
		if (acceptingResources) {
			resourceBlockingQueue.put(resource);
		}
	}

	/**
	 * Returns the head of a queue with a timeout of 1 second.
	 * 
	 * @return The resource as an integer
	 * @throws InterruptedException
	 *             if the timeout has occurred before the queue could return
	 */
	public Integer poll() throws InterruptedException {
		return resourceBlockingQueue.poll();
	}

	/**
	 * Return the size of the containing queue.
	 * Size is defined by how many elements are in the queue
	 * 
	 * @return the size of the queue, how many elements are in it
	 */
	public int size() {
		return resourceBlockingQueue.size();
	}
}
