package assembler

import spock.lang.Specification

class InstructionSpecification extends Specification {
    def assembler = new Assembler()

    def "Should interpret instructions"() {
        when:
        def code = Instruction.codes.get(command)
        def interpretedName = Instruction.instructions.get(code).name

        then:
        interpretedName == command

        where:
        command << ["ADD", "ADC", "INC", "SUB", "DEC", "MUL", "DIV", "MOV", "AND", "NAND", "OR", "NOR", "XOR", "NOP"]
    }

    def "should not throw exception when validating valid instruction line: #line"() {
        when:
        Instruction.validate(line, assembler)

        then:
        noExceptionThrown()

        where:
        line << ["ADD AX FFH", "ADD [51] AL", "INC BX",
                 "SUB AX FFH", "SUB [51] AL", "DEC BX",
                 "MUL AX FFH", "MUL [51] AL", "MUL AL BH",
                 "DIV AX FFH", "DIV AX [51]",
                 "MOV AL 01H", "MOV DL 05H" , "MOV [6F] DX",
                 "AND CX 11H", "NAND AL 05H", "OR AX [F5]", "NOR DX 4AH", "XOR BL CH",
                 "NOP"]

    }

    def "should throw exception when validating invalid instruction line: #line"() {
        when:
        Instruction.validate(line, assembler)

        then:
        thrown(Exception)

        where:
        line << ["ADD F0H FFH", "ADD [51]", "INC FF",
                 "SIB AX FFH", "SUB 51H [A0]", "DCR BX",
                 "MULL AX FFH", "MUL [51sgsg] AL", "MUL AL BH FFH",
                 "DIV AL FFH", "DIV [51] AL",
                 "MOV [5FB] 01H", "MOVx DL 05H" , "MOV 6FH DX",
                 "AND [CX] 11H", "NAND ALH 05H", "OR 01H [F5]", "NOR DXH 4A", "XOR BL CHH",
                 "NOP AL"]

    }
}
