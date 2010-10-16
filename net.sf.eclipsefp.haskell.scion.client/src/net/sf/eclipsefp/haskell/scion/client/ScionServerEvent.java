package net.sf.eclipsefp.haskell.scion.client;

import java.util.EventObject;

import net.sf.eclipsefp.haskell.scion.internal.servers.GenericScionServer;

public class ScionServerEvent extends EventObject {
  /** Required serial version UID. */
  private static final long serialVersionUID = 1032029311500515018L;
  /** The event type enumeration */
  ScionServerEventType evType;

  /** Construct a scion-server event
   * 
   * @param source The scion-server that caused this event
   * @param evType The event type of the status change
   */
  public ScionServerEvent(GenericScionServer source, ScionServerEventType evType) {
    super(source);
    this.evType = evType;
  }
  /** Get the event type */
  public ScionServerEventType getEventType() {
    return evType;
  }
}
