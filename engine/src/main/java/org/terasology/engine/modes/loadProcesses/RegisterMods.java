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

package org.terasology.engine.modes.loadProcesses;

import org.terasology.registry.CoreRegistry;
import org.terasology.engine.GameEngine;
import org.terasology.engine.bootstrap.ApplyModulesUtil;
import org.terasology.engine.modes.StateMainMenu;
import org.terasology.engine.module.Module;
import org.terasology.engine.module.ModuleIdentifier;
import org.terasology.engine.module.ModuleManager;
import org.terasology.game.GameManifest;

/**
 * @author Immortius
 */
public class RegisterMods extends SingleStepLoadProcess {

    private GameManifest gameManifest;

    public RegisterMods(GameManifest gameManifest) {
        this.gameManifest = gameManifest;
    }

    @Override
    public String getMessage() {
        return "Registering Mods...";
    }

    @Override
    public boolean step() {
        ModuleManager moduleManager = CoreRegistry.get(ModuleManager.class);
        moduleManager.disableAllModules();

        for (ModuleIdentifier moduleId : gameManifest.getModules()) {
            Module module = moduleManager.getModule(moduleId.getId(), moduleId.getVersion());
            if (module != null) {
                moduleManager.enableModule(module);
            } else {
                CoreRegistry.get(GameEngine.class).changeState(new StateMainMenu("Missing required module: " + moduleId));
                return true;
            }
        }

        ApplyModulesUtil.applyModules();
        return true;
    }
}
