package com.ryu.brainalarm.util


class MathProblem {
    enum class Operators{
        PLUS, MINUS, PRODUCT, DIVISION
    }
    //수학문제 생성
    private var operand1 : Int = 0
    private var operand2 : Int = 0
    private var operator = ""
    private var result : Int = 0


    init {
        val operatorValue = (0..3).random()
        var op1 = 0
        var op2 = 0
        var resultValue = 0
        when(operatorValue){
            Operators.PLUS.ordinal -> {
                op1 = (10..999).random()
                op2 = (10..99).random()
                resultValue = op1+op2
                operator = "+"
            }
            Operators.MINUS.ordinal -> {
                op1 = (10..999).random()
                op2 = (10..99).random()
                if(op1 < op2){ // Swap operands
                    op1 += op2
                    op2 = op1 - op2
                    op1 -= op2
                }
                resultValue = op1 - op2
                operator = "-"
            }
            Operators.PRODUCT.ordinal -> {
                op1 = (10..99).random()
                op2 = (1..9).random()
                resultValue = op1 * op2
                operator = "x"
            }
            Operators.DIVISION.ordinal -> {
                op1 = (1..9).random()
                op2 = (1..9).random()
                op1 *= op2
                resultValue = op1 / op2
                operator = "/"
            }
        }
        operand1 = op1
        operand2 = op2
        result = resultValue
    }

    fun getOperand1() = operand1
    fun getOperand2() = operand2
    fun getOperator() = operator
    fun getResult() = result

}