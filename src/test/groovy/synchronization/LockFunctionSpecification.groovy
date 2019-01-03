package synchronization

import processess.PCB
import processess.ProcessState
import spock.lang.Specification

class LockFunctionSpecification extends Specification {
    def lockFunction = new LockFunction()

    PCB p1 = Mock()
    PCB p2 = Mock()

    def setup() {
        p1.PID >> 1
        p1.name >> "p1"

        p1.PID >> 2
        p1.name >> "p2"
    }

    def "should lock" () {
        when:
        lockFunction.lock(p1)

        then:
        lockFunction.locked
        0 * p1.setState(_)
        lockFunction.kolejka == []
    }

    def "should lock for specified process" () {
        when:
        lockFunction.lock(p1)
        lockFunction.lock(p2)

        then:
        lockFunction.locked
        1 * p2.setState(ProcessState.WAITING)
        lockFunction.kolejka == [p2]
    }

    def "should unlock" () {
        given:
        lockFunction.locked = true
        lockFunction.kolejka = []

        when:
        lockFunction.unlock()

        then:
        !lockFunction.locked
        0 * p1.setState(_)
        lockFunction.kolejka == []
    }

    def "should unlock and lock for waiting process" () {
        given:
        lockFunction.locked = true
        lockFunction.kolejka = [p2]

        when:
        lockFunction.unlock()

        then:
        lockFunction.locked
        0 * p1.setState(_)
        1 * p2.setState(ProcessState.READY)
        lockFunction.kolejka == []
    }

    def "should signal that process is ready for execution" () {
        given:
        lockFunction.kolejka = [p1]

        when:
        lockFunction.signal()

        then:
        1 * p1.setState(ProcessState.READY)
        lockFunction.kolejka == []
    }

}
