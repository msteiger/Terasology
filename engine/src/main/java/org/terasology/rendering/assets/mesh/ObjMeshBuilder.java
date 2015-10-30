/*
 * Copyright 2015 MovingBlocks
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

import gnu.trove.list.TIntList;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import de.javagl.obj.AbstractWritableObj;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.ObjFace;

/**
 * An adapter for de.javagl.obj.ObjReader that spits out MeshData instances.
 */
class ObjMeshBuilder extends AbstractWritableObj {

    private final MeshData result = new MeshData();

    private final Set<String> activeGroups = new LinkedHashSet<>();

    public MeshData getMeshData() {
        int idx = result.getIndices().size();

        for (String group : activeGroups) {
            result.endGroupAt(group, idx - 1);
        }

        activeGroups.clear();

        return result;
    }


    @Override
    public void addVertex(FloatTuple vertex) {
        result.getVertices().add(vertex.getX());
        result.getVertices().add(vertex.getY());
        result.getVertices().add(vertex.getZ());
    }

    @Override
    public void addTexCoord(FloatTuple texCoord) {
        result.getTexCoord0().add(texCoord.getX());
        result.getTexCoord0().add(1 - texCoord.getY());
    }

    @Override
    public void addNormal(FloatTuple normal) {
        result.getNormals().add(normal.getX());
        result.getNormals().add(normal.getY());
        result.getNormals().add(normal.getZ());
    }

    @Override
    public void addFace(ObjFace face) {
        TIntList indices = result.getIndices();
        for (int i = 0; i < face.getNumVertices() - 2; i++) {
            indices.add(face.getVertexIndex(0));
            indices.add(face.getVertexIndex(i + 1));
            indices.add(face.getVertexIndex(i + 2));
        }
    }

    @Override
    public void setActiveGroupNames(Collection<? extends String> groupNames) {
        int idx = result.getIndices().size();

        Collection<String> removed = new LinkedHashSet<>(activeGroups);
        removed.removeAll(groupNames);

        Collection<String> added = new LinkedHashSet<>(groupNames);
        added.removeAll(activeGroups);

        for (String group : removed) {
            result.endGroupAt(group, idx - 1);
        }

        for (String group : added) {
            result.startGroupAt(group, idx);
        }

        activeGroups.clear();
        activeGroups.addAll(groupNames);
    }
}
