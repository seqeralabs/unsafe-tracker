java \
  -javaagent:$PWD/app/build/libs/unsafe-tracker-0.1.0.jar \
  -Xbootclasspath/a:$PWD/app/build/libs/unsafe-tracker-0.1.0.jar:./app/build/classes/groovy/test \
  -XX:-Inline \
  io.seqera.debug.UnsafeTest