java \
  -javaagent:$PWD/build/libs/unsafe-tracker-0.1.0.jar \
  -Xbootclasspath/a:$PWD/build/libs/unsafe-tracker-0.1.0.jar \
  -cp ./build/classes/groovy/test \
  io.seqera.debug.UnsafeTest