# mc-image

Compile Minecraft to a native executable using [GraalVM native-image](https://www.graalvm.org/native-image/)!

## Build instructions

1. Open project in IntelliJ
2. Run "prepare" run configuration
3. Press "Build project"
4. Run `build.sh`

After a few minutes, a `minecraft-client` executable will have been produced.
You can run it using `run.sh`, which passes a few arguments Minecraft requires.
