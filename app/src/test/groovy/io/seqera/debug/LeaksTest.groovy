package io.seqera.debug

import java.time.Duration
import java.time.Instant

import spock.lang.Specification

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class LeaksTest extends Specification {

    def 'should find all leaks older than specified duration' (){
        given:
        def all = [
                new Context('foo', 1000, Instant.now().minusMillis(20_000), null),
                new Context('foo', 1000, Instant.now().minusMillis(10_000), null),
                new Context('foo', 1000, Instant.now().minusMillis(1_000), null),
        ]

        when:
        def list1 = Leaks.findOlderThan(all, Duration.ofSeconds(10))
        then:
        list1.size() == 2
        list1[0] == all[0]
        list1[1] == all[1]

        when:
        def list2 = Leaks.findOlderThan(all, Duration.ofSeconds(15))
        then:
        list2.size() == 1
        list2[0] == all[0]

        when:
        def list3 = Leaks.findOlderThan(all, Duration.ofSeconds(30))
        then:
        list3.size() == 0
    }

}
