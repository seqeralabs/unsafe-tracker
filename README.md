# unsafe-tracker

Java agent that tracks the access to off-heap memory made by an application by 
using  `Unsafe` class.

The goal of this project is being able to track usage of off-heap memory and detect potential memory leaks.

The detection is made via a Java agent implemented by using [Byte Buddy](https://bytebuddy.net/) library.


### Get started

1. Create the agent jar file using this command:

    ```
    ./gradlew jar
    ```
    `

2. Launch the agent along with your Java application by adding these options
to the Java command line:

```
  -javaagent:$PWD/build/libs/unsafe-tracker-<VERSION>.jar \
  -Xbootclasspath/a:$PWD/build/libs/unsafe-tracker-<VERSION>.jar \`
```


### Development

1. Compile the project using this command 

    ```
    ./gradlew check shadowJar
    ```

2. Run it using this script: 

    ```
    bash run.sh
    ```


### Credits 

Thanks to Rafael Winterhalter for advising about the use of the [Byte Buddy](https://github.com/raphw/byte-buddy).
