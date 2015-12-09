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

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import de.javagl.obj.ObjReader;

/**
 *
 */
public class ObjMeshBuilderTest {

    private static final String SQUARE_AND_TRIANGLE = ""
            + "v 0.0 0.0 0.0\n"
            + "v 4.0 0.0 0.0\n"
            + "v 4.0 4.0 0.0\n"
            + "v 0.0 4.0 0.0\n"
            + "v 2.0 6.0 0.0\n"
            + "g all group0\n"
            + "f 3 4 5\n"
            + "g all group1\n"
            + "f 1 2 3\n"
            + "f 4 3 2\n"
            + "g unused\n"
            + "f 2 1 3";

    @Test
    public void test() throws IOException {
        ObjMeshBuilder meshBuilder = new ObjMeshBuilder();
        ObjReader.read(new StringReader(SQUARE_AND_TRIANGLE), meshBuilder);
        MeshData data = meshBuilder.getMeshData();
        System.out.println(data.getIndices());
        Assert.assertEquals(0, data.getGroupStart("group0"));
        Assert.assertEquals(3, data.getGroupLength("group0"));
        Assert.assertEquals(3, data.getGroupStart("group1"));
        Assert.assertEquals(6, data.getGroupLength("group1"));
        Assert.assertEquals(0, data.getGroupStart("all"));
        Assert.assertEquals(9, data.getGroupLength("all"));
    }
}
