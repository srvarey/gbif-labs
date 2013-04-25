/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.events;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * One can either extend and bind listeners in this class, or bind them in other modules.
 * Note that you MUST start the listener. Often the easiest way of doing this is simply to bind it as an eager singleton
 * in guice.
 */
public class EventModule extends AbstractModule {

  private final EventBus eventBus = new EventBus();

  @Override
  protected void configure() {
    bind(EventBus.class).toInstance(eventBus);

    // From: http://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/
    bindListener(Matchers.any(), new TypeListener() {

      @Override
      public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        encounter.register(new InjectionListener<I>() {

          @Override
          public void afterInjection(I injectee) {
            eventBus.register(injectee);
          }
        });
      }
    });

    bindEventListeners();
  }

  /**
   * Bind Event listeners here as eager Singletons.
   */
  private void bindEventListeners() {
  }

}
