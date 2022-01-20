package com.loohp.interactivechatdiscordsrvaddon.metrics;

import java.util.concurrent.Callable;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import github.scarsz.discordsrv.dependencies.jda.api.JDA;

public class Charts {
	
	public static void setup(Metrics metrics) {
		
		metrics.addCustomChart(new Metrics.SingleLineChart("discord_servers_present", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	JDA jda = InteractiveChatDiscordSrvAddon.discordsrv.getJda();
            	if (jda == null) {
            		return 0;
            	}
                return jda.getGuilds().size();
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("discord_channels_present", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	JDA jda = InteractiveChatDiscordSrvAddon.discordsrv.getJda();
            	if (jda == null) {
            		return 0;
            	}
                return jda.getGuilds().stream().mapToInt(each -> each.getChannels().size()).sum();
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_discord_members", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	JDA jda = InteractiveChatDiscordSrvAddon.discordsrv.getJda();
            	if (jda == null) {
            		return 0;
            	}
                return jda.getUsers().size();
            }
        }));
		
		metrics.addCustomChart(new Metrics.SimplePie("item_image_view_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.itemImage) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("item_image_tooltip_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImage) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("inventory_image_view_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.invImage) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("enderchest_image_view_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.enderImage) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("hoverevent_display_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("hoverevent_image_tooltip_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.hoverUseTooltipImage) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("discord_images_preview_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("show_death_message_weapon", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.deathMessageItem) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("correct_advancement_name", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.advancementName) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("show_advancement_item_icon", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.advancementItem) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("show_advancement_description", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.advancementDescription) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SimplePie("translate_mentions_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (InteractiveChatDiscordSrvAddon.plugin.translateMentions) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("resource_packs_installed", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Math.max(0, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getResourcePackInfo().size() - 1);
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_messages_processed_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.messagesCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_images_created_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.imageCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_inventory_images_created_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_discord_attachments_processed_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.attachmentCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_discord_images_processed_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.attachmentImageCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("total_image_maps_viewed_per_interval", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	long amount = InteractiveChatDiscordSrvAddon.plugin.imagesViewedCounter.getAndSet(0);
                return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("servers_rendering_player_models_with_hand_items", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView && InteractiveChatDiscordSrvAddon.plugin.renderHandHeldItems ? 1 : 0;
            }
        }));
		
		metrics.addCustomChart(new Metrics.SingleLineChart("servers_combined_average_pmwhh_rendering_times", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                long combined = 0;
                Integer i;
                int counter = 0;
                while ((i = InteractiveChatDiscordSrvAddon.plugin.playerModelRenderingTimes.poll()) != null) {
                	combined += i;
                	counter++;
                }
                return (int) ((double) combined / (double) counter);
            }
        }));
		
	}

}
