package org.mtr.mod;

import com.jonafanho.apitools.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModUpload {

	private static final String[] MINECRAFT_VERSIONS = {"1.16.5", "1.17.1", "1.18.2", "1.19.2", "1.19.4", "1.20.1", "1.20.4"};


	public static void main(String[] args) throws IOException {
		String changelog = "## **更新日志**\n" +
				"\n" +
				"> 本 Mod 依赖 **Minecraft Transit Railway 4.0.0 及以上**\n" +
				"> 安装本Mod前，请备份您的存档。\n" +
				"\n" +
				"---\n" +
				"### **修复内容**\n" +
				"* 修复使用较低版本的Fabric Loader无法启动游戏的问题\n" +
				"\n" +
				"---\n" +
				"\n" +
				"## **Change Log**\n" +
				"\n" +
				"> This Mod requires **Minecraft Transit Railway 4.0.0 or above.**\n" +
				"> Please back up your save before installing this Mod.\n" +
				"\n" +
				"---\n" +
				"### **Bug Fixes**\n" +
				"* Fixed the issue where the game could not start when using an older version of Fabric Loader.\n" +
				"\n" +
				"---";





		if (args.length == 2) {
			for (final String minecraftVersion : MINECRAFT_VERSIONS) {
				for (final ModLoader modLoader : ModLoader.values()) {
					final String modVersion = String.format("%s-%s+%s", modLoader.name, args[0], minecraftVersion);
					final String modVersionUpperCase = String.format("%s-%s+%s", modLoader.name.toUpperCase(Locale.ENGLISH), args[0], minecraftVersion);
					final String fileName = String.format("Yunzhu-Transit-Extension-%s.jar", modVersion);
					final Path filePath = Paths.get("build/release").resolve(fileName);



//					 //Modrinth
//					final Map<String, DependencyType> dependenciesModrinth = new HashMap<String, DependencyType>();
//					dependenciesModrinth.put("XKPAmI6u", DependencyType.REQUIRED);
//					do {
//					} while (!new ModId("nqMdKn6A", ModProvider.MODRINTH).uploadFile(
//							modVersionUpperCase,
//							modVersionUpperCase,
//							changelog,
//							dependenciesModrinth,
//							ReleaseStatus.BETA,
//							Collections.singleton(minecraftVersion),
//							Collections.singleton(modLoader),
//							false,
//							Files.newInputStream(filePath),
//							fileName,
//							args[1]
//					));

					 //CurseForge
					final Map<String, DependencyType> dependenciesCurseForge = new HashMap<>();
					dependenciesCurseForge.put("minecraft-transit-railway", DependencyType.REQUIRED);//mtr依赖
					do {
					} while (!new ModId("1421375", ModProvider.CURSE_FORGE).uploadFile(
							"",
							modVersionUpperCase,
							changelog,
							dependenciesCurseForge,
							ReleaseStatus.BETA,
							Collections.singleton(minecraftVersion),
							Collections.singleton(modLoader),
							false,
							Files.newInputStream(filePath),
							fileName,
							args[1]
					));
				}
			}
		}
	}
}
