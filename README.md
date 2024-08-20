# unsafe-tracker

JVM agent that track access to off-heap memory via Unsafe class (DRAFT).

The goal of this project is being able to track usage of off-heap memory made by a Java application
via `sun.misc.Unsafe` and detect potential memory leaks.

The detection is made via a Java instrumentation implemented via [Byte Buddy](https://bytebuddy.net/) agent.


### Get started 

1. Compile the project using this command 

    ```
    ./gradlew check shadowJar
    ```

2. Run it using this script: 

    ```
    bash run.sh
    ```
