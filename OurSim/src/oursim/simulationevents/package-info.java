/**
 * Contains the events that drive the simulation. 
 * 
 * The method {@link TimedEvent#action()} of each event is this package is intended to perform 
 * some action specifically related with its equivalent class in package {@link oursim.entities} 
 * and dispatch the appropriate event of the package {@link oursim.dispatchableevents} that will
 * be treaded by all the registered listeners. 
 */
package oursim.simulationevents;