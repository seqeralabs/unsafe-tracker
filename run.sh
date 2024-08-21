java \
  -javaagent:$PWD/build/libs/unsafe-tracker-$(cat VERSION).jar \
  -Xbootclasspath/a:$PWD/build/libs/unsafe-tracker-$(cat VERSION).jar \
  -cp ./build/classes/groovy/test \
  io.seqera.debug.UnsafeTest
