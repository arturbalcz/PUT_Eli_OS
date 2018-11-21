package assembler

import processess.PCB
import shell.Shell
import spock.lang.Specification
import spock.lang.Unroll

class AssemblerSpecification extends Specification {
    def assembler = new Assembler()

    PCB pcb = Mock()

    def setup() {
        // clear registers
        Assembler.cpu.A.set(AssemblerUtils.emptyRegistry())
        Assembler.cpu.B.set(AssemblerUtils.emptyRegistry())
        Assembler.cpu.C.set(AssemblerUtils.emptyRegistry())
        Assembler.cpu.D.set(AssemblerUtils.emptyRegistry())

        // clear flags
        Assembler.cpu.setCF(false)
        Assembler.cpu.setZF(false)
    }

    @Unroll
    def "MOV - should copy given value to selected register"() {
        when:
        assembler.mov(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value || result
        CPU.AH_ID | 1     || [true, false, false, false]
        CPU.BL_ID | 1     || [true, false, false, false]
        CPU.CX_ID | 245   || [true, false, true, false, true, true, true, true]
        CPU.DX_ID | 42    || [false, true, false, true, false, true, false, false]
    }

    @Unroll
    def "MOV - should copy contents from one registry to another" () {
        given:
        assembler.cpu.A.set([true, false, true, false, true, true, true, true] as boolean[])
        assembler.cpu.B.setL([false, true, true, false] as boolean[])

        when:
        assembler.mov(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.REGISTRY.ordinal() as byte,
                source as byte,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry) == assembler.cpu.getRegistryById(source)

        where:
        registry      | source        || result
        CPU.CH_ID | CPU.BL_ID || [true, false, false, false]
        CPU.DL_ID | CPU.BL_ID || [true, false, false, false]
        CPU.DX_ID | CPU.AX_ID || [true, false, true, false, true, true, true, true]
    }

    @Unroll
    def "ADD - should add given value to selected register without carry"() {
        given:
        assembler.cpu.A.set([true, false, true, false, true, true, true, true] as boolean[])
        assembler.cpu.D.set([true, false, false, false, true, true, false, true] as boolean[])

        when:
        assembler.add(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                false,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value || result
        CPU.AH_ID | 1     || [false, false, false, false]
        CPU.BL_ID | 1     || [true, false, false, false]
        CPU.CX_ID | 245   || [true, false, true, false, true, true, true, true]
        CPU.DX_ID | 42    || [true, true, false, true, true, false, true, true]
    }

    @Unroll
    def "ADD - should add value of given registry to selected registry without carry"() {
        given:
        assembler.cpu.A.set([true, false, true, false, true, true, true, true] as boolean[])
        assembler.cpu.C.set([true, false, false, false, false, false, false, false] as boolean[])
        assembler.cpu.D.set([true, false, false, false, true, false, false, true] as boolean[])

        when:
        assembler.add(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.REGISTRY.ordinal() as byte,
                value as byte,
                false,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value         || result
        CPU.BX_ID | CPU.AX_ID || [true, false, true, false, true, true, true, true]
        CPU.AH_ID | CPU.CL_ID || [false, false, false, false]
        CPU.AL_ID | CPU.DH_ID || [false, true, true, true]
    }

    @Unroll
    def "ADC - should add given value to selected register with carry"() {
        given:
        assembler.cpu.A.set([true, true, true, true, true, true, true, true] as boolean[])
        assembler.cpu.B.set([false, false, false, false, false, false, false, false] as boolean[])

        when:
        assembler.add(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                CPU.AX_ID,
                ArgumentTypes.VALUE.ordinal() as byte,
                5 as byte,
                false,
                pcb
        )
        assembler.add(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                CPU.BX_ID,
                ArgumentTypes.VALUE.ordinal() as byte,
                1 as byte,
                true,
                pcb
        )

        then:
        assembler.cpu.getRegistryById(CPU.BX_ID).toList() == [false, true, false, false, false, false, false, false] as boolean[]
    }

    @Unroll
    def "SUB - should subtract given value from selected registry"() {
        given:
        assembler.cpu.A.set([true, false, true, false, true, true, true, true] as boolean[])
        assembler.cpu.B.set([true, false, false, false, true, true, false, true] as boolean[])
        assembler.cpu.C.set([true, false, false, false, true, true, false, true] as boolean[])

        when:
        assembler.sub(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )

        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value || result
        CPU.AL_ID | 1     || [false, false, true, false]
        CPU.BX_ID | 7     || [false, true, false, true, false, true, false, true]
        CPU.CX_ID | 181   || [false, false, true, true, true, true, true, true] // B5H
    }

    def"INC - should increment value of given registry"() {
        given:
        assembler.cpu.A.set([true, true, true, false, true, true, true, true] as boolean[])

        when:
        assembler.inc(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                CPU.AX_ID,
                pcb
        )

        then:
        assembler.cpu.A.get() == [false, false, false, true, true, true, true, true] as boolean[]
    }

    def"DEC - should decrement value of given registry"() {
        given:
        assembler.cpu.A.set([true, true, true, false, true, true, true, true] as boolean[])

        when:
        assembler.dec(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                CPU.AX_ID,
                pcb
        )

        then:
        assembler.cpu.A.get() == [false, true, true, false, true, true, true, true] as boolean[]
    }

    @Unroll
    def "MUL - should multiply value of given registry"() {
        given:
        assembler.cpu.A.set([true, false, true, false, true, true, true, false] as boolean[])
        assembler.cpu.D.set([true, false, false, false, false, true, false, false] as boolean[])

        when:
        assembler.mul(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value || result
        CPU.AX_ID | 2     || [false, true, false, true, false, true, true, true]
        CPU.BL_ID | 100   || [false, false, false, false]
        CPU.DX_ID | 5     || [true, false, true, false, false, true, false, true]
    }

    @Unroll
    def "DIV - should multiply value of divide registry"() {
        given:
        assembler.cpu.A.set([false, true, false, true, false, false, false, false] as boolean[])
        assembler.cpu.B.set([false, false, false, false, false, false, false, false] as boolean[])
        assembler.cpu.C.set([false, false, true, false, false, true, true, false] as boolean[])

        when:
        assembler.div(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )
        then:
        assembler.cpu.getRegistryById(registry).toList() == result

        where:
        registry      | value || result
        CPU.AX_ID | 2     || [false, false, false, false, true, false, true, false]
        CPU.BX_ID | 100   || [false, false, false, false, false, false, false, false]
        CPU.CX_ID | 13    || [true, false, false, true, true, true, true, false]
    }

    @Unroll
    def "AND - should perform logical AND on registry and given value"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])

        when:
            Assembler.and(
                    ArgumentTypes.REGISTRY.ordinal() as byte,
                    registry as byte,
                    ArgumentTypes.VALUE.ordinal() as byte,
                    value as byte,
                    pcb
            )

        then:
            Assembler.cpu.getRegistryById(registry) == result

        where:
        registry      | value || result
        CPU.AX_ID | 170   || [false, false, false, false, false, false, false, false] as boolean[] // AAH
        CPU.AX_ID | 85    || [true, false, true, false, true, false, true, false] as boolean[] // 55H
        CPU.BX_ID | 255   || [false, false, false, false, false, false, false, false] as boolean[] // FFH
    }

    @Unroll
    def "NAND - should perform logical NAND on registry and given value"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])

        when:
        Assembler.nand(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )

        then:
        Assembler.cpu.getRegistryById(registry) == result

        where:
        registry      | value || result
        CPU.AX_ID | 170   || [true, true, true, true, true, true, true, true] as boolean[] // AAH
        CPU.AX_ID | 85    || [false, true, false, true, false, true, false, true] as boolean[] // 55H
        CPU.BX_ID | 255   || [true, true, true, true, true, true, true, true] as boolean[] // FFH
    }

    @Unroll
    def "OR - should perform logical OR on registry and given value"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])

        when:
        Assembler.or(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )

        then:
        Assembler.cpu.getRegistryById(registry) == result

        where:
        registry      | value || result
        CPU.AX_ID | 170   || [true, true, true, true, true, true, true, true] as boolean[] // AAH
        CPU.AX_ID | 85    || [true, false, true, false, true, false, true, false] as boolean[] // 55H
        CPU.BX_ID | 255   || [true, true, true, true, true, true, true, true] as boolean[] // FFH
    }

    @Unroll
    def "NOR - should perform logical NOR on registry and given value"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])

        when:
        Assembler.nor(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )

        then:
        Assembler.cpu.getRegistryById(registry) == result

        where:
        registry      | value || result
        CPU.AX_ID | 170   || [false, false, false, false, false, false, false, false] as boolean[] // AAH
        CPU.AX_ID | 85    || [false, true, false, true, false, true, false, true] as boolean[] // 55H
        CPU.BX_ID | 255   || [false, false, false, false, false, false, false, false] as boolean[] // FFH
    }

    @Unroll
    def "XOR - should perform logical XOR on registry and given value"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])

        when:
        Assembler.xor(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                ArgumentTypes.VALUE.ordinal() as byte,
                value as byte,
                pcb
        )

        then:
        Assembler.cpu.getRegistryById(registry) == result

        where:
        registry      | value || result
        CPU.AX_ID | 170   || [true, true, true, true, true, true, true, true] as boolean[] // AAH
        CPU.AX_ID | 85    || [false, false, false, false, false, false, false, false] as boolean[] // 55H
        CPU.BX_ID | 255   ||  [true, true, true, true, true, true, true, true] as boolean[] // FFH
    }

    @Unroll
    def "NOT - should perform logical NOT on given registry"() {
        given:
        Assembler.cpu.A.set([true, false, true, false, true, false, true, false] as boolean[])
        def registry = CPU.AX_ID as byte

        when:
        Assembler.not(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry,
                pcb
        )

        then:
        Assembler.cpu.getRegistryById(registry) == [false, true, false, true, false, true, false, true] as boolean[]
    }

    def "JMP - should perform unconditional jump to selected memory address"() {
        given:
        def address = 10 as byte
        def pcb = Mock(PCB)

        when:
        Assembler.jmp(address, pcb)

        then:
        1 * pcb.setPC(address)
    }

    @Unroll
    def "JNZ - should perform jump to selected memory address if ZF is set"() {
        given:
        def address = 10 as byte
        def pcb = Mock(PCB)

        when:
        Assembler.cpu.setZF(zf)
        Assembler.jnz(address, pcb)

        then:
        changedPC * pcb.setPC(address)

        where:
        zf    || changedPC
        true  || 0
        false || 1
    }

    @Unroll
    def "Should trigger the ZERO FLAG when result of operation is ZERO"() {
        given:
        Assembler.cpu.A.set([true, true, true, true, true, true, true, true] as boolean[])
        Assembler.cpu.B.set([true, false, true, false, true, true, true, true] as boolean[])

        when:
        Assembler.inc(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                pcb
        )

        then:
        Assembler.cpu.ZF == result

        where:
        registry      || result
        CPU.AX_ID || true
        CPU.BX_ID || false
    }

    @Unroll
    def "Should trigger the CARRY FLAG when result of operation doesn't fit in register"() {
        given:
        Assembler.cpu.A.set([true, true, true, true, true, true, true, true] as boolean[])
        Assembler.cpu.B.set([true, false, true, false, true, true, true, true] as boolean[])

        when:
        Assembler.inc(
                ArgumentTypes.REGISTRY.ordinal() as byte,
                registry as byte,
                pcb
        )

        then:
        Assembler.cpu.CF == result

        where:
        registry      || result
        CPU.AX_ID || true
        CPU.BX_ID || false
    }

//    def "Should execute sample program"() {
//
//    }
}

