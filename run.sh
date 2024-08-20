java \
  -javaagent:$PWD/app/build/libs/unsafe-tracker-0.1.0.jar \
  -cp ./app/build/classes/groovy/test \
  io.seqera.debug.UnsafeTest