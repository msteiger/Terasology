/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.rendering.nui.layers.mainMenu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

import org.terasology.rendering.nui.databinding.Binding;

/**
 * Fires a notification event when {@link #set(Object)} is called.
 * <br>
 * The property name is <code>className.fieldName</code>.
 */
public class PropertyChangeBinding<T> implements Binding<T> {

    private final Binding<T> binding;
    private Object source;
    private String propertyName;
    private Collection<PropertyChangeListener> listeners;

    /**
     * @param binding
     */
    public PropertyChangeBinding(Binding<T> binding, Object source, Field field, Collection<PropertyChangeListener> listeners) {
        this.binding = binding;
        this.source = source;
        this.propertyName = field.getDeclaringClass().getName() + "." + field.getName();
        this.listeners = listeners;
    }

    @Override
    public T get() {
        return binding.get();
    }

    @Override
    public void set(T value) {
        T old = binding.get();
        binding.set(value);

        if (!Objects.equals(old, value)) {
            PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName, old, value);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(evt);
            }
        }
    }
}
