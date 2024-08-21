package io.seqera.debug

import java.time.Instant

import spock.lang.Specification

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class AllocationContextTest extends Specification {

    def 'should create alloc context' () {
        when:
        def c1 = AllocationContext.create('foo', 1, 2)

        then:
        c1.name == 'foo'
        c1.size == 1
        c1.address == 2
        c1.createdAt.compareTo(Instant.now())<=0
        c1.stackTrace.startsWith('  java.base/jdk.internal.reflect.NativeMethodAccessorImpl')
    }

}
