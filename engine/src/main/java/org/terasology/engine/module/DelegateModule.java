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

package org.terasology.engine.module;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import org.reflections.Reflections;
import org.terasology.module.Module;
import org.terasology.module.ModuleMetadata;
import org.terasology.naming.Name;
import org.terasology.naming.Version;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class DelegateModule implements Module {

    private final Module mod;

    /**
     * @param skin
     */
    public DelegateModule(Module mod) {
        Preconditions.checkArgument(mod != null);

        this.mod = mod;
    }

    @Override
    public ImmutableList<Path> getLocations() {
        return mod.getLocations();
    }

    @Override
    public FileSystem getFileSystem() {
        return mod.getFileSystem();
    }

    @Override
    public ImmutableList<Path> findFiles() throws IOException {
        return mod.findFiles();
    }

    @Override
    public ImmutableList<Path> findFiles(String fileFilterGlob) throws IOException {
        return mod.findFiles(fileFilterGlob);
    }

    @Override
    public ImmutableList<Path> findFiles(Path rootPath, PathMatcher scanFilter, PathMatcher fileFilter) throws IOException {
        return mod.findFiles(rootPath, scanFilter, fileFilter);
    }

    @Override
    public ImmutableList<URL> getClasspaths() {
        return mod.getClasspaths();
    }

    @Override
    public Name getId() {
        return mod.getId();
    }

    @Override
    public Version getVersion() {
        return mod.getVersion();
    }

    @Override
    public ImmutableSet<String> getRequiredPermissions() {
        return mod.getRequiredPermissions();
    }

    @Override
    public boolean isOnClasspath() {
        return mod.isOnClasspath();
    }

    @Override
    public boolean isCodeModule() {
        return mod.isCodeModule();
    }

    @Override
    public Reflections getReflectionsFragment() {
        return mod.getReflectionsFragment();
    }

    @Override
    public ModuleMetadata getMetadata() {
        return mod.getMetadata();
    }


}
