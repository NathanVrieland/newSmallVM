OUT "storing integer 23..."
STO num1 23
; STO just gonna drop a comment here
OUT "success, outputting..."
OUT num1
OUT "getting integer from user"
IN num2
OUT "success, outputting..."
OUT num2
OUT "storing num2 in num1 and outputting..."
STO num1 num2
OUT num1
OUT "adding them..."
STO num1 23
ADD num3 num1 num2
OUT num3
OUT "adding literals 20 + 20"
ADD num3 20 20
OUT num3
OUT "adding literal 20 to num1 (23)"
ADD num3 20 num1
OUT num3
OUT "quick test of MUL, DIV, and SUB of num1 and num2"
MUL num3 num1 num2
OUT num3
SUB num3 num1 num2
OUT num3
DIV num3 num1 num2
OUT num3
DUMP
