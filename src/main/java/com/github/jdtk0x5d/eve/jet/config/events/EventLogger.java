package com.github.jdtk0x5d.eve.jet.config.events;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Component
@Scope("singleton")
public class EventLogger {

  private static final Logger logger = LogManager.getLogger(EventLogger.class);

  @Subscribe
  public void logEvent(Object event) {
    logger.debug("Event registered: " + event.getClass().getSimpleName() + "." + event);
  }
}