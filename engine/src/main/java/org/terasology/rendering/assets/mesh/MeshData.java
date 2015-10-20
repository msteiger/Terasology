/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.assets.mesh;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.terasology.assets.AssetData;

/**
 * @author Immortius
 */
public class MeshData implements AssetData {

    private TFloatList vertices = new TFloatArrayList();
    private TFloatList texCoord0 = new TFloatArrayList();
    private TFloatList texCoord1 = new TFloatArrayList();
    private TFloatList normals = new TFloatArrayList();
    private TFloatList colors = new TFloatArrayList();
    private TIntList indices = new TIntArrayList();

    private Map<String, IndexRange> groups = new LinkedHashMap<>();

    public MeshData() {
    }

    public TFloatList getVertices() {
        return vertices;
    }

    public TFloatList getTexCoord0() {
        return texCoord0;
    }

    public TFloatList getTexCoord1() {
        return texCoord1;
    }

    public TFloatList getNormals() {
        return normals;
    }

    public TFloatList getColors() {
        return colors;
    }

    public TIntList getIndices() {
        return indices;
    }

    /**
     * Adds a (face) group. If it already exists, this will overwrite previous settings.
     * @param groupId the group id
     * @param startIndex the first index of the group
     */
    public void startGroupAt(String groupId, int startIndex) {
        groups.put(groupId, new IndexRange(startIndex));
    }

    /**
     * Sets the end index of a (face) group. If it already exists, this will overwrite previous settings.
     * @param groupId the group id
     * @param endIndex the first index of the group
     */
    public void endGroupAt(String groupId, int endIndex) {
        IndexRange group = groups.get(groupId);
        group.setLength(endIndex + 1 - group.getMin());
    }

    /**
     * Finds the start index of a (face) group
     * @param groupId the group id
     * @return the first vertex index
     */
    public int getGroupStart(String groupId) {
        return groups.get(groupId).getMin();
    }

    /**
     * Finds the length a (face) group
     * @param group the group id
     * @return the first vertex index
     */
    public int getGroupLength(String group) {
        return groups.get(group).getLength();
    }

    /**
     * An unmodifiable set of group IDs
     * @return the group IDs.
     */
    public Set<String> getGroupIds() {
        return Collections.unmodifiableSet(groups.keySet());
    }

    private static class IndexRange {
        private int length;
        private int min;

        public IndexRange(int min) {
            this.min = min;
            this.length = 0;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return (min + length - 1);
        }

        @Override
        public String toString() {
            return "IndexRange [" + min + ".." + getMax() + "]";
        }
    }
}
