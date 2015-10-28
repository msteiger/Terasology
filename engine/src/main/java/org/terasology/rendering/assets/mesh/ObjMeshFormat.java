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

import gnu.trove.map.TMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetFileFormat;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import de.javagl.obj.Objs;

/**
 * Importer for Wavefront obj files. Supports core obj mesh data
 *
 * @author Immortius
 */
@RegisterAssetFileFormat
public class ObjMeshFormat extends AbstractAssetFileFormat<MeshData> {

    public ObjMeshFormat() {
        super("obj");
    }

    @Override
    public MeshData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        try (InputStream stream = inputs.get(0).openStream()) {

            Obj tempObj1 = ObjReader.read(stream);
            ObjMeshBuilder meshBuilder = new ObjMeshBuilder();
            Obj result = ObjUtils.triangulate(tempObj1);
            result = ObjUtils.makeTexCoordsUnique(result);
            result = ObjUtils.makeNormalsUnique(result);
            ObjUtils.makeVertexIndexed(result, meshBuilder);
            MeshData data = meshBuilder.getMeshData();

            if (data.getVertices() == null) {
                throw new IOException("No vertices defined");
            }
            if (!data.getNormals().isEmpty() && data.getNormals().size() != data.getVertices().size()) {
                throw new IOException("The number of normals does not match the number of vertices.");
            }
            if (!data.getTexCoord0().isEmpty() && data.getTexCoord0().size() / 2 != data.getVertices().size() / 3) {
                throw new IOException("The number of tex coords does not match the number of vertices.");
            }

            return data;
        }
    }
}
