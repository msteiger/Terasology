/*
 * Copyright 2014 MovingBlocks
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.terasology.config.Config;
import org.terasology.config.ServerInfo;
import org.terasology.engine.GameEngine;
import org.terasology.engine.modes.StateLoading;
import org.terasology.engine.module.ModuleManager;
import org.terasology.module.ModuleRegistry;
import org.terasology.naming.Name;
import org.terasology.naming.NameVersion;
import org.terasology.naming.Version;
import org.terasology.network.JoinStatus;
import org.terasology.network.NetworkSystem;
import org.terasology.network.ServerInfoMessage;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.BindHelper;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.IntToStringBinding;
import org.terasology.rendering.nui.databinding.ListSelectionBinding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.ItemActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.world.internal.WorldInfo;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * @author Immortius
 */
public class JoinGameScreen extends CoreScreenLayer {

    @In
    private Config config;

    @In
    private NetworkSystem networkSystem;

    @In
    private GameEngine engine;

    @In
    private ModuleManager moduleManager;

    private UIList<ServerInfo> serverList;

    private Map<ServerInfo, Future<ServerInfoMessage>> extInfo = new HashMap<>();

    @Override
    public void initialise() {
        serverList = find("serverList", UIList.class);
        if (serverList != null) {
            serverList.bindList(BindHelper.bindBeanListProperty("servers", config.getNetwork(), ServerInfo.class));
            serverList.setItemRenderer(new StringTextRenderer<ServerInfo>() {
                @Override
                public String getString(ServerInfo value) {
                    return value.getName();
                }
            });
            serverList.subscribe(new ItemActivateEventListener<ServerInfo>() {
                @Override
                public void onItemActivated(UIWidget widget, ServerInfo item) {
                    join(item.getAddress(), item.getPort());
                }
            });

            final ListSelectionBinding<ServerInfo> infoBinding = new ListSelectionBinding<ServerInfo>(serverList);

            UILabel modules = find("modules", UILabel.class);
            modules.bindText(new ReadOnlyBinding<String>() {
                @Override
                public String get() {
                    Future<ServerInfoMessage> info = extInfo.get(serverList.getSelection());
                    if (info != null) {
                        if (info.isDone()) {
                            try {
                                List<String> codedModInfo = new ArrayList<>();
                                ModuleRegistry reg = moduleManager.getRegistry();
                                for (NameVersion entry : info.get().getModuleList()) {
                                    boolean isInstalled = reg.getModule(entry.getName(), entry.getVersion()) != null;
                                    Color color = isInstalled ? Color.GREEN : Color.RED;
                                    codedModInfo.add(FontColor.getColored(entry.toString(), color));
                                }
                                return "aa";//Joiner.on('\n').join(codedModInfo);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace(); // never!
                            }
                        } else {
                            return "requested";
                        }
                    }
                    return null;
                }
            });

            UILabel worlds = find("worlds", UILabel.class);
            worlds.bindText(new ReadOnlyBinding<String>() {
                @Override
                public String get() {
                    Future<ServerInfoMessage> info = extInfo.get(serverList.getSelection());
                    if (info != null) {
                        if (info.isDone()) {
                            try {
                                List<String> codedWorldInfo = new ArrayList<>();
                                for (WorldInfo wi : info.get().getWorldInfoList()) {
                                    codedWorldInfo.add(wi.getTitle());
                                    codedWorldInfo.add("Time: " + wi.getTime() / 3600_000);
                                    codedWorldInfo.add(wi.getWorldGenerator() + "-" + wi.getSeed());
                                }
                                return Joiner.on('\n').join(codedWorldInfo);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace(); // never!
                            }
                        } else {
                            return "requested";
                        }
                    }
                    return null;
                }
            });

            UILabel name = find("name", UILabel.class);
            name.bindText(BindHelper.bindBoundBeanProperty("name", infoBinding, ServerInfo.class, String.class));

            UILabel address = find("address", UILabel.class);
            address.bindText(BindHelper.bindBoundBeanProperty("address", infoBinding, ServerInfo.class, String.class));

            UILabel port = find("port", UILabel.class);
            port.bindText(new IntToStringBinding(BindHelper.bindBoundBeanProperty("port", infoBinding, ServerInfo.class, int.class)));

            WidgetUtil.trySubscribe(this, "add", new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget button) {
                    getManager().pushScreen(AddServerPopup.ASSET_URI);
                }
            });
            WidgetUtil.trySubscribe(this, "edit", new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget button) {
//                        AddServerPopup popup = getManager().pushScreen(AddServerPopup.ASSET_URI, AddServerPopup.class);
//                        popup.setServerInfo(infoBinding.get());
                        ServerInfo item = serverList.getSelection();
                        if (!extInfo.containsKey(item)) {
                            Future<ServerInfoMessage> futureInfo = networkSystem.requestInfo(item.getAddress(), item.getPort());
                            extInfo.put(item, futureInfo);
                        }
                    }
                });
            WidgetUtil.trySubscribe(this, "remove", new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget button) {
                    if (serverList.getSelection() != null) {
                        config.getNetwork().remove(serverList.getSelection());
                        serverList.setSelection(null);
                    }
                }
            });
            WidgetUtil.trySubscribe(this, "join", new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget button) {
                    config.save();
                    ServerInfo item = serverList.getSelection();
                    if (item != null) {
                        join(item.getAddress(), item.getPort());
                    }
                }
            });

            Binding<Boolean> hasSelection = new ReadOnlyBinding<Boolean>() {

                @Override
                public Boolean get() {
                    return infoBinding.get() != null;
                }
            };

            UIButton editButton = find("edit", UIButton.class);
            UIButton removeButton = find("remove", UIButton.class);
            UIButton joinButton = find("join", UIButton.class);

            editButton.bindEnabled(hasSelection);
            removeButton.bindEnabled(hasSelection);
            joinButton.bindEnabled(hasSelection);
        }

        WidgetUtil.trySubscribe(this, "close", new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget button) {
                config.save();
                getManager().popScreen();
            }
        });
    }

    @Override
    public boolean isLowerLayerVisible() {
        return false;
    }

    private void join(final String address, final int port) {
        Callable<JoinStatus> operation = new Callable<JoinStatus>() {

            @Override
            public JoinStatus call() throws InterruptedException {
                JoinStatus joinStatus = networkSystem.join(address, port);
                return joinStatus;
            }
        };

        final WaitPopup<JoinStatus> popup = getManager().pushScreen(WaitPopup.ASSET_URI, WaitPopup.class);
        popup.setMessage("Join Game", "Connecting to '" + address + ":" + port + "' - please wait ...");
        popup.onSuccess(new Function<JoinStatus, Void>() {

            @Override
            public Void apply(JoinStatus result) {
                if (result.getStatus() != JoinStatus.Status.FAILED) {
                    engine.changeState(new StateLoading(result));
                } else {
                    MessagePopup screen = getManager().pushScreen(MessagePopup.ASSET_URI, MessagePopup.class);
                    screen.setMessage("Failed to Join", "Could not connect to server - " + result.getErrorMessage());
                }
                return null;
            }
        });
        popup.startOperation(operation, true);

    }
}
