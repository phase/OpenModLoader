# Open Mod Loader
A mod loader and compatibility API for Minecraft. The project is currently in its infancy however huge amounts of progress is being made. The primary goal of Open Mod Loader is to provide a clean and efficient way to mod Minecraft. We currently have a [Discord server](https://discordapp.com/invite/0155YJdCqhGXB8ARX) where development is being discussed, everyone is welcome to join. 

# Setup: IntelliJ
Warning: Setup for OpenModLoader is not yet fully automated. As the project further develops the process will become much more streamlined, but for now a tedious setup process is required. This process is for setting up Open Mod Loader as a project developer and should not be used for writing your own mods. There is currently no setup for authoring mods. 

1. Clone this repository using `git clone https://github.com/OpenModLoader/OpenModLoader` or another preferred method.
2. Run `gradlew setupOML genIdeaRuns --refresh-dependencies` in the cloned repository folder.
3. Open IntelliJ on the repository folder.
4. You should now be able to run Open Mod Loader using the pre installed run configuration.

# Setup: Eclipse
Warning: Setup for OpenModLoader is not yet fully automated. As the project further develops the process will become much more streamlined, but for now a tedious setup process is required. This process is for setting up Open Mod Loader as a project developer and should not be used for writing your own mods. There is currently no setup for authoring mods. 

1. Clone this repository using `git clone https://github.com/OpenModLoader/OpenModLoader` or another preferred method.
2. Run `gradlew setupOML extractNatives eclipse` in the cloned repository folder.
3. Open Eclipse and import the cloned directory as a project.
4. Add `work/src` as a source folder.
5. Add `.gradle/minecraft/libs` to your build path.
6. Add `.gradle/minecraft/natives` to the natives on LWJGL
7. The project should now build without errors. If anything is missing you can download the libraries from Mojang directly.
8. Create a new run configuration with the main class set to `xyz.openmodloader.launcher.OpenModLoaderClient`
9. You should now be able to run Open Mod Loader.
