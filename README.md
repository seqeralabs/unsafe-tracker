# unsafe-tracker

Java agent that tracks the access to off-heap memory made by an application by 
using  `Unsafe` class.

The goal of this project is being able to track usage of off-heap memory and detect potential memory leaks.

The detection is made via a Java agent implemented by using [Byte Buddy](https://bytebuddy.net/) library.


### Get started 

1. Compile the project using this command 

    ```
    ./gradlew check shadowJar
    ```

2. Run it using this script: 

    ```
    bash run.sh
    ```
